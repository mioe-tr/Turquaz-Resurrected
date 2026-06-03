# Turquaz Standalone — Resurrected

> **Not (e-belge entegrasyon dalı):** Bu branch
> (`claude/einvoice-api-integration-6ylE8`) `legacy/standalone-resurrected`
> kodu üzerine kuruludur ve GİB e-Arşiv/e-Fatura entegrasyonu (özel entegratör
> API'leri için sağlayıcı-bağımsız katman) eklemeyi hedefler. Tasarım:
> `docs/einvoice/PLAN.md`.

Bu branch, orijinal **Turquaz Financial Accounting** (2004–2010) kaynak kodunu
modern toolchain ile derleyip Linux/Windows/macOS (x86_64 + Apple Silicon)
için paketleyen "diriliş" branch'idir. CVS'den Git'e dönüştürülmüş tam
sürüm tarihi + SourceForge release tarball'ları + Docker tabanlı build yığını
bir arada.

## Hızlı kullanım

```bash
docker build -t turquaz-legacy-build docker/
docker run --rm -v "$PWD":/src:ro -v "$PWD/dist":/out turquaz-legacy-build
ls dist/
# turquaz-standalone-resurrected-linux.tar.gz          (15 MB)
# turquaz-standalone-resurrected-linux-aarch64.tar.gz
# turquaz-standalone-resurrected-windows.zip
# turquaz-standalone-resurrected-macos.tar.gz
# turquaz-standalone-resurrected-macos-aarch64.tar.gz
```

Ayrıntılı dökümantasyon: [`docker/README.md`](docker/README.md).

## Branch içeriği

### Kaynak kod (CVS'den dönüştürülmüş, alt dizinler)

5 alt modül (Turquaz2 dönemi, 2007–2010):

- `TurquazCommon/`     — paylaşılan sabitler, i18n, RPC kontratları
- `TurquazServer/`     — servlet + servis mapper
- `TurquazBusinessLogic/` — XxxBL + XxxDAL iş kuralları (Hibernate 3)
- `TurquazStandAlone/`    — in-process Hibernate bootstrap, ServiceCaller
- `TurquazClient/`        — SWT istemcisi (büyük modül, ~454 .java)

Plus eski monolitik (Turquaz1 dönemi, 2004–2005):

- `Turquaz/`           — tek Eclipse projesi içinde her şey, 1831 commit

Her birinin orijinal commit zaman damgaları ve yazarları (`onsel`,
`cemdayanik`, `huseyiner`, `cem`, `huseyin`, `ehad`, `erhanb`) korundu.

### SourceForge release tarball'ları (`releases/`)

5 sürüm Linux dağıtım içeriği:

- `releases/0.4.2_alpha4_YTL/`     (2004-12-20)
- `releases/0.6.0_Alpha5/`         (2005-02-18)
- `releases/0.7.0_Beta2/`          (2005-04-08)
- `releases/0.7.0_Beta4_TURKISH/`  (2005-04-15)
- `releases/0.8.1_Beta_5/`         (2006-05-06)

Her biri orijinal release tarihiyle commit edildi; gömülü JRE'ler hariç
tutuldu (yer kazancı için).

### Docker build yığını (`docker/`)

- `docker/Dockerfile`  — Eclipse Temurin JDK 11 + Ant + curl tabanlı build env
- `docker/build.sh`    — 5 modül derleme + Ant + SWT swap + paketleme
- `docker/README.md`   — kullanım, yapılandırma, build pipeline, yol haritası

## Diğer ilgili branch'ler

| Branch | İçerik |
|--------|--------|
| `main` | Modern Turquaz Resurrected — Spring Boot + React + modern SWT |
| `legacy/cvs-Turquaz` | Eski monolitik proje CVS history (1831 commit) |
| `legacy/cvs-TurquazBusinessLogic` | BL modülü izole CVS history |
| `legacy/cvs-TurquazClient` | Client modülü izole CVS history |
| `legacy/cvs-TurquazCommon` | Common modülü izole CVS history |
| `legacy/cvs-TurquazServer` | Server modülü izole CVS history |
| `legacy/cvs-TurquazStandAlone` | StandAlone modülü izole CVS history |
| `legacy/cvs-import` | 6 CVS modülünün birleşik history (1860 commit) |
| `legacy/sourceforge-releases` | `cvs-import` + 5 release tarball commit |
| `legacy/standalone-resurrected` | **Bu branch** — `sourceforge-releases` + Docker build yığını |

## Durum

✅ Build pipeline tamamlanmış: 5 hedef OS için cross-platform paketleme,
modern SWT, Java 17+ runtime, Hibernate Configuration init noktasına kadar
sınanmış. Geri kalan (Hibernate 3.0.3 DTD resolver patch'i, HSQLDB 2.x
migrasyonu) `docker/README.md`'deki yol haritasında.

Felsefe: **diriltirken aynı zamanda modernleştirmek**. Orijinal kaynak
kodu en az değişiklikle koru, dış katmanları (JRE, SWT, paketleme) çağa
getir.

## Lisans

Orijinal Turquaz **GNU GPL v2**. Bu reprodüksiyon ve build yığını da
GPLv2-uyumlu kalır.
