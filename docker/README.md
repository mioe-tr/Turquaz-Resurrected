# Turquaz Standalone — Resurrected build environment

Bu Docker yığını, 2005–2010 dönemi **orijinal Turquaz Financial Accounting**
(`0.8.x`) kaynak kodunu — Hüseyin Erkin, Önsel Armağan ve Cem Dayanık'ın
emeğini — modern Eclipse SWT 3.124 ile yeniden derler ve Linux, Windows,
macOS (x86_64 + Apple Silicon dahil) için dağıtılabilir paketler üretir.

Felsefe: **modernleştirirken aynı zamanda diriltmek**. Kaynak kodu sıfır
ya da minimum değişiklikle korumayı, dış katmanları (Java runtime, SWT
versiyonu, paketleme) ise modern dünyaya getirmeyi hedefler.

## Hızlı başlangıç

Bu branch'in (legacy/standalone-resurrected) kökünden:

```bash
# 1. Build image'ı üret (~700 MB; bir kez)
docker build -t turquaz-legacy-build docker/

# 2. Tüm hedef OS'ler için artefakt üret (~30 sn)
docker run --rm \
    -v "$PWD":/src:ro \
    -v "$PWD/dist":/out \
    turquaz-legacy-build

# 3. Çıktı:
ls dist/
# turquaz-standalone-resurrected-linux.tar.gz          (15 MB)
# turquaz-standalone-resurrected-linux-aarch64.tar.gz
# turquaz-standalone-resurrected-windows.zip
# turquaz-standalone-resurrected-macos.tar.gz
# turquaz-standalone-resurrected-macos-aarch64.tar.gz
```

Açtıktan sonra Linux/Mac'te `./turquaz.sh`, Windows'ta `turquaz.bat` —
**Java 17+ kurulu olmalı** (Adoptium Temurin önerilir).

## Yapılandırma

```bash
docker run --rm -v "$PWD":/src:ro -v "$PWD/dist":/out \
    -e TARGETS=linux,windows \
    -e SWT_VERSION=3.124.100 \
    -e ENCODING=ISO-8859-9 \
    turquaz-legacy-build
```

| Env var | Varsayılan | Açıklama |
|---------|------------|----------|
| `TARGETS` | `linux,linux-aarch64,windows,macos,macos-aarch64` | Üretilecek paketler |
| `SWT_VERSION` | `3.124.0` | Maven Central'dan çekilen Eclipse SWT |
| `ENCODING` | `ISO-8859-9` | Kaynak kodun karakter kodlaması (Türkçe Latin-5) |

## Build pipeline (build.sh ne yapıyor?)

1. **Kaynak kopyalama**: 5 modülü (`TurquazCommon`, `TurquazServer`,
   `TurquazBusinessLogic`, `TurquazStandAlone`, `TurquazClient`) `/src`'ten
   çalışma dizinine kopyalar.
2. **Eski binary temizliği**: 2005 dağıtımıyla gelmiş `turquaz-*.jar`
   kalıntılarını siler (yoksa javac classpath'inde aşılan eski sürümleri
   bulur ve "method not found" hatası verir).
3. **Java 9+ patch**: `Starter.java`'daki `new URLClassLoader(urls, null)`
   ifadesini `ClassLoader.getSystemClassLoader()` parent'ı ile değiştirir
   (Java 9+ modüler runtime'da `java.sql.SQLException` gibi platform
   modüllerini görebilmek için).
4. **Derleme**: Her modülü doğru bağımlılık sırasında derler
   (Common → Server → BL → StandAlone → Client).
