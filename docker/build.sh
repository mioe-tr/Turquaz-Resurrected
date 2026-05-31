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
SWT_VERSION=${SWT_VERSION:-3.131.0}
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
            // Seed sirasinda FK kontrolu kapali tutuluyor; HSQLDB 2.x sintaksi.
            // turq_engine_menu -> turq_module_components gibi FK'lar yuzunden
            // sirayla INSERT gerekmesin diye.
            try { stmt.execute("SET DATABASE REFERENTIAL INTEGRITY FALSE"); } catch (Exception ignore) {}
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
                    catch (Exception ie) {
                        skip++;
                        if (skip <= 30) System.err.println("SeedRunner skip[" + skip + "]: " + ie.getMessage() + " | SQL: " + sql.substring(0, Math.min(sql.length(), 200)));
                    }
                    buf.setLength(0);
                }
            }
            // Orphan FK cleanup: turq_engine_menu icindeki menu_module_component
            // FK'lari turq_module_components ID seti ile eslesmiyorsa (eski
            // monolitik Turquaz schema farkli ID sistemi kullaniyordu) placeholder
            // component ekle. Boylece Hibernate lazy initialize ObjectNotFoundException
            // atmaz; UI bu menuleri "isimlendirilmemis" olarak gosterir.
            int orphanFixed = 0;
            try {
                String selectOrphans =
                    "SELECT DISTINCT menu_module_component FROM turq_engine_menu " +
                    "WHERE menu_module_component IS NOT NULL " +
                    "AND menu_module_component NOT IN (SELECT id FROM turq_module_components)";
                java.util.ArrayList orphanIds = new java.util.ArrayList();
                java.sql.ResultSet rs = stmt.executeQuery(selectOrphans);
                while (rs.next()) orphanIds.add(Integer.valueOf(rs.getInt(1)));
                rs.close();
                String ins =
                    "INSERT INTO turq_module_components " +
                    "(id, modules_id, components_name, components_description, " +
                    " created_by, creation_date, updated_by, update_date) " +
                    "VALUES (?, -1, ?, 'auto-placeholder for orphan FK', " +
                    "        'system', CURRENT_DATE, 'system', CURRENT_DATE)";
                java.sql.PreparedStatement ps = s.connection().prepareStatement(ins);
                for (int idx = 0; idx < orphanIds.size(); idx++) {
                    int id = ((Integer) orphanIds.get(idx)).intValue();
                    ps.setInt(1, id);
                    ps.setString(2, "orphan_" + id);
                    try { ps.executeUpdate(); orphanFixed++; }
                    catch (Exception ignore) {}
                }
                ps.close();
            } catch (Exception orphanEx) {
                System.err.println("SeedRunner: orphan cleanup hata: " + orphanEx.getMessage());
            }
            try { stmt.execute("SET DATABASE REFERENTIAL INTEGRITY TRUE"); } catch (Exception ignore) {}
            stmt.close();
            tx.commit();
            br.close();
            // SQL VIEW restore: orijinal Turquaz database/turquaz.script icinde
            // 8 adet CREATE VIEW vardi (TURQ_VIEW_ACC_TOTALS vd.). Bu view'lar
            // Hibernate .hbm.xml mapping'lerinde "<class table=turq_view_*>"
            // olarak tanimli — hbm2ddl.auto=update bunlari BOS TABLO olarak
            // yaratiyor, view olarak degil. Sonuc: AccUIAccountingPlan.getAllAccountsWithSum
            // sorgusu TurqAccountingAccount ile TurqViewAccTotal'i INNER JOIN
            // edip 0 row donduruyor — hesap plani UI'de tamamen bos kaliyor.
            // Cozum: bos turq_view_* tablolarini drop edip orijinal CREATE VIEW'lari
            // calistir, boylece view'lar canlanir ve query 462 row doner.
            int viewsOk = 0, viewsSkip = 0;
            InputStream vis = SeedRunner.class.getResourceAsStream("/turquaz-views.sql");
            if (vis != null) {
                BufferedReader vbr = new BufferedReader(new InputStreamReader(vis, "UTF-8"));
                Statement vstmt = s.connection().createStatement();
                StringBuilder vbuf = new StringBuilder();
                String vline;
                while ((vline = vbr.readLine()) != null) {
                    String t = vline.trim();
                    if (t.length() == 0 || t.startsWith("--")) continue;
                    vbuf.append(vline).append('\n');
                    if (t.endsWith(";")) {
                        String sql = vbuf.toString().trim();
                        sql = sql.substring(0, sql.length() - 1);
                        java.util.regex.Matcher vm = java.util.regex.Pattern
                            .compile("CREATE VIEW\\s+(\\w+)", java.util.regex.Pattern.CASE_INSENSITIVE)
                            .matcher(sql);
                        if (vm.find()) {
                            String viewName = vm.group(1);
                            try { vstmt.execute("DROP TABLE IF EXISTS " + viewName + " CASCADE"); } catch (Exception ignore) {}
                            try { vstmt.execute("DROP VIEW IF EXISTS " + viewName + " CASCADE"); } catch (Exception ignore) {}
                        }
                        try { vstmt.execute(sql); viewsOk++; }
                        catch (Exception ve) {
                            viewsSkip++;
                            System.err.println("SeedRunner view skip: " + ve.getMessage()
                                + " | SQL: " + sql.substring(0, Math.min(sql.length(), 200)));
                        }
                        vbuf.setLength(0);
                    }
                }
                vstmt.close();
                vbr.close();
                System.out.println("SeedRunner: " + viewsOk + " VIEW yaratildi, " + viewsSkip + " atlandi");
            } else {
                System.err.println("SeedRunner: /turquaz-views.sql classpath'te yok — view restore atlandi");
            }
            // CHECKPOINT: pending degisiklikleri .script dosyasina flush et;
            // uygulama crashed/force-quit olsa bile seed kalir, kullanici
            // turquaz.script acinca INSERT'leri gorur.
            try {
                java.sql.Statement cp = s.connection().createStatement();
                cp.execute("CHECKPOINT");
                cp.close();
            } catch (Exception cpe) { System.err.println("SeedRunner: CHECKPOINT atlandi: " + cpe.getMessage()); }
            System.out.println("SeedRunner: " + ok + " INSERT basarili, " + skip
                + " atlandi (duplike vs.), " + orphanFixed + " orphan FK placeholder eklendi");
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

