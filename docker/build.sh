#!/usr/bin/env bash
# Turquaz Standalone — Resurrected build orchestrator
#
# Mounts:
#   /src   — read-only source tree (branch root with TurquazCommon/, TurquazClient/, ...)
#   /out   — output dir for packaged artifacts (.tar.gz / .zip)
#
# Env (override at `docker run -e KEY=value`):
#   TARGETS       — virgülle ayrılmış hedef OS listesi
#   SWT_VERSION   — Maven Central'dan çekilen org.eclipse.swt.<platform> sürümü
#   ENCODING      — kaynak kod karakter kodlaması (Türkçe için ISO-8859-9)
#
# Çıktı:
#   /out/turquaz-standalone-resurrected-<target>.tar.gz (linux/macos)
#   /out/turquaz-standalone-resurrected-<target>.zip   (windows)

set -euo pipefail

SRC=${SRC:-/src}
OUT=${OUT:-/out}
SWT_VERSION=${SWT_VERSION:-3.124.0}
TARGETS=${TARGETS:-linux,linux-aarch64,windows,macos,macos-aarch64}
ENCODING=${ENCODING:-ISO-8859-9}

declare -A SWT_ART=(
    [linux]="org.eclipse.swt.gtk.linux.x86_64"
    [linux-aarch64]="org.eclipse.swt.gtk.linux.aarch64"
    [windows]="org.eclipse.swt.win32.win32.x86_64"
    [macos]="org.eclipse.swt.cocoa.macosx.x86_64"
    [macos-aarch64]="org.eclipse.swt.cocoa.macosx.aarch64"
)
declare -A ARCHIVE_EXT=(
    [linux]=tar.gz
    [linux-aarch64]=tar.gz
    [windows]=zip
    [macos]=tar.gz
    [macos-aarch64]=tar.gz
)

log() { printf '==> %s\n' "$*"; }
sub() { printf '    %s\n' "$*"; }
err() { printf 'ERROR: %s\n' "$*" >&2; }

# ---------------------------------------------------------------------------
# 0. Sanity
# ---------------------------------------------------------------------------
for mod in TurquazCommon TurquazServer TurquazBusinessLogic TurquazStandAlone TurquazClient; do
    if [[ ! -d "$SRC/$mod/src" ]]; then
        err "Kaynak modülü bulunamadı: $SRC/$mod/src"
        err "Branch kökünü /src'e mount ettiğinden emin ol (örn: -v \$PWD:/src:ro)."
        exit 1
    fi
done

mkdir -p "$OUT"

WORK=$(mktemp -d)
trap 'rm -rf "$WORK"' EXIT

log "Çalışma dizini: $WORK"
log "Hedefler:       $TARGETS"
log "SWT sürümü:     $SWT_VERSION"
log "Kodlama:        $ENCODING"

cp -a \
    "$SRC/TurquazCommon" \
    "$SRC/TurquazServer" \
    "$SRC/TurquazBusinessLogic" \
    "$SRC/TurquazStandAlone" \
    "$SRC/TurquazClient" \
    "$WORK/"
cd "$WORK"

# Eclipse projelerinde bazen dosya sistemi kalıntısı olarak CVS/ klasörleri
# kalmış oluyor; build'in temiz olması için silinir.
find . -type d -name CVS -prune -exec rm -rf {} +

# 2005 dağıtımıyla gelmiş eski turquaz-*.jar binary'lerini at — biz onları
# kaynaktan tekrar derliyoruz. Aksi takdirde javac classpath'inde modül
# bin/'lerinden önce gelip kaybolmuş metot referansı hatasına yol açar.
find . -path "*/lib/turquaz-*.jar" -delete