5. **Resource taşıma**: `*.properties` (Türkçe/İngilizce i18n bundle'ları),
   `*.hbm.xml` (Hibernate mapping), `*.gif`/`*.png` (ikon) ve diğer
   non-Java kaynakları `bin/` ağaçlarına taşır (javac sadece `.java` ile
   ilgilenir).
6. **Ant build**: Orijinal `TurquazStandAlone/build.xml`'i Ant ile koşar;
   `turquaz-{standalone,client,common,business}.jar` + `run.jar` + `dbgui.jar`
   üretilir.
7. **Per-platform paketleme**: Her hedef için:
   - Maven Central'dan `org.eclipse.swt.<platform>-3.124.0.jar` indir
   - `dist/lib/swt.jar` olarak kur (yeni SWT native lib'leri jar içinde)
   - Eski 32-bit `.dll`/`.so` dosyalarını sil
   - Platform launcher (`turquaz.sh` veya `turquaz.bat`) yaz
   - `BUILD-INFO.txt` ekle
   - `.tar.gz` veya `.zip` olarak paketle

## Mevcut durum (doğrulanmış)

Lokal test (`xvfb-run` altında Java 21 ile başlatma):

| Aşama | Durum |
|-------|-------|
| 5 modülü derleme | ✅ 1184 sınıf, 0 hata |
| `turquaz-*.jar` üretimi (Ant) | ✅ BUILD SUCCESSFUL |
| Cross-platform paketleme | ✅ 5/5 platform, 15 MB her biri |
| Custom URLClassLoader bootstrap | ✅ (patch sonrası) |
| SWT 3.124 native lib yükleme | ✅ |
| GUI Display açılışı | ✅ |
| Türkçe i18n bundle yükleme | ✅ |
| Hibernate 3.0.3 Configuration init | ✅ |
| Hibernate `.hbm.xml` mapping yüklemek | ❌ DTD resolver patlıyor |
| Veritabanı oturum + ilk ekran | ⏳ Henüz |

## Sıradaki adımlar (geliştirme yol haritası)

Build pipeline tamamlanmış. Çalışan bir GUI'ye varmak için kalan sürtünme
20 yıllık Hibernate 3.0.3'ün Java 21 ile uyum problemi:

```
org.dom4j.DocumentException: Error on line 1 of document
http://hibernate.sourceforge.net/hibernate-mapping-2.0.dtd
```

Hibernate 3.0.3, DOCTYPE'ı internet'ten çekmeye çalışıyor; modern dom4j
sürümünde de DTD lokal cache stratejisi farklılaştı. Çözüm seçenekleri:

1. **Hibernate 3.0.3 → 3.6.10 bump (önerilen)** — Maven Central'da
   `org.hibernate:hibernate-core:3.6.10.Final` mevcut. DTD'ler jar içinde
   düzgün resolve edilir, Java 8–17 ile çalışır. Mapping XML şeması
   geriye uyumlu. `TurquazServer/lib/hibernate/hibernate3.jar`'ı değiştir.
2. **Local DTD resolver** — `EngDALSessionFactory.<init>` öncesi global
   bir `org.xml.sax.EntityResolver` kur; `hibernate-mapping-2.0.dtd` ve
   `hibernate-configuration-3.0.dtd`'leri classpath kaynağı olarak sun.
3. **`-Dorg.xml.sax.driver=...` + `-Dhttp.proxyHost=`** workaround'u —
   kırılgan.

Sonraki adımlar (tahmini sırayla):
- [ ] Hibernate 3.6.10 swap → Configuration başarısı
- [ ] HSQLDB 1.x → 2.7 migrate (dist/database/turquaz.* formatı yükselir)
- [ ] İlk ekran açılır mı doğrula (login + module list)
- [ ] CGLIB 2.x → 3.x bump (Hibernate 3.6.10 zaten yeni CGLIB ile gelir)
- [ ] Mac aarch64 üzerinde el ile sınama
- [ ] CI: GitHub Actions matrix build (Linux/Win/Mac runner'ları)

## Bilinen sürtünmeler

- **Hibernate 3.0.3 + Java 17/21**: DTD resolver başlatamıyor (yukarıda).
- **CGLIB 2.x reflection**: `--add-opens` flag'leri Launcher'larda zaten
  ekli (`java.base/java.lang`, `java.lang.reflect`, `java.util`).
- **SWT 3.124 minimum Java 17**: Java 8 launcher denemesi
  `UnsupportedClassVersionError` verir; bilinçli karar.
- **`Invalid image` uyarıları**: `icons/Ok24.gif` vb. dosyalar eski SWT
  versiyonu için format spesifik; modern SWT okuyamıyor. Uygulama
  bunlarsız da çalışır (sadece toolbar ikonları kayıp).
- **DBus session manager** uyarıları: container içinde
  GNOME/Xfce session yok; SWT bunsuz çalışır, uyarı sadece.

## Geliştirme akışı

Kaynak kodu düzenle (`TurquazClient/src/...`), Docker'ı tekrar koştur,
güncellenmiş paketler. Build ortamı tekrarlanabilir, native lib indirme
yok. Eclipse IDE ile düzenleme için modüllerdeki `.classpath` dosyaları
hâlâ duruyor; "Import Existing Projects" akışı çalışır.

## Mimari notlar

- **Strategy pattern**: `ServiceCaller` interface'i (TurquazCommon), iki
  implementasyon — `ServiceCallerInternet` (HTTP servlet RPC, TurquazClient)
  ve `ServiceCallerStandalone` (in-process Hibernate). Standalone modunda
  UI doğrudan Hibernate session ile konuşur.
- **Reflection-driven RPC**: `turq_services` tablosundan `(class, method)`
  okur, runtime'da reflection ile invoke eder.
- **Embedded HSQLDB**: `dist/database/turquaz.*` dosyaları olarak gömülü;
  ayrıca `dist/config/postgresql/database.properties` ile harici PostgreSQL
  seçeneği.
- Ayrıntılar için bakınız `legacy/standalone-resurrected` branch'inin
  kök `README.md`'si ve TurquazStandAlone modülünün mimari incelemesi.

## Lisans

Orijinal Turquaz **GPLv2** ile dağıtılmıştı; bu reprodüksiyon ve build
katmanı da GPLv2-uyumlu kalır. Modern Turquaz Resurrected (ana branch)
GPLv3.