# HSQLDB 2.x default'ta CACHED tablo yaratir (data .data dosyasinda); orijinal
# Turquaz HSQLDB 1.x MEMORY mode kullaniyordu (data .script dosyasinda).
# JDBC URL'e hsqldb.default_table_type=memory ekleyerek orijinal davranisa
# don — kullanici turquaz.script'i acinca tum INSERT'leri gorur.
sed -i 's|jdbc:hsqldb:database/turquaz|jdbc:hsqldb:file:database/turquaz;hsqldb.default_table_type=memory;hsqldb.write_delay=false|' \
    TurquazStandAlone/src/server/util/EngDALSessionFactory.java
sub "EngDALSessionFactory: JDBC URL'e default_table_type=memory + write_delay=false"

# EngBLServer.getApplicationMenus: yaprak menulerin (type==3) permission
# check'ini bypass et + null-safe getTurqModuleComponent() cagri.
# Orijinal kod turq_user_permissions tablosuna bagimli; bu tablo seed'i
# kompleks, demo "dirilik" modu icin tum menuleri gosterelim.
BL_SERVER=TurquazBusinessLogic/src/com/turquaz/engine/bl/EngBLServer.java
# Permission check'i bypass et: turq_user_permissions seed kompleks; demo
# modu icin tum yaprak menuleri goster.
sed -i 's|if(perm_level!=null)|if(true) // resurrected: bypass|' "$BL_SERVER"
sed -i 's|if(perm_level\.intValue()>0)|if(true) // resurrected: bypass|' "$BL_SERVER"
# Stale FK referansli menulere karsi null-safe sarmala: lazy proxy NPE'sini
# sessizce yut.
python3 - <<'PYEOF'
import re
p = "TurquazBusinessLogic/src/com/turquaz/engine/bl/EngBLServer.java"
src = open(p).read()
# Lazy proxy ya da silinmis component icin NPE/ObjectNotFoundException kaynagi.
# Tum menuleri (orphan dahil) goster — kullanici siralamayi koruyarak gorebilsin.
# Tikladiginda Class.forName fail openNewTab try-catch icinde log'a yazilir.
old = "menu.getTurqModuleComponent().getComponentsName()"
new = ("(menu.getTurqModuleComponent() != null "
       "&& menu.getTurqModuleComponent().getComponentsName() != null "
       "? menu.getTurqModuleComponent().getComponentsName() : null)")
