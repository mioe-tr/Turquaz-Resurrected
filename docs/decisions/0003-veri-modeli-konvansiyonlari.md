# ADR 0003 — Veri Modeli Konvansiyonları

- **Durum:** Kabul edildi (Faz 1)
- **Tarih:** 2026-05-28

## Bağlam

Eski Turquaz şeması; tamsayı birincil anahtarlar (tek global `hibernate_sequence`),
sınırsız `numeric`, saat içermeyen `date`, tek-kiracılı tasarım kullanıyor.
Modern bir muhasebe motoru için bu yetersiz: çoklu kiracı, yüksek hassasiyet,
tahmin edilemez anahtarlar ve zaman dilimi farkındalığı gerekiyor.

## Karar

### Birincil anahtarlar

- Tüm tablolarda `id UUID NOT NULL`.
- Üretim **uygulama tarafında, UUIDv7** ile yapılır (zaman-sıralı → B-tree indeks
  yerleşimi UUIDv4'e göre çok daha iyi; aynı zamanda tahmin edilemez).
- Sıralı `hibernate_sequence` kaldırıldı.

### Çok kiracılılık

- `company_id UUID NOT NULL` her iş tablosuna eklenir.
- **Global kalan tablolar** (kiracıya özel değil, uygulama meta-verisi):
  - `turq_companies` — kiracı tanımı
  - `turq_modules` — yazılım modül kataloğu
  - `turq_module_components` — modül bileşen kataloğu
  - `turq_engine_menu` — uygulama menü yapısı
- Her `company_id` sütunu `turq_companies(id)` referansıdır; tabloya tekil indeks.

### Sayısal duyarlık

| Kategori           | Tip              | Kullanım                                  |
|--------------------|------------------|-------------------------------------------|
| Para               | `NUMERIC(19,4)`  | `*_amount`, `*_price`, totaller, limitler |
| Miktar / oran      | `NUMERIC(19,6)`  | `amount_in/out`, `*_rate`, `card_units_factor`, `exchange_ratio`, `tradebill_amount` |
| Sayaç / seviye     | `INTEGER`        | `card_minimum_amount`, `card_maximum_amount`, `user_permissions_level`, `days_to_value`, `menu_type` |

Java tarafı: **para ve miktar her zaman `BigDecimal`**. `double`/`float` yasak.

### Zaman damgaları

- Tüm `date` sütunları `TIMESTAMPTZ`'ye dönüştürüldü.
- Java tarafı: `Instant` (UTC) veya `OffsetDateTime`.

### Denetim alanları

Her tabloda korunur: `created_by`, `creation_date`, `updated_by`,
`last_modified`. Bazı eski tablolarda `update_date` alanı `last_modified`
yerine kullanılıyor — şimdilik dump'taki adlar korundu, Faz 1.x'te birleştirme
düşünülebilir. JPA tarafı bunu `AuditableEntity` `@MappedSuperclass` ile
soyutlar.

### Yabancı anahtarlar

- FK kümesi **eski `*.hbm.xml` mapping'lerinden türetildi** (many-to-one
  associations) — sadece adlandırma konvansiyonuna güvenmek yetmiyor (`_id`
  ile bitmeyen FK'ler var: `engine_sequence`, `transaction_type`, `parent_account`,
  `warehouse_in/out`, vs.).
- Her FK sütunu için tekil B-tree indeks eklendi.
- Bütünlük: FK kısıtları PostgreSQL tarafında uygulanır; uygulama doğrulamasına
  güvenmiyoruz.

### Adlandırma

- Tablo adları `turq_*` ön ekiyle korundu (kurumsal süreklilik).
- Sütun adları (`snake_case`, eski adlar) korundu; tipler/uzunluklar değişebilir
  ama isimler değişmez (mevcut hbm referansları ve şema haritasıyla eşleşsin diye).

### Tip dönüşüm özeti

| Eski               | Yeni                       |
|--------------------|----------------------------|
| `integer` (PK/FK)  | `uuid`                     |
| `smallint` (FK)    | `uuid`                     |
| `integer` (sayaç)  | `integer`                  |
| `numeric` (para)   | `numeric(19,4)`            |
| `numeric` (miktar) | `numeric(19,6)`            |
| `date`             | `timestamptz`              |
| `character varying(n)` | `varchar(n)`           |
| `character varying`    | `text`                 |

## Sonuçlar

- Şema üretici: `tools/gen_schema.py` (deterministik). Manuel düzenleme yerine
  üreticiyi koruyup yeniden çalıştırırız.
- Tüm dönüşüm gerçek PostgreSQL 16 örneğine `V1__sema.sql` migration'ı
  uygulanarak doğrulandı: 79 tablo, 79 PK, 191 FK, 270 indeks, sıfır hata.
- 2018'de eklenen `turq_cheque_cheques_rolls` ara tablosu modern şemaya dahil.
- Sonraki adımlar (Faz 1 devamı): JPA entity'leri + repository'ler + 2005
  verisini taşıyan seed migration + testler.
