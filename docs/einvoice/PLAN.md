# Plan: Turquaz Resurrected — GİB e-Belge (e-Arşiv/e-Fatura) Entegrasyonu

## Bağlam (neden?)

Türkiye'de GİB e-dönüşüm zorunlulukları genişliyor: **1 Ocak 2026'dan itibaren
e-Arşiv eşiği fiilen kalktı** → neredeyse tüm faturalar elektronik kesilmek
zorunda. 3.000.000 TL ciro üstü mükellefler e-Fatura. Bir ön muhasebe yazılımının
(Turquaz) bugün anlamlı olabilmesi için GİB e-belge ekosistemine bağlanması şart.

Turquaz açık kaynak, masaüstü uygulama (SWT + Java 17 runtime + HSQLDB, kod
`legacy/standalone-resurrected` branch'inde). Bu plan, kullanıcı kararları
doğrultusunda **çok-entegratörlü, sağlayıcı-bağımsız bir e-belge katmanı** ekler;
ilk teslimatta **e-Arşiv fatura**yı çalışır hale getirir, e-Fatura/e-İrsaliye için
zemin hazırlar.

### Temel iş akışı kararı (kullanıcı talebi)
Faturalar **otomatik kesilmez**. Akış: kullanıcı faturayı **uygulamada normal
şekilde oluşturur/kaydeder (TASLAK)** → hazır olduğunda fatura ekranındaki
**"e-Arşiv Kes" / "e-Fatura Kes"** butonuna basar → belge seçili entegratöre
gönderilip **kesinleştirilir (SENT/ACCEPTED)**. Yani kesme manuel, kullanıcı
tetiklemeli bir aksiyondur.

---

## 1. Türkiye e-belge ortamı — kim, nasıl, kaça

### Yöntemler ve "kim yapabilir"
| Yöntem | Açıklama | Kimler için |
|---|---|---|
| GİB Portal (manuel) | Ücretsiz web, elle giriş, API yok | Çok düşük hacim |
| **Özel Entegratör API** | Lisanslı entegratörün REST/SOAP API'si; mali mühür, 7/24 altyapı, GİB uyumu onlarda | **Bizim için tek gerçekçi yol — seçildi** |
| Doğrudan Entegrasyon | Aracısız GİB; ISO + TÜBİTAK onayı; 100-400K TL | Çok yüksek hacim |
| Entegratör OLMAK | 500K+ TL, sertifikasyon, 12-24 ay | Uygun değil |

Turquaz **entegratör olmaz**; lisanslı entegratör(ler)in API'sini tüketir. Son
kullanıcı (mükellef) entegratörle sözleşme + mali mühür sahibi olur.

### Geliştirici API'si olan başlıca entegratörler (sağlayıcı katmanında hedefleyeceğimiz)
- **Nilvera** — REST + OAuth2, modern doküman, ayrık test ortamı → **ilk implementasyon**
- **İzibiz (Logo)** — REST, Postman koleksiyonu
- **Uyumsoft** — SOAP/WSDL, olgun
- **Foriba/Sovos**, **EDM**, **Paraşüt**, **Turkcell e-Şirket**, **Digital Planet** — sonraki adaptörler
- Resmi liste: ebelge.gib.gov.tr/efaturaozelentegratorlerlistesi.html

### Maliyet (2025-2026 yaklaşık)
| Kalem | Tutar |
|---|---|
| Mali mühür (3 yıl, tüzel) | ~1.740 TL — **son kullanıcı öder** |
| Kontör (pay-as-you-go) | belge başına ~0.75–1.5 TL |
| Aylık paket (KOBİ) | ~150–800 TL/ay |
| **Yazılım (biz) tarafı** | **0 TL doğrudan** — sadece geliştirme eforu |

---

## 2. Çoklu entegratör — resmi durum ve çözümümüz (kullanıcı talebi)

**Araştırma sonucu (VUK 509 / TURMOB Özel Entegrasyon Kılavuzu / GİB Forum):**

| Senaryo | Resmî? | Koşul |
|---|---|---|
| **Aynı belge tipi, aynı anda 2+ entegratör (aynı VKN)** | ✅ Evet | Her entegratörde **farklı GB-PK etiketi + AYRI fatura serisi** zorunlu |
| Aynı belge tipi: GİB Portal + entegratör birlikte | ❌ Hayır | Entegratör aktifleşince portal otomatik kapanır |
| **Farklı belge tipleri farklı entegratörlerde** (e-Fatura A, e-Defter B) | ✅ Evet | Bağımsız sistemler |
| Entegratör değiştirme | ✅ Evet | Lock-in yok, 1-3 iş günü |

**Riskler:** Aynı seri iki entegratörde kullanılırsa ETTN/numara çakışması →
GİB hata **1163 (mükerrer)** / **1104 (numara tekrarı)**. Muhasebeciler aynı belge
tipi için tek entegratör önerir; ama mevzuat çokluya izin veriyor.

**Yazılım çözümümüz (bu mimarinin tam karşılığı):**
1. **Çoklu entegratör profili:** Kullanıcı birden çok entegratör tanımlayıp ayar
   girebilir (API anahtarı, GB-PK etiketi, ortam test/canlı).
2. **Belge-tipi başına yönlendirme:** Her belge tipi (e-Arşiv / e-Fatura /
   e-İrsaliye) için "aktif entegratör" seçilir → farklı belge tipi farklı
   entegratöre gidebilir (resmen serbest).
3. **Entegratör başına ayrı fatura serisi:** Aynı belge tipinde birden fazla
   entegratör kullanılmak istenirse, uygulama her entegratöre **ayrı seri/sıra
   numarası** atayarak çakışmayı (1104/1163) engeller. ETTN her belgede `UUID` ile
   benzersiz üretilir/saklanır.
4. **Entegratör seçme/değiştirme:** Ayarlardan aktif entegratör değiştirilebilir;
   eski belgelerin hangi entegratörle/seriyle gönderildiği kayıtta tutulur.
5. Varsayılan/önerilen mod: belge tipi başına tek entegratör (basit, uyumlu);
   çoklu mod isteğe bağlı ve seri ayrımı zorunlu kılınarak açılır.

---

## 3. Seçilen yaklaşım (kararlar özeti)
- **Yöntem:** Özel entegratör API; **tüm bilinen entegratörler için sağlayıcı
  (provider) katmanı**, ilk implementasyon **Nilvera**
- **İlk kapsam:** **e-Arşiv Fatura** (manuel "Kes" akışıyla)
- **Mimari:** Uygulama içi yeni Java modülü `com.turquaz.einvoice`
- **Çoklu entegratör:** Belge-tipi başına yönlendirme + entegratör başına ayrı seri
- **Branch:** `claude/einvoice-api-integration-6ylE8`, **`legacy/standalone-resurrected` üzerine**

---

## 4. Mevcut kod durumu ve boşluklar
**Hazır:** Fatura başlığı `TurqCurrentTransaction`
(`TurquazServer/src/ejb/com/turquaz/engine/dal/TurqCurrentTransaction.hbm.xml`),
satırlar `TurqCurrentTransactionBill`, cari `TurqCurrentCard`, iş kuralı deseni
`TurquazBusinessLogic/src/com/turquaz/current/bl/CurBLCurrentTransactionAdd.java`,
config `TurquazServer/src/conf/hibernate.cfg.xml` (`hbm2ddl.auto=update` →
şema otomatik göç eder), build `docker/build.sh` (modülleri sırayla `javac`).

**Boşluklar:**
- ❌ **KDV satır bazında yok** (UBL/e-Arşiv için zorunlu — en kritik eksik)
- ❌ Cari kartta VKN/TCKN, vergi dairesi, ülke/ilçe/posta/e-posta yok
- ❌ Modern HTTP yok (Java 17 `java.net.http.HttpClient` — ek bağımlılık gerekmez)
- ❌ JSON yok (entegratör REST için)
- ❌ e-belge durum/ETTN/seri/entegratör/yanıt saklama yok
- Mali mühür/imza/zarf: özel entegratör modelinde **gerekmez** (entegratörde)

---

## 5. Mimari — yeni modül `com.turquaz.einvoice`

```
com.turquaz.einvoice
├── model/      EInvoice, EInvoiceLine, EInvoiceParty, EInvoiceStatus(enum: DRAFT/SENT/ACCEPTED/REJECTED/CANCELLED/ERROR)
├── provider/   EInvoiceProvider (arayüz: submit, queryStatus, cancel, getDocumentPdf)
│               NilveraProvider (ilk impl); IzibizProvider/UyumsoftProvider (sonraki — aynı arayüz)
│               EInvoiceProviderRegistry (kayıtlı sağlayıcılar)
├── routing/    EInvoiceRouter — belge tipi → aktif entegratör + seri seçimi
├── map/        TurqInvoiceMapper — TurqCurrentTransaction → EInvoice (+ KDV hesabı)
├── bl/         EinvoiceBLIssue — "Kes" orkestrasyonu: maple → gönder → durumu DB'ye yaz
├── dal/        EinvoiceDALStatus (yeni hbm entity CRUD)
└── config/     EInvoiceSettings — entegratör profilleri, anahtar, etiket, seri, ortam
```

**Sağlayıcı-bağımsızlık:** Tüm entegratörler aynı `EInvoiceProvider` arayüzünü
uygular; `EInvoiceRouter` belge tipine göre doğru sağlayıcıyı + seriyi seçer.
Yeni entegratör eklemek = yeni `XxxProvider` sınıfı + ayar formu satırı.

**"Kes" akışı:** UI "e-Arşiv Kes" → `EinvoiceBLIssue` → `EInvoiceRouter` aktif
entegratörü/seriyi belirler → `TurqInvoiceMapper` belgeyi (KDV dahil) hazırlar →
`NilveraProvider.submit()` test ortamına gönderir → dönen ETTN/durum/PDF
`TurqEInvoiceStatus`'a yazılır → UI durumu gösterir.

---

## 6. Şema değişiklikleri (`hbm2ddl.auto=update` ile otomatik göç)

1. **Yeni entity `TurqEInvoiceStatus`** (`turq_einvoice_status`): id, FK
   `current_transactions_id`, `document_type` (EARSIV/EFATURA/EIRSALIYE),
   `provider` (entegratör adı), `invoice_series`, `ettn` (UUID),
   `provider_doc_id`, `status`, `gib_response`, `pdf_url`, `created/updated`.
   Yeni `.hbm.xml` (Hibernate DTD 2.0) + `hibernate.cfg.xml` mapping satırı.
2. **`TurqCurrentCard` genişletme:** `tax_number`, `tax_office`, `country`,
   `district`, `postal_code`, `email`.
3. **KDV modellemesi:** `TurqCurrentTransactionBill` (ya da `TurqBill`)'e
   `vat_rate` alanı; mapper satır KDV'lerini hesaplayıp belge KDV özetini üretir.
   *(Faz 2'de `TurqBill` yapısı incelenip kesinleştirilecek.)*
4. **Entegratör profili + seri ayarları:** `turquaz.properties` veya yeni küçük
   ayar entity'si (`TurqEInvoiceProviderConfig`): provider adı, ortam, anahtar
   (şifreli), GB-PK etiketi, belge-tipi→seri eşlemesi, aktif/pasif.

---

## 7. Faz planı

**Faz 0 — Branch:** `claude/einvoice-api-integration-6ylE8`'i
`origin/legacy/standalone-resurrected` koduna dayandır (histories bağımsız;
reset/checkout ile kodu getir), push `-u origin`.

**Faz 1 — Veri modeli & build:** `TurqEInvoiceStatus`, cari vergi alanları, KDV
alanı, entegratör config; yeni modülü `docker/build.sh` javac classpath'ine kat;
Docker build'in geçtiğini doğrula. **HTTP istemcisi kararı** (Java 11+ derleme vs
HttpClient jar) burada netleşir.

**Faz 2 — Sağlayıcı çekirdeği:** `EInvoiceProvider` arayüzü + `model/*` +
`EInvoiceProviderRegistry` + `EInvoiceRouter` + `EInvoiceSettings`. **NilveraProvider**
(OAuth2, e-Arşiv submit/status/pdf) test ortamına karşı.

**Faz 3 — Maplama & "Kes" iş kuralı:** `TurqInvoiceMapper` (cari+satır+KDV→EInvoice),
`EinvoiceBLIssue` (kes → gönder → durum yaz, hata/retry), entegratör başına seri.

**Faz 4 — UI (SWT):** Fatura ekranına **"e-Arşiv Kes"** + **"Durum"** sütunu;
Ayarlar ekranına çoklu entegratör profili (ekle/sil/aktif seç) + belge-tipi→entegratör
yönlendirme + seri tanımı (mevcut `current/ui` desenini izle).

**Faz 5 — İkinci entegratör + doğrulama:** İkinci adaptör (örn. İzibiz veya
Uyumsoft) ile arayüzün gerçekten sağlayıcı-bağımsız olduğunu kanıtla; README'ye
kurulum/kontör/çoklu-entegratör notu.

---

## 8. Doğrulama (nasıl test edilir)
- **Build:** `docker build -t turquaz-legacy-build docker/ && docker run --rm -v "$PWD":/src:ro -v "$PWD/dist":/out turquaz-legacy-build` — derleme kırılmamalı, yeni modül jar'a girmeli.
- **Şema:** Açılışta `turq_einvoice_status` + yeni kolonlar HSQLDB script'inde oluşmalı.
- **Sağlayıcı testi:** `NilveraProvider` → **Nilvera test ortamı** (apitest.nilvera.com) sahte mükellefle e-Arşiv gönder; ETTN/durum/PDF dönüşünü doğrula.
- **Çoklu entegratör testi:** İki profil tanımla, belge-tipi yönlendirmeyi ve entegratör başına ayrı serinin atandığını (ETTN çakışması olmadığını) doğrula.
- **Manuel "Kes" akışı:** Fatura kaydet (TASLAK) → "e-Arşiv Kes" → durum SENT/ACCEPTED, PDF erişilebilir; tekrar kesmeye çalışınca engellenmeli (mükerrer koruması).

---

## 9. Riskler / açık noktalar
- **KDV modeli** mevcut şemada yok; doğru modellemek hukuki geçerlilik için kritik (Faz 2-3).
- **Test ortamı erişimi:** Nilvera test API anahtarı gerekir; gerçek gönderim için mali mühür + entegratör sözleşmesi son kullanıcıda.
- **Java 8 derleme vs Java 17 runtime:** `HttpClient` Java 11+; Faz 1'de derleme stratejisi çözülecek.
- **Çoklu entegratör uyumu:** Aynı belge tipinde çoklu kullanımda ayrı seri zorunluluğu uygulama tarafından dayatılmalı (1104/1163 hatalarını önlemek için).
- Eski Hibernate 3 + DTD 2.0 — yeni hbm aynı DTD'yi kullanmalı.