n = src.count(old)
if n > 0:
    open(p, "w").write(src.replace(old, new))
    print(f"  PATCH OK: getTurqModuleComponent null-safe ({n} occurrences)")
else:
    print("  PATCH WARN: getTurqModuleComponent() call'i bulunamadi")

# MenuManager.MenuSelectionAdapter null-safe: widget.getData() null donerse
# .toString() NPE atiyordu (top menu bar item'inda setData(null)'dan kaynaklanir).
mm = "TurquazClient/src/com/turquaz/engine/ui/component/MenuManager.java"
src2 = open(mm).read()
old2 = "EngUIMainFrame.openNewTab(((MenuItem) arg0.widget).getText(), arg0.widget.getData().toString());"
new2 = (
    "Object _d = arg0.widget.getData();\n"
    "\t\tif (_d != null) {\n"
    "\t\t\tEngUIMainFrame.openNewTab(((MenuItem) arg0.widget).getText(), _d.toString());\n"
    "\t\t}"
)
if old2 in src2:
    open(mm, "w").write(src2.replace(old2, new2))
    print("  PATCH OK: MenuManager.MenuSelectionAdapter null-safe")
else:
    print("  PATCH WARN: MenuManager satiri bulunamadi")
PYEOF
sub "EngBLServer.getApplicationMenus + MenuManager null-safe"

# EngUIMainFrame.tabfldMainItemClosed: getControl() null donerse NPE
# atiyordu. Modern SWT 3.131'de cabuk-kapanan tab control'unu null donmus
# olabiliyor; null-safe sarmal.
python3 - <<'PYEOF'
p = "TurquazClient/src/com/turquaz/engine/ui/EngUIMainFrame.java"
src = open(p).read()
old = "mapList.remove(item.getControl().getClass().getName());"
new = ("if (item != null && item.getControl() != null) {\n"
       "\t\t\tmapList.remove(item.getControl().getClass().getName());\n"
       "\t\t}")
if old in src:
    open(p, "w").write(src.replace(old, new))
    print("  PATCH OK: tabfldMainItemClosed null-safe getControl()")
else:
    print("  PATCH WARN: tabfldMainItemClosed satiri bulunamadi")
PYEOF

# EngConfiguration.java cross-platform path fix:
# Orijinal kod Windows-only "\\.turquaz\\config\\turquaz.xml" path'ini
# hardcode'lamis. macOS/Linux'ta literal `\` karakteri olarak parse edilip
# FileNotFoundException (Permission denied) atiyor. Forward slash'a cevir;
# Java tum OS'lerde `/` separator'u kabul eder (Windows dahil).
python3 - <<'PYEOF'
p = "TurquazClient/src/com/turquaz/engine/EngConfiguration.java"
src = open(p).read()
patches = [
    ('"\\\\.turquaz\\\\config\\\\turquaz.xml"', '"/.turquaz/config/turquaz.xml"'),
    ('"\\\\.turquaz\\\\config"',                '"/.turquaz/config"'),
]
n = 0
for old, new in patches:
    if old in src:
        src = src.replace(old, new)
        n += 1
if n > 0:
    open(p, "w").write(src)
    print(f"  PATCH OK: EngConfiguration.java path separator ({n}/2 yer)")
else:
    print("  PATCH WARN: EngConfiguration.java path eski format bulunamadi")
PYEOF

