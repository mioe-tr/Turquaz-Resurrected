# Turquaz Resurrected

Turquaz Financial Accounting Software (2003–2010, GPLv2) için arşiv + diriliş
deposu. Türkiye'de yazılmış ilk açık kaynak ön muhasebe yazılımlarından birini
modern toolchain ile yeniden çalışır hâle getirme ve tarihi koruma projesi.

Orijinal kod CVS'den Git'e dönüştürüldü; SourceForge release tarball'ları
arşivlendi; modern SWT 3.131 + Java 17 + HSQLDB 2.x ile derleme ortamı kuruldu.

---

## Hemen çalıştırmak istiyorum

Doğrudan [`legacy/standalone-resurrected`](../../tree/legacy/standalone-resurrected)
branch'ine git — Docker tabanlı build yığını 5 platform için paket üretiyor.

```bash
git checkout legacy/standalone-resurrected
docker build -t turquaz-legacy-build docker/
docker run --rm -v "$PWD":/src:ro -v "$PWD/dist":/out turquaz-legacy-build
ls dist/
```

Çıktı:

- `turquaz-standalone-resurrected-linux.tar.gz`
- `turquaz-standalone-resurrected-linux-aarch64.tar.gz`
- `turquaz-standalone-resurrected-windows.zip`
- `turquaz-standalone-resurrected-macos.tar.gz`
- `turquaz-standalone-resurrected-macos-aarch64.tar.gz`

Her paket gömülü JRE 17 + modern SWT içerir; harici Java kurulumu gerektirmez.

---

## Branch haritası

Bu deponun branch'leri farklı amaçlara hizmet ediyor; her birinin commit
geçmişi bağımsız (CVS'den ayrı ayrı dönüştürüldüğü için aralarında ortak ata
yok — bu yüzden GitHub'da `main`'e PR açılamaz, doğrudan branch'i ziyaret et).

### Diriliş (çalışan kod)

| Branch | İçerik |
|--------|--------|
| ⭐ [`legacy/standalone-resurrected`](../../tree/legacy/standalone-resurrected) | Orijinal kaynak kod + Docker build yığını + modern SWT/JFace/Hibernate swap + Java 17 runtime. **Burada başla.** |

### Arşiv / arkeoloji

| Branch | İçerik |
|--------|--------|
| [`legacy/sourceforge-releases`](../../tree/legacy/sourceforge-releases) | 5 SourceForge release tarball'ı (0.4.2 → 0.8.1 Beta 5, 2004–2006), `cvs-import` üzerine eklendi. |
| [`legacy/cvs-import`](../../tree/legacy/cvs-import) | 6 CVS modülünün birleşik history'si — toplam **1860 commit**, 2004–2010. |
| [`legacy/cvs-Turquaz`](../../tree/legacy/cvs-Turquaz) | Eski monolitik proje (Turquaz1 dönemi, 2004–2005, **1831 commit**). |
| [`legacy/cvs-TurquazCommon`](../../tree/legacy/cvs-TurquazCommon) | Paylaşılan sabitler, i18n, RPC kontratları (Turquaz2). |
| [`legacy/cvs-TurquazServer`](../../tree/legacy/cvs-TurquazServer) | Servlet + servis mapper. |
| [`legacy/cvs-TurquazBusinessLogic`](../../tree/legacy/cvs-TurquazBusinessLogic) | XxxBL + XxxDAL iş kuralları (Hibernate 3). |
| [`legacy/cvs-TurquazStandAlone`](../../tree/legacy/cvs-TurquazStandAlone) | In-process Hibernate bootstrap, ServiceCaller. |
| [`legacy/cvs-TurquazClient`](../../tree/legacy/cvs-TurquazClient) | SWT istemcisi (büyük modül, ~454 .java). |

### Gelecek

| Branch | İçerik |
|--------|--------|
| `main` | (planlanan) Modern rebuild — Spring Boot + React veya modernize edilmiş SWT. Şu an boş; bu README "harita" olarak duruyor. |

---

## Orijinal geliştiriciler

2004–2010 arası CVS commit'lerinden çıkarılmış katkı dağılımı. Hepsine teşekkür
— bu kod onların eseri; "diriliş" tarafı sadece runtime'ı çağa taşıyor.

| Geliştirici | Commit | Aktif dönem |
|-------------|--------|-------------|
| **onsel** | 1038 | 2004-08 → 2005-06 |
| **cem** / **cemdayanik** | 1193 | 2004-10 → 2005-06 |
| **huseyin** / **huseyiner** | 346 | 2004-09 → 2010-01 (en uzun) |
| **ehad** | 10 | 2004-11 → 2004-12 |
| **erhanb** | 8 | 2005-04 → 2005-04 |

Orijinal proje 2003'te kurulmuş, 2010'a kadar aktif geliştirildi. SourceForge
deposu: https://sourceforge.net/projects/turquaz/

---

## Felsefe

**Diriltirken aynı zamanda modernleştirmek.** Orijinal kaynak kodu en az
değişiklikle koru, dış katmanları (JRE, SWT, paketleme, build araçları) çağa
getir. Kod arkeolojisi + canlı uygulama bir arada.

---

## Lisans

Orijinal Turquaz: **GNU GPL v2 (veya sonrası)**.
Bu reprodüksiyon, build yığını ve tüm türev branch'ler GPLv2-uyumlu kalır.

Detay: [LICENSE](LICENSE) (orijinal projeden korunmuş).
