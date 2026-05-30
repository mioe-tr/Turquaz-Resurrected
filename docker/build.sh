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

# Hibernate mapping DTD migrasyonu:
#   Tüm .hbm.xml dosyaları Hibernate 2.x dönemi DOCTYPE'ıyla başlıyor:
#       "-//Hibernate/Hibernate Mapping DTD 2.0//EN"
#       "http://hibernate.sourceforge.net/hibernate-mapping-2.0.dtd"
#   Modern Hibernate 3.6'nın dahili DTDEntityResolver'ı sadece "3.0"
#   PUBLIC ID'sini classpath'ten resolve ediyor. 2.0 ID'si yanıtsız
#   kalıp parser internet'ten DTD çekmeye çalışıyor → SAX patlar.
#   2.0 DTD'si 3.0 ile geriye uyumlu olduğundan in-place upgrade yeterli.
HBM_COUNT=$(find . -name '*.hbm.xml' | wc -l)
find . -name '*.hbm.xml' -exec sed -i \
    -e 's|Hibernate Mapping DTD 2\.0|Hibernate Mapping DTD 3.0|g' \
    -e 's|hibernate-mapping-2\.0\.dtd|hibernate-mapping-3.0.dtd|g' \
    {} +
sub "Hibernate mapping DTD 2.0 → 3.0 (${HBM_COUNT} .hbm.xml dosyası)"

# EngDALSessionFactory: Hibernate 3.6 query cache açıkken second-level
# cache'i de açık ister; Hibernate 3.0'da bağımsızdı. Accountancy uygulaması
# için query cache şart değil — devre dışı bırakıyoruz.
sed -i 's|props\.put("hibernate.cache.use_query_cache","true");|props.put("hibernate.cache.use_query_cache","false"); props.put("hibernate.hbm2ddl.auto","update");|' \
    TurquazStandAlone/src/server/util/EngDALSessionFactory.java
sub "EngDALSessionFactory: query_cache=false + hbm2ddl.auto=update"