# EngUIEntryFrameStandalone Base64: eski Eclipse runtime'daki internal
# org.eclipse.core.internal.preferences.Base64 modern JFace/runtime'da yok.
# Java 8'den beri standart java.util.Base64 var.
python3 - <<'PYEOF'
p = "TurquazClient/src/com/turquaz/engine/ui/EngUIEntryFrameStandalone.java"
src = open(p).read()
src = src.replace(
    "import org.eclipse.core.internal.preferences.Base64;",
    "import java.util.Base64;"
)
src = src.replace(
    "org.eclipse.core.internal.preferences.Base64.encode(txtPassword.getText().getBytes())",
    "Base64.getEncoder().encode(txtPassword.getText().getBytes())"
)
src = src.replace(
    "Base64.decode(password.getBytes())",
    "Base64.getDecoder().decode(password.getBytes())"
)
open(p, "w").write(src)
print("  PATCH OK: EngUIEntryFrameStandalone Base64 -> java.util.Base64")
PYEOF

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
    # BL'den INSERT extract'i: Java string literal'leri "INSERT...);" formatinda.
    # grep -oE ile sadece string content'ini al ($"...";) prefix'siz/suffix'siz.
    echo "-- ===== Services + menu + components + inventory_accounting_types ====="
    grep -oE '"INSERT INTO turq_(services|engine_menu|module_components|inventory_accounting_types)[^"]*"' "$BL_FILE" \
        | sed -E 's/^"//; s/"$/;/' \
        | sort -u
    echo ""
    echo "-- ===== Orijinal Turquaz 0.8 Beta 4 turquaz.script (production seed) ====="
    echo "-- 462 hesap planı + 227 services + 141 menu + 102 components + settings"
    # Orijinal Windows installer'dan cikartilmis tam dist/database/turquaz.script:
    # 75 CREATE TABLE + 1106 INSERT. Eski Turquaz/database/turquaz.script
    # (745 INSERT) eksikti — turq_accounting_accounts 0 row, services az vd.
    # Bu dosya 2005 production'da kullanilan tam seed.
    SCRIPT_SRC=""
    for cand in "$SRC/Turquaz/database/turquaz-08beta4.script" \
                "$SRC/Turquaz/database/turquaz.script"; do
        [[ -f "$cand" ]] && SCRIPT_SRC="$cand" && break
    done
    if [[ -n "$SCRIPT_SRC" ]]; then
        # HSQLDB internal format INSERT'leri ';' olmadan yazar; sed ile ekliyoruz.
        # ISO-8859-9 -> UTF-8 (Turkce karakterler icin).
        # turq_settings dahil — orijinal script'te 0.8.1 set ediliyor zaten.
        iconv -f ISO-8859-9 -t UTF-8 "$SCRIPT_SRC" \
            | grep -iE "^INSERT INTO TURQ_" \
            | sed -E 's/[[:space:]]*$/;/'
    fi
} > TurquazCommon/bin/turquaz-import.sql
SEED_LINES=$(grep -c "^INSERT" TurquazCommon/bin/turquaz-import.sql)
sub "turquaz-import.sql üretildi: $SEED_LINES INSERT (services + menu + components + settings)"

# turquaz-views.sql: orijinal HSQLDB 1.x script'inden cikartilmis 8 adet
# CREATE VIEW statement. SeedRunner INSERT'lerden sonra bunlari restore eder;
# yoksa Hibernate hbm2ddl=update bunlari bos tablo olarak yaratir ve hesap
# plani UI'si TurqViewAccTotal join'i ile 0 row doner.
if [[ -f "$SRC/docker/turquaz-views.sql" ]]; then
    cp "$SRC/docker/turquaz-views.sql" TurquazCommon/bin/turquaz-views.sql
    VIEW_COUNT=$(grep -c "^CREATE VIEW" TurquazCommon/bin/turquaz-views.sql)
    sub "turquaz-views.sql kopyalandi: $VIEW_COUNT CREATE VIEW (TurqViewAccTotal vd.)"
else
    sub "UYARI: docker/turquaz-views.sql yok — hesap plani UI bos kalabilir"
fi