# Java 9+ modüler runtime patch'i:
#   Starter.java orijinalde `new URLClassLoader(urls, null)` kullanıyor — yani
#   parent class loader yok. Java 8'de bootstrap rt.jar'ı kapsıyordu, Java 9+
#   ise java.sql gibi modüller PLATFORM class loader'a taşındı; null parent
#   onları görmüyor → ClassNotFoundException: java.sql.SQLException
#   Sistem class loader'a delege ederek düzeltiyoruz.
sed -i 's|new URLClassLoader((URL\[\]) jars\.toArray(new URL\[jars\.size()\]),null);|new URLClassLoader((URL[]) jars.toArray(new URL[jars.size()]), ClassLoader.getSystemClassLoader());|' \
    TurquazStandAlone/src/run/Starter.java
if ! grep -q 'ClassLoader\.getSystemClassLoader()' TurquazStandAlone/src/run/Starter.java; then
    err "Starter.java patch'i uygulanamadı (kaynak değişmiş olabilir)"
    exit 1
fi
sub "Starter.java URLClassLoader parent patch'i uygulandı"

# ---------------------------------------------------------------------------
# 1. Derleme (Common → Server → BusinessLogic → StandAlone → Client)
# ---------------------------------------------------------------------------
log "5 modül derleniyor (javac, -source 8 -target 8, $ENCODING)"

JAVAC_OPTS=(-source 8 -target 8 -encoding "$ENCODING" -nowarn -Xlint:none)

# 1a. TurquazCommon — temel sabitler, i18n, HTTP servis req/resp helper'ları
mkdir -p TurquazCommon/bin
COMMON_CP=$(printf '%s:' \
    TurquazStandAlone/lib/dom4j-1.6.jar \
    TurquazStandAlone/lib/hibernate3.jar \
    TurquazClient/lib/jdom.jar \
    TurquazClient/lib/log4j-1.2.8.jar \
    TurquazClient/lib/commons-codec-1.2.jar \
    TurquazClient/lib/lzma/JLzma.jar)
javac "${JAVAC_OPTS[@]}" -cp "${COMMON_CP%:}" -d TurquazCommon/bin \
    $(find TurquazCommon/src -name '*.java')
sub "Common:        $(find TurquazCommon/bin -name '*.class' | wc -l) sınıf"