# ---------------------------------------------------------------------------
# Seed runner: SessionFactory build'tan sonra turq_settings bossa,
# turquaz-import.sql'i classpath'ten okuyup statement-by-statement calistirir.
# Hibernate'in hbm2ddl.auto=update modu import.sql'i otomatik calistirmiyor;
# kendimiz JDBC ile yapmamiz lazim.
# ---------------------------------------------------------------------------
cat > TurquazStandAlone/src/server/util/SeedRunner.java <<'SEED_EOF'
package server.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Statement;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public final class SeedRunner {
    private SeedRunner() {}

    public static void seedIfEmpty(SessionFactory factory) {
        if (factory == null) return;
        Session s = factory.openSession();
        Transaction tx = null;
        try {
            Object raw = s.createSQLQuery("SELECT COUNT(*) FROM turq_settings").uniqueResult();
            int existing = (raw instanceof Number) ? ((Number) raw).intValue() : 0;
            if (existing > 0) {
                System.out.println("SeedRunner: turq_settings dolu (" + existing + " row), seed atlandi");
                return;
            }
            InputStream is = SeedRunner.class.getResourceAsStream("/turquaz-import.sql");
            if (is == null) {
                System.err.println("SeedRunner: classpath'te /turquaz-import.sql yok");
                return;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            tx = s.beginTransaction();
            Statement stmt = s.connection().createStatement();
            String line;
            StringBuilder buf = new StringBuilder();
            int ok = 0, skip = 0;
            while ((line = br.readLine()) != null) {
                String t = line.trim();
                if (t.length() == 0 || t.startsWith("--")) continue;
                buf.append(line).append('\n');
                if (t.endsWith(";")) {
                    String sql = buf.toString().trim();
                    sql = sql.substring(0, sql.length() - 1);
                    try { stmt.execute(sql); ok++; }
                    catch (Exception ie) { skip++; }
                    buf.setLength(0);
                }
            }
            stmt.close();
            tx.commit();
            br.close();
            System.out.println("SeedRunner: " + ok + " INSERT basarili, " + skip + " atlandi (duplike vs.)");
        } catch (Exception ex) {
            if (tx != null) { try { tx.rollback(); } catch (Exception ignore) {} }
            ex.printStackTrace();
        } finally {
            s.close();
        }
    }
}
SEED_EOF
sub "SeedRunner.java yazildi (TurquazStandAlone/src/server/util/)"

# EngDALSessionFactory'yi patch'le: buildSessionFactory'den sonra SeedRunner'i cagir
sed -i 's|factory = cfg\.buildSessionFactory();|factory = cfg.buildSessionFactory(); server.util.SeedRunner.seedIfEmpty(factory);|' \
    TurquazStandAlone/src/server/util/EngDALSessionFactory.java
sub "EngDALSessionFactory: buildSessionFactory sonrasi SeedRunner cagrisi eklendi"

# turquaz-import.sql olustur: EngBLVersionValidate.java icindeki migration
# zincirindeki tum INSERT'leri (turq_services, turq_engine_menu, vd.)
# birlestirip statik bir SQL dosyasi olarak yazıyoruz. Ayrica turq_settings'i
# mevcut DATABASE_VERSION='0.8.1' ile seed ediyoruz, boylece checkVersion()
# basariyla doner ve migration zinciri (HSQLDB 2.x'te multi-statement
# execute() sorunlu) tamamen atlanir.
mkdir -p TurquazCommon/bin
BL_FILE=TurquazBusinessLogic/src/com/turquaz/engine/bl/EngBLVersionValidate.java
{
    echo "-- Turquaz seed data — Resurrected build script tarafindan uretildi"
    echo "-- Kaynak: EngBLVersionValidate.java migration zinciri (HSQLDB variant)"
    echo "-- Hibernate hbm2ddl.auto=update tablolari yarattiktan sonra, SeedRunner"
    echo "-- bu dosyayi turq_settings bossa calistirir."
    echo ""
    echo "-- ===== Services (turq_services) ====="
    grep -E '"INSERT INTO turq_services' "$BL_FILE" \
        | sed -E 's/^[[:space:]]*"//; s/;"[[:space:]]*\+?[[:space:]]*$/;/' \
        | sort -u
    echo ""
    echo "-- ===== Engine menu (turq_engine_menu) ====="
    grep -E '"INSERT INTO turq_engine_menu' "$BL_FILE" \
        | sed -E 's/^[[:space:]]*"//; s/;"[[:space:]]*\+?[[:space:]]*$/;/' \
        | sort -u
    echo ""
    echo "-- ===== Module components (turq_module_components) ====="
    grep -E '"INSERT INTO turq_module_components' "$BL_FILE" \
        | sed -E 's/^[[:space:]]*"//; s/;"[[:space:]]*\+?[[:space:]]*$/;/' \
        | sort -u
    echo ""
    echo "-- ===== Inventory accounting types (turq_inventory_accounting_types) ====="
    grep -E '"INSERT INTO turq_inventory_accounting_types' "$BL_FILE" \
        | sed -E 's/^[[:space:]]*"//; s/;"[[:space:]]*\+?[[:space:]]*$/;/' \
        | sort -u
    echo ""
    echo "-- ===== Eski Turquaz/database/turquaz.script'ten reference data ====="
    echo "-- Hesap planı (~460 hesap), modüller, sırasıyla, döviz, vd."
    if [[ -f "$SRC/Turquaz/database/turquaz.script" ]]; then
        # Sadece INSERT INTO satırlarını çıkar (CREATE/SET vd atlanır).
        # SeedRunner duplike PK ve format farklarını try/catch ile skip ediyor.
        # Eski script TURQ_SETTINGS'i de içeriyor; onu hariç tut, sonda kendi seed'imizi koyalım.
        # HSQLDB internal script formatı INSERT'leri ';' olmadan yazıyor;
        # SeedRunner statement bitişini tanıyabilsin diye sonlarına ';' ekliyoruz.
        grep -iE "^INSERT INTO TURQ_" "$SRC/Turquaz/database/turquaz.script" \
            | grep -viE "INSERT INTO TURQ_SETTINGS" \
            | sed -E 's/[[:space:]]*$/;/'
    fi
    echo ""
    echo "-- ===== Settings: mevcut sürüm ile seed (migration zinciri atlanir) ====="
    echo "INSERT INTO turq_settings (id, database_version) VALUES (0, '0.8.1');"
} > TurquazCommon/bin/turquaz-import.sql
SEED_LINES=$(grep -c "^INSERT" TurquazCommon/bin/turquaz-import.sql)
sub "turquaz-import.sql üretildi: $SEED_LINES INSERT (services + menu + components + settings)"

# HSQLDB başlangıç durumu:
# HSQLDB 2.x eski 1.7.3 dosya formatını okuyamaz ("wrong database file
# version"). Onun yerine boş DB ile başlıyoruz; yukarıda Hibernate'e
# eklenen hbm2ddl.auto=update flag'i SessionFactory build sırasında 90
# Hibernate entity'sinden tabloları otomatik yaratıyor. Schema güvenli
# şekilde oluşur, EngBLVersionValidate sonra Turquaz spesifik seed
# verileri (turq_services, vd.) ekleyebilir.
mkdir -p TurquazStandAlone/dist/database
sub "HSQLDB: dist/database/ boş; Hibernate hbm2ddl.auto=update şema'yı kuracak"

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
# 2b. Hibernate 3.0.3 → 3.6.10.Final swap (runtime için)
#     Hibernate 3.0.3 (2005) Java 17+ ile DTD resolver patlatıyor; mapping
#     dosyalarındaki DOCTYPE'ı internet'ten çekmeye çalışıyor. 3.6.10.Final
#     classpath-relative DTD resolver kullanıyor ve Java 17 ile uyumlu.
#     API geriye uyumlu (Session/Transaction/Configuration imzaları aynı).
# ---------------------------------------------------------------------------
log "Hibernate 3.0.3 → 3.6.10.Final swap (Maven Central)"
HIB_VERSION=${HIB_VERSION:-3.6.10.Final}
MVN_BASE=https://repo1.maven.org/maven2

mvn_jar() {
    local group_path=$1 artifact=$2 ver=$3 outdir=$4
    local group_slash="${group_path//.//}"
    local jar="${artifact}-${ver}.jar"
    curl -fsSL "$MVN_BASE/$group_slash/$artifact/$ver/$jar" -o "$outdir/$jar" \
        || { err "$jar indirilemedi"; return 1; }
    sub "+ $jar"
}

DL=TurquazStandAlone/dist/lib

# Eski Hibernate 3.0.3 + CGLIB/ASM ailesini at (Hibernate 3.6 javassist kullanır)
rm -f "$DL"/hibernate3.jar \
      "$DL"/antlr-2.7.5H3.jar \
      "$DL"/commons-collections-2.1.1.jar \
      "$DL"/dom4j-1.6.jar \
      "$DL"/cglib-2.1.jar \
      "$DL"/cglib2.jar \
      "$DL"/asm.jar \
      "$DL"/asm-attrs.jar

# Hibernate 3.6.10 + transitive dependencies
mvn_jar org.hibernate hibernate-core                   "$HIB_VERSION"      "$DL"
mvn_jar org.hibernate hibernate-commons-annotations    3.2.0.Final         "$DL"
mvn_jar org.hibernate.javax.persistence hibernate-jpa-2.0-api 1.0.1.Final  "$DL"
mvn_jar antlr antlr                                    2.7.7               "$DL"
mvn_jar commons-collections commons-collections        3.1                 "$DL"
mvn_jar dom4j dom4j                                    1.6.1               "$DL"
mvn_jar javassist javassist                            3.12.0.GA           "$DL"
mvn_jar org.slf4j slf4j-api                            1.6.1               "$DL"
mvn_jar org.slf4j slf4j-log4j12                        1.6.1               "$DL"
mvn_jar javax.transaction jta                          1.1                 "$DL"

# HSQLDB 1.7.3 (2005) → 2.7.2 (2023). Modern Hibernate 3.6 + modern HSQLDB
# daha temiz çalışıyor; 1.7.x kullanıcı yönetimi 2.x'le uyumsuz.
rm -f "$DL"/hsqldb.jar
mvn_jar org.hsqldb hsqldb 2.7.2 "$DL"

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