# Eski script ve BL'den gelen tum INSERT'ler "VALUES (...)" formatında —
# kolon adlari yok. Hibernate'in olusturdugu kolon sirasi (property + en
# sonda associations) eski CREATE TABLE sirasindan farkli; VALUES tip
# uyusmazligi olusturuyor.
# Cozum: eski script'in CREATE TABLE statement'larindan tablo -> kolon
# listesini Python ile parse et, sonra her INSERT INTO X VALUES (...) 'i
# INSERT INTO X (col1, col2, ...) VALUES (...) formatina cevir.
SCHEMA_CAND="$SRC/Turquaz/database/turquaz-08beta4.script"
[[ -f "$SCHEMA_CAND" ]] || SCHEMA_CAND="$SRC/Turquaz/database/turquaz.script"
SCHEMA_PATH="$SCHEMA_CAND" python3 - <<'PYEOF'
import os, re

# 1. Eski CREATE TABLE'lardan tablo -> kolon haritasi
schema_path = os.environ["SCHEMA_PATH"]
tbl_cols = {}
with open(schema_path) as f:
    for line in f:
        m = re.match(r'^CREATE\s+TABLE\s+(\w+)\s*\((.*)\)\s*$', line, re.I)
        if not m: continue
        tbl = m.group(1).lower()
        body = m.group(2)
        # CONSTRAINT'leri at, sadece kolon tanimlarini al
        cols = []
        depth = 0; cur = []
        for ch in body:
            if ch == '(': depth += 1
            elif ch == ')': depth -= 1
            if ch == ',' and depth == 0:
                cols.append(''.join(cur).strip()); cur = []
            else:
                cur.append(ch)
        if cur: cols.append(''.join(cur).strip())
        col_names = []
        for c in cols:
            if c.upper().startswith("CONSTRAINT"): continue
            name = c.split()[0]
            col_names.append(name.lower())
        tbl_cols[tbl] = col_names

# Turquaz2 doneminde eklenen tablolar eski script'te yok; bunlar icin
# manuel kolon listesi. KRITIK: BL migration kodu VALUES sirasini yanlis
# yazmis — modern Hibernate hbm.xml property-then-association siralamasiyla
# uyusmuyor. Eski Hibernate 3.0'da kolon sirasi farkliydi (FK ortada,
# parent_id sonda). Ornek BL INSERT:
#   "INSERT INTO turq_engine_menu VALUES (10101, 'STR_ACCOUNTING_PLAN',
#    ' ', 3, 26, 10100);"
# Burada VALUES (id, name, image, type, **FK=26**, **parent_id=10100**)
# olmali; ama modern Hibernate (id, name, image, type, parent_id, FK)
# diziyor — sonuc: 26 parent_id'ye, 10100 FK'ya gidiyor → orphan_10100.
# Manuel kolon listesi ile dogru kolonlara map ediyoruz.
tbl_cols.setdefault("turq_engine_menu", [
    "id", "menu_name", "menu_image", "menu_type",
    "menu_module_component",  # 5. VALUES: BL'nin component FK'si
    "parent_id",               # 6. VALUES: BL'nin parent menu ID'si
])
tbl_cols.setdefault("turq_services", [
    "id", "service_name", "class_name", "method_name",
])
tbl_cols.setdefault("turq_settings", [
    "id", "database_version",
])

# 2. turquaz-import.sql'i parse et, INSERT'leri kolon-adlandirilmis hale getir
import_path = "TurquazCommon/bin/turquaz-import.sql"
with open(import_path) as f:
    src = f.read()

# Hatta "INSERT INTO X VALUES (...)" formatini bul, X icin kolon listesi varsa ekle
def rewrite(m):
    tbl = m.group(1).lower()
    values = m.group(2)
    cols = tbl_cols.get(tbl)
    if not cols:
        return m.group(0)  # tablo bilgisi yok, dokunma
    return f"INSERT INTO {tbl} ({', '.join(cols)}) VALUES{values};"