# 1b. TurquazServer — servlet katmanı + servis mapper
mkdir -p TurquazServer/bin
SERVER_CP="TurquazCommon/bin"
for j in TurquazServer/lib/*.jar TurquazServer/lib/hibernate/*.jar TurquazServer/lib/jasperreport/*.jar; do
    [[ -f "$j" ]] && SERVER_CP="$SERVER_CP:$j"
done
javac "${JAVAC_OPTS[@]}" -cp "$SERVER_CP" -d TurquazServer/bin \
    $(find TurquazServer/src -name '*.java')
sub "Server:        $(find TurquazServer/bin -name '*.class' | wc -l) sınıf"

# 1c. TurquazBusinessLogic — XxxBL + XxxDAL iş kuralları
mkdir -p TurquazBusinessLogic/bin
BL_CP="$SERVER_CP:TurquazServer/bin"
for j in TurquazBusinessLogic/lib/*.jar; do
    [[ -f "$j" ]] && BL_CP="$BL_CP:$j"
done
javac "${JAVAC_OPTS[@]}" -cp "$BL_CP" -d TurquazBusinessLogic/bin \
    $(find TurquazBusinessLogic/src -name '*.java')
sub "BusinessLogic: $(find TurquazBusinessLogic/bin -name '*.class' | wc -l) sınıf"

# 1d. TurquazStandAlone — Hibernate bootstrap + ServiceCaller in-process
mkdir -p TurquazStandAlone/bin
SA_CP="$BL_CP:TurquazBusinessLogic/bin"
for j in TurquazStandAlone/lib/*.jar; do
    [[ -f "$j" ]] && SA_CP="$SA_CP:$j"
done
javac "${JAVAC_OPTS[@]}" -cp "$SA_CP" -d TurquazStandAlone/bin \
    $(find TurquazStandAlone/src -name '*.java')
sub "StandAlone:    $(find TurquazStandAlone/bin -name '*.class' | wc -l) sınıf"

# 1e. TurquazClient — SWT formları (en büyük modül)
mkdir -p TurquazClient/bin
CL_CP="$SA_CP:TurquazStandAlone/bin"
for j in TurquazClient/lib/*.jar TurquazClient/lib/jface/*.jar TurquazClient/lib/jasperreport/*.jar TurquazClient/lib/poi/*.jar TurquazClient/lib/windows/*.jar TurquazClient/lib/lzma/*.jar; do
    [[ -f "$j" ]] && CL_CP="$CL_CP:$j"
done
javac "${JAVAC_OPTS[@]}" -cp "$CL_CP" -d TurquazClient/bin \
    $(find TurquazClient/src -name '*.java')
sub "Client:        $(find TurquazClient/bin -name '*.class' | wc -l) sınıf"

# ---------------------------------------------------------------------------
# 1f. Non-Java kaynakları (i18n bundle, .hbm.xml, .gif, vd.) bin/'e taşı
#     javac sadece .java dosyalarını işler; ResourceBundle.getBundle ve
#     Hibernate'in classpath kaynak arayışı için bunlar bin/'de olmalı.
# ---------------------------------------------------------------------------
log "Java olmayan kaynakları bin/ ağaçlarına kopyala"
for mod in TurquazCommon TurquazServer TurquazBusinessLogic TurquazStandAlone TurquazClient; do
    (cd "$mod/src" && \
        find . -type f \
            \( -name '*.properties' -o -name '*.xml' -o -name '*.hbm.xml' \
               -o -name '*.gif' -o -name '*.png' -o -name '*.jpg' \
               -o -name '*.jrxml' -o -name '*.jasper' \
               -o -name '*.dtd' -o -name '*.txt' \) \
            -exec sh -c 'mkdir -p "../bin/$(dirname "$1")" && cp "$1" "../bin/$1"' _ {} \;)
done

# ---------------------------------------------------------------------------
# 2. Ant ile turquaz-*.jar üretimi (build.xml zaten doğru hedefleri içeriyor)
# ---------------------------------------------------------------------------
log "Ant build (turquaz-{standalone,client,common,business}.jar + run.jar + dbgui.jar)"
(cd TurquazStandAlone && ant -q)

# ---------------------------------------------------------------------------
# 3. Her hedef için SWT'yi swap edip dist/'i paketle
# ---------------------------------------------------------------------------
log "Hedef OS'ler için paketleme (SWT $SWT_VERSION, Maven Central)"

IFS=',' read -ra TGT_LIST <<< "$TARGETS"
for tgt in "${TGT_LIST[@]}"; do
    tgt=$(echo "$tgt" | xargs)
    art="${SWT_ART[$tgt]:-}"
    ext="${ARCHIVE_EXT[$tgt]:-}"
    if [[ -z "$art" ]]; then
        err "Bilinmeyen hedef: '$tgt' — atlandı"
        continue
    fi

    sub "$tgt"
    STAGE="$WORK/stage-$tgt"
    cp -a TurquazStandAlone/dist "$STAGE"

    # 3a. Maven Central'dan modern SWT fragment jar'ı
    URL="https://repo1.maven.org/maven2/org/eclipse/platform/${art}/${SWT_VERSION}/${art}-${SWT_VERSION}.jar"
    if ! curl -fsSL "$URL" -o "$STAGE/lib/swt.jar.new"; then
        err "$tgt: SWT indirme başarısız — $URL"
        rm -rf "$STAGE"
        continue
    fi

    # 3b. Eski SWT jar'larını ve 32-bit native lib'lerini temizle
    find "$STAGE/lib" -maxdepth 1 -name 'swt*.jar' -delete
    mv "$STAGE/lib/swt.jar.new" "$STAGE/lib/swt.jar"
    find "$STAGE" -maxdepth 1 \( -name 'swt-*.dll' -o -name 'libswt-*.so' -o -name '*.jnilib' \) -delete

    # 3c. Platform launcher
    #   SWT 3.124 Java 17+ ister; Hibernate 3.0.3 + CGLIB 2.x ise Java 17'nin
    #   güçlü encapsulation'ı altında reflection iç dökümlerinde patlar.
    #   --add-opens flag'leri eski reflection pattern'lerini açar.
    #   Mac için SWT mutlaka -XstartOnFirstThread ile başlatılmalı.
    JAVA_OPTS_COMMON='--add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED'
    case "$tgt" in
        linux*)
            cat > "$STAGE/turquaz.sh" <<LAUNCH
#!/usr/bin/env sh
DIR=\$(cd "\$(dirname "\$0")" && pwd)
cd "\$DIR"
if ! command -v java >/dev/null 2>&1; then
    echo "Java 17+ PATH'te bulunamadı. https://adoptium.net adresinden Temurin 17 LTS kur." >&2
    exit 1
fi
exec java $JAVA_OPTS_COMMON -jar run.jar "\$@"
LAUNCH
            chmod +x "$STAGE/turquaz.sh"
            ;;
        macos*)
            cat > "$STAGE/turquaz.sh" <<LAUNCH
#!/usr/bin/env sh
DIR=\$(cd "\$(dirname "\$0")" && pwd)
cd "\$DIR"
if ! command -v java >/dev/null 2>&1; then
    echo "Java 17+ PATH'te bulunamadı. \`brew install --cask temurin\` ile Temurin 17 LTS kur." >&2
    exit 1
fi
exec java -XstartOnFirstThread $JAVA_OPTS_COMMON -jar run.jar "\$@"
LAUNCH
            chmod +x "$STAGE/turquaz.sh"
            ;;
        windows)
            cat > "$STAGE/turquaz.bat" <<LAUNCH
@echo off
cd /d "%~dp0"
where java >nul 2>&1
if errorlevel 1 (
  echo Java 17+ PATH'te bulunamadi. https://adoptium.net adresinden Temurin 17 LTS kur.
  pause
  exit /b 1
)
java $JAVA_OPTS_COMMON -jar run.jar %*
LAUNCH
            ;;
    esac

    # 3d. Yapı metadata'sı
    cat > "$STAGE/BUILD-INFO.txt" <<INFO
Turquaz Standalone — Resurrected build
========================================
Hedef:           $tgt
SWT artefaktı:   $art
SWT sürümü:      $SWT_VERSION (Eclipse SWT, Maven Central)
Build zamanı:    $(date -u +%Y-%m-%dT%H:%M:%SZ)
Java target:     1.8 (-source 8 -target 8) — bytecode Java 8+ uyumlu
Runtime gereği:  Java 17+ (SWT 3.122+ Java 17 isteyor)
Kodlama:         $ENCODING

Kaynak yazarlar (CVS, 2004–2010):
  Önsel Armağan (onsel)     — 1038 commit
  Cem Dayanık   (cemdayanik) —  577 commit
  Hüseyin Erkin (huseyiner)  —  143 commit
  cem, huseyin, ehad, erhanb

Çalıştırma:
  $(case "$tgt" in windows) echo "turquaz.bat";; *) echo "./turquaz.sh";; esac)

Lisans: GNU General Public License v2 (GPLv2) — orijinal Turquaz lisansı.
INFO

    # 3e. Paketle
    ART="$OUT/turquaz-standalone-resurrected-${tgt}.${ext}"
    case "$ext" in
        tar.gz)
            tar -C "$WORK" -czf "$ART" \
                --transform "s|^stage-$tgt|turquaz-standalone|" \
                "stage-$tgt"
            ;;
        zip)
            (cd "$WORK" && cp -a "stage-$tgt" "turquaz-standalone" \
                && zip -rq "$ART" "turquaz-standalone" \
                && rm -rf "turquaz-standalone")
            ;;
    esac

    sub "  → $(basename "$ART") ($(du -sh "$ART" | cut -f1))"
done

log "Tamamlandı. Artefaktlar:"
ls -lh "$OUT"
