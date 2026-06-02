# Turquaz e-Belge Entegrasyonu (GİB e-Arşiv / e-Fatura)

Bu dizin, Turquaz'a GİB e-belge gönderme yeteneği ekleyen çalışmanın
dokümantasyonudur. Tasarım kararları ve maliyet/mevzuat araştırması için
[`PLAN.md`](PLAN.md) dosyasına bakın.

## Yaklaşım

- **Özel entegratör API** modeli: Turquaz entegratör *olmaz*; lisanslı bir
  entegratörün (ilk olarak **Nilvera**) API'sini tüketir. Mali mühür, 7/24
  altyapı ve GİB uyumu entegratördedir.
- **Sağlayıcı-bağımsız katman:** tüm entegratörler tek bir
  `EInvoiceProvider` arayüzünü uygular; uygulamanın geri kalanı hangi
  entegratörle çalışıldığını bilmez.
- **Manuel "Kes" akışı:** fatura uygulamada normal şekilde oluşturulur (DRAFT),
  sonra kullanıcı **"e-Arşiv Kes"** aksiyonuyla gönderir/kesinleştirir.

## Modül haritası (`com.turquaz.einvoice`, `TurquazBusinessLogic` içinde)

| Paket | Sorumluluk |
|---|---|
| `model` | Sağlayıcı-bağımsız belge modeli (`EInvoice`, `EInvoiceLine`, `EInvoiceParty`), `EInvoiceType`, `EDocStatus`, `EInvoiceResult` |
| `provider` | `EInvoiceProvider` arayüzü, `ProviderCredentials`, `EInvoiceProviderRegistry`, `NilveraProvider` |
| `routing` | `EInvoiceRouter` — belge tipini aktif entegratör + seriye yönlendirir |
| `config` | `ProviderProfile`, `EInvoiceSettings` (çoklu entegratör + seri doğrulama + satıcı kimliği) |
| `map` | `TurqInvoiceMapper` — `TurqCurrentTransaction` (+ stok satırları/KDV) → `EInvoice` |
| `dal` | `EinvoiceDAL` — fatura/şirket yükleme, durum bulma/saklama (mevcut `EngDALSessionFactory`/`EngDALCommon`) |
| `bl` | `EinvoiceBLIssue` — "Kes" orkestrasyonu (mükerrer koruması dahil) |
| `util` | `HttpJson` (Java 8 uyumlu `HttpURLConnection`), `Json` (minimal, bağımlılıksız) |

Kalıcılık katmanı: `TurqEInvoiceStatus` entity'si
(`com.turquaz.engine.dal`) her belgenin durum/ETTN/entegratör/seri/yanıt
bilgisini tutar; `turq_einvoice_status` tablosu `hbm2ddl.auto=update` ile
otomatik oluşur.

## Yeni entegratör eklemek

1. `EInvoiceProvider` arayüzünü uygulayan yeni bir sınıf yaz
   (`provider/IzibizProvider.java` gibi).
2. `EInvoiceProviderRegistry` statik bloğunda `register(new IzibizProvider())`.
3. Ayar ekranında profil oluştururken bu entegratör seçilebilir hale gelir.

Mevcut `NilveraProvider`, REST + Bearer kimlik doğrulama ve e-Arşiv gönderimi
için referans uygulamadır.

## Çoklu entegratör (resmi durum)

VUK 509 / TURMOB kılavuzuna göre bir mükellef **aynı anda birden fazla özel
entegratörle** çalışabilir; koşul: her entegratörde **farklı GB-PK etiketi +
ayrı fatura serisi**. Farklı belge tipleri farklı entegratörlere yönlendirilebilir
(serbest). Aynı belge tipinde GİB Portal + entegratör birlikte kullanılamaz.

Uygulama bunu şöyle destekler: belge-tipi başına aktif profil (`EInvoiceSettings`
yönlendirme tablosu) + profil başına ayrı seri; `EInvoiceSettings.validate()`
aynı adaptör+seri çakışmasını (GİB hata 1104/1163) engeller.

## Derleme notu

Build `javac -source 8 -target 8` ile derlediğinden Java 11 `HttpClient`
kullanılmaz; bunun yerine gömülü `HttpURLConnection` (Java 8) tercih edilmiştir.
Yeni `.java` dosyaları mevcut modül `src` ağaçlarına eklendiğinden
`docker/build.sh` (find tabanlı javac) tarafından otomatik derlenir.

## Durum

- [x] Faz 0 — branch `legacy/standalone-resurrected` kodu üzerine kuruldu
- [x] Faz 1 — `TurqEInvoiceStatus` entity + mapping + cfg kaydı
- [x] Faz 2 — sağlayıcı-bağımsız katman (model, provider, routing, config, util)
- [x] Faz 3 — `TurqInvoiceMapper` (fatura → EInvoice, KDV dahil) + `EinvoiceBLIssue` ("Kes" orkestrasyonu) + `EinvoiceDAL`
- [ ] Faz 4 — SWT UI: "e-Arşiv Kes" + durum sütunu + çoklu entegratör ayar ekranı
- [ ] Faz 5 — ikinci entegratör adaptörü + uçtan uca test (Nilvera test ortamı)