# Match: INSERT INTO <table> VALUES (... ); where (...) can span balanced parens
pat = re.compile(
    r"INSERT\s+INTO\s+(\w+)\s+VALUES\s*(\([^()]*(?:\([^()]*\)[^()]*)*\))\s*;?",
    re.I
)
rewritten = pat.sub(rewrite, src)
n = sum(1 for _ in pat.finditer(src))
open(import_path, "w").write(rewritten)
print(f"  PATCH OK: {n} INSERT kolon-adlandirildi ({len(tbl_cols)} tablo seması parse edildi)")
PYEOF

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
               -o -name '*.gif' -o -name '*.png' -o -name '*.jpg' -o -name '*.jpeg' \
               -o -name '*.ico' -o -name '*.bmp' -o -name '*.svg' \
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

    # 3b. SWT swap: sadece SWT'nin kendisini sil, swtcalendar / swtjasperviewer
    # gibi 3rd-party widget'lar dist'te kalsin.
    rm -f "$STAGE/lib/swt.jar" "$STAGE/lib/swt-windows.jar" \
          "$STAGE/lib/swt-pi.jar" "$STAGE/lib/swt-nl.jar" \
          "$STAGE/lib/swt-mozilla.jar"
    mv "$STAGE/lib/swt.jar.new" "$STAGE/lib/swt.jar"
    find "$STAGE" -maxdepth 1 \( -name 'swt-*.dll' -o -name 'libswt-*.so' -o -name '*.jnilib' \) -delete

    # SWT jar Eclipse tarafindan imzali (META-INF/*.SF, *.RSA). Bizim stub
    # jar imzasiz oldugu icin "signer information does not match" Security
    # Exception aliyoruz (ayni `org.eclipse.swt.custom` paketinde mixed
    # signer). Cozum: SWT jar'dan imza dosyalarini sil — her ikisi de
    # unsigned olsun.
    zip -dq "$STAGE/lib/swt.jar" 'META-INF/*.SF' 'META-INF/*.RSA' 'META-INF/*.DSA' 'META-INF/*.EC' 2>/dev/null || true

    # 3b'. Modern JFace 3.34 + 3 stub class:
    #   Eski JFace 3.0 modern SWT 3.131 ile VerifyError veriyor (Event
    #   sinifi bytecode imza degisikligi). Modern JFace 3.34 SWT 3.131
    #   ile uyumlu ama bazi eski iç API'leri kaldirmis. Eski Turquaz
    #   kodunun referans verdigi 3 sinifin stub'ini ekleyerek aciklik
    #   kapatiyoruz.
    # JFace 3.14.0 (Eclipse Photon, 2018) — eski Turquaz'in bagimli oldugu
    # org.eclipse.jface.contentassist.* paketini (TextContentAssistSubjectAdapter,
    # SubjectControlContentAssistant, ISubjectControl*, vd.) HALA iceriyor.
    # JFace 3.20+'da bu paket kaldirildi. 3.14 + modern SWT 3.131 uyumlu;
    # public API stabil kalmis. Stub yaklasimimi tamamen tasinmali oluyor.
    JFACE_VERSION=${JFACE_VERSION:-3.14.0}
    rm -f "$STAGE/lib/jface.jar" "$STAGE/lib/jfacetext.jar" \
          "$STAGE/lib/runtime.jar" "$STAGE/lib/osgi.jar" \
          "$STAGE/lib/text.jar" "$STAGE/lib/boot.jar"
    # JFace 3.14.0 dependencies. Versiyon listesi: jface/jface.text 3.14.0,
    # text 3.10.0 (org/eclipse/jface/text/IDocument burada), core.commands +
    # equinox.common 3.x. Her dep'i ayri versiyonla cunku Eclipse Platform
    # aggregate versioning yapmiyor; her jar kendi semver'iyle release oluyor.
    declare -A JF_DEPS=(
        [org.eclipse.jface]="$JFACE_VERSION"
        [org.eclipse.jface.text]="$JFACE_VERSION"
        [org.eclipse.text]="3.10.0"
        [org.eclipse.core.commands]="3.9.0"
        [org.eclipse.core.runtime]="3.14.0"
        [org.eclipse.core.contenttype]="3.7.0"
        [org.eclipse.core.jobs]="3.10.0"
        [org.eclipse.equinox.common]="3.10.0"
        [org.eclipse.equinox.preferences]="3.7.100"
        [org.eclipse.equinox.registry]="3.8.0"
        [org.eclipse.osgi]="3.13.0"
    )
    for art in "${!JF_DEPS[@]}"; do
        v="${JF_DEPS[$art]}"
        JF_URL="$MVN_BASE/org/eclipse/platform/${art}/${v}/${art}-${v}.jar"
        JF_PATH="$STAGE/lib/${art}-${v}.jar"
        if curl -fsSL "$JF_URL" -o "$JF_PATH" 2>/dev/null; then
            # Eclipse Foundation imzasini sil — eski Turquaz jasperreports vs.
            # ayni paketi imzasiz iceriyorsa mixed signer SecurityException
            # atiyor (ornegin: org.eclipse.jface.text.IWidgetTokenKeeper).
            zip -dq "$JF_PATH" 'META-INF/*.SF' 'META-INF/*.RSA' 'META-INF/*.DSA' 'META-INF/*.EC' 2>/dev/null || true
        fi
    done

    # SWT 3.131'de kaldirilan TableTreeItem icin stub jar.
    # KRITIK: JFace 3.14'in OpenStrategy$1.setSelection bytecode'u
    # TableTreeItem'i Widget'a assign etmeyi deniyor — stub MUTLAKA
    # `extends Widget` (Item zaten Widget'i extend ediyor) ve TableTree
    # `extends Composite` olmalı, yoksa VerifyError. Dolayisiyla stub
    # gercek SWT classpath'i ile derlenmeli; JDK 8 javac modern SWT class
    # file v61'i okuyamaz, JDK 17 javac kullaniyoruz (Dockerfile'da
    # /opt/jdk17 mevcut).
    # Stub jar: önceden derlenmiş .class dosyaları docker/stubs/'ta. JDK 17
    # javac SWT 3.131'i (class file v61) okuyup TableTreeItem extends Item
    # bytecode'unu üretiyor. Lokal build env'inde JDK 17 yoksa fallback
    # olarak repo'daki precompiled class'lar kullanılır.
    STUB_BIN="$WORK/legacy-stubs-bin"
    rm -rf "$STUB_BIN"
    mkdir -p "$STUB_BIN"
    PRECOMPILED_STUBS="$SRC/docker/stubs"
    if [[ -d "$PRECOMPILED_STUBS/org" ]]; then
        cp -a "$PRECOMPILED_STUBS/org" "$STUB_BIN/"
        sub "stub class'lari docker/stubs/'tan kopyalandi"
    else
        # JDK 17 ile derle (build env'inde mevcut, ya da fallback javac)
        STUB_SRC="$WORK/legacy-stubs-src"
        rm -rf "$STUB_SRC"
        mkdir -p "$STUB_SRC/org/eclipse/swt/custom"
        cat > "$STUB_SRC/org/eclipse/swt/custom/TableTreeItem.java" <<'JSRC'
package org.eclipse.swt.custom;
import org.eclipse.swt.widgets.Item;
public class TableTreeItem extends Item {
    public TableTreeItem(TableTree parent, int style) { super(parent.getTable(), style); }
    public TableTreeItem(TableTree parent, int style, int index) { super(parent.getTable(), style); }
    public TableTreeItem(TableTreeItem parent, int style) { super(null, style); }
    public Object getData(String key) { return super.getData(key); }
    public void setData(String key, Object value) { super.setData(key, value); }
    public TableTreeItem[] getItems() { return new TableTreeItem[0]; }
}
JSRC
        cat > "$STUB_SRC/org/eclipse/swt/custom/TableTree.java" <<'JSRC'
package org.eclipse.swt.custom;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
public class TableTree extends Composite {
    public TableTree(Composite parent, int style) { super(parent, style); }
    public TableTreeItem[] getItems() { return new TableTreeItem[0]; }
    public Table getTable() { return null; }
}
JSRC
        JAVAC17="${JDK17_HOME:-/opt/jdk17}/bin/javac"
        [[ -x "$JAVAC17" ]] || JAVAC17="javac"
        "$JAVAC17" -source 8 -target 8 -nowarn -Xlint:none \
            -cp "$STAGE/lib/swt.jar" \
            -d "$STUB_BIN" \
            $(find "$STUB_SRC" -name "*.java") 2>&1 | tail -3 || true
    fi
    if [[ -d "$STUB_BIN/org" ]]; then
        (cd "$STUB_BIN" && jar cf "$STAGE/lib/turquaz-legacy-stubs.jar" org/)
        sub "turquaz-legacy-stubs.jar (TableTreeItem extends Item + TableTree extends Composite)"
    fi

    # 3b''. Adoptium Temurin JRE 17 bundle: orijinal Turquaz install4j ile
    # JRE'yi paketliyordu — kullanici sifir kurulumla calistirir, eski API'ler
    # (org.eclipse.core.internal.preferences vd.) modern runtime'da olmayan
    # sinif yollari problem olmaz.
    JRE_RELEASE=${JRE_RELEASE:-17.0.13_11}
    JRE_TAG=${JRE_TAG:-jdk-17.0.13%2B11}
    declare -A JRE_ARCHIVE
    JRE_ARCHIVE=(
        [linux]="OpenJDK17U-jre_x64_linux_hotspot_${JRE_RELEASE}.tar.gz"
        [linux-aarch64]="OpenJDK17U-jre_aarch64_linux_hotspot_${JRE_RELEASE}.tar.gz"
        [windows]="OpenJDK17U-jre_x64_windows_hotspot_${JRE_RELEASE}.zip"
        [macos]="OpenJDK17U-jre_x64_mac_hotspot_${JRE_RELEASE}.tar.gz"
        [macos-aarch64]="OpenJDK17U-jre_aarch64_mac_hotspot_${JRE_RELEASE}.tar.gz"
    )
    JRE_ARCH="${JRE_ARCHIVE[$tgt]:-}"
    if [[ -n "$JRE_ARCH" ]]; then
        JRE_BASE_URL="https://github.com/adoptium/temurin17-binaries/releases/download/${JRE_TAG}"
        JRE_URL="$JRE_BASE_URL/$JRE_ARCH"
        sub "JRE 17 indiriliyor: $JRE_ARCH"
        if curl -fsSL "$JRE_URL" -o "$STAGE/jre.archive"; then
            if [[ "$JRE_ARCH" == *.zip ]]; then
                unzip -q "$STAGE/jre.archive" -d "$STAGE/"
            else
                tar -C "$STAGE" -xzf "$STAGE/jre.archive"
            fi
            rm -f "$STAGE/jre.archive"
            EXTRACTED=$(find "$STAGE" -maxdepth 1 -name "jdk-17*-jre" -type d | head -1)
            if [[ -n "$EXTRACTED" ]]; then
                mv "$EXTRACTED" "$STAGE/jre"
                sub "JRE 17 bundled: $STAGE/jre"
            fi
        else
            err "$tgt: JRE 17 indirme basarisiz"
        fi
    fi

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
# Bundled JRE 17 (Adoptium Temurin); kullanici sistem Java'sini gerektirmez.
JAVA=./jre/bin/java
if [ ! -x "\$JAVA" ]; then JAVA=java; fi
exec "\$JAVA" $JAVA_OPTS_COMMON -jar run.jar "\$@"
LAUNCH
            chmod +x "$STAGE/turquaz.sh"
            ;;
        macos*)
            cat > "$STAGE/turquaz.sh" <<LAUNCH
#!/usr/bin/env sh
DIR=\$(cd "\$(dirname "\$0")" && pwd)
cd "\$DIR"
# Mac JRE archive: Contents/Home/bin/java
JAVA=./jre/Contents/Home/bin/java
if [ ! -x "\$JAVA" ]; then JAVA=java; fi
exec "\$JAVA" -XstartOnFirstThread \\
    -Dapple.awt.UIElement=true \\
    -Dorg.eclipse.swt.internal.cocoa.useNSCellEditing=false \\
    $JAVA_OPTS_COMMON \\
    -jar run.jar "\$@"
LAUNCH
            chmod +x "$STAGE/turquaz.sh"
            ;;
        windows)
            cat > "$STAGE/turquaz.bat" <<LAUNCH
@echo off
cd /d "%~dp0"
set JAVA=.\jre\bin\java.exe
if not exist "%JAVA%" set JAVA=java
"%JAVA%" $JAVA_OPTS_COMMON -jar run.jar %*
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
