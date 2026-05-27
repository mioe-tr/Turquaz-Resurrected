# Şema Haritası — Eski Tablolar → Modern Şema

Bu belge, eski Turquaz veritabanı şemasının envanteridir ve modern şemaya
eşleştirme için temel oluşturur. Tam DDL için `docs/legacy-schema.sql` dosyasına
bakınız (2005 tarihli PostgreSQL 8.0 test veritabanının `pg_dump` çıktısından
`pg_restore --schema-only` ile üretilmiştir).

## Önemli Bulgular

- Eski şemada **78 tablo** bulundu (`turq_` ön ekli). Şartnamede "77 tablo"
  geçiyor; aradaki 1 fark sürümler arası bir tablonun eklenmesi/çıkması
  kaynaklı olabilir — bu, Faz 1'de hbm mapping'lerle çapraz doğrulanacak.
- Birincil anahtarlar `integer` ve tek bir global `hibernate_sequence` ile
  üretiliyor → modernde **UUID** önerilir.
- Para alanları sınırsız `numeric` (ölçek/duyarlık tanımsız) → modernde
  **`NUMERIC(19,4)`**. Çift taraflı kayıt `turq_accounting_transaction_columns`
  tablosunda `dept_amount` (borç) ve `credit_amount` (alacak) sütunlarında.
- Tarih alanları `date` (saat yok) → modernde **`TIMESTAMPTZ`**.
- Şema tek şirketlidir → modernde her iş tablosuna **`company_id`** eklenecek.
- Denetim alanları (`created_by`, `creation_date`, `updated_by`,
  `last_modified`) hemen her tabloda tekrarlanıyor → modernde ortak bir
  `@MappedSuperclass` (`AuditableEntity`) ile soyutlanacak.

## Modüllere Göre Dağılım (78 tablo)

| Modül            | Tablo |
|------------------|------:|
| Stok (inventory) | 14    |
| Bill/Sipariş/İrsaliye | 12 |
| Cari (current)   | 10    |
| Banka (bank)     | 8     |
| Muhasebe (accounting) | 7 |
| Çek-Senet (cheque) | 6   |
| Yönetim/Güvenlik (admin) | 6 |
| Motor/Sistem (engine) | 5 |
| Kasa (cash)      | 4     |
| Konsinye (consignment) | 3 |
| Ortak (currency/company) | 3 |
| **Toplam**       | **78** |

## Tablo Sözlüğü

### Muhasebe (accounting) — Yevmiye motoru
| Tablo | Açıklama |
|-------|----------|
| `turq_accounting_account_classes` | Hesap sınıfları (tekdüzen hesap planı üst sınıfları) |
| `turq_accounting_account_types` | Hesap türleri |
| `turq_accounting_accounts` | Muhasebe hesapları (hesap planı kalemleri) |
| `turq_accounting_journal` | Yevmiye defteri başlığı (fiş üst kaydı) |
| `turq_accounting_transactions` | Muhasebe işlemi (fiş) |
| `turq_accounting_transaction_columns` | Fiş satırları: borç (`dept_amount`) / alacak (`credit_amount`) |
| `turq_accounting_transaction_types` | Muhasebe işlem türleri |

### Cari (current) — Müşteri/Tedarikçi
| Tablo | Açıklama |
|-------|----------|
| `turq_current_cards` | Cari kartlar (müşteri/tedarikçi ana kayıt) |
| `turq_current_cards_groups` | Cari kart ↔ grup ilişkisi |
| `turq_current_cards_phones` | Cari kart telefon numaraları |
| `turq_current_contacts` | Cari iletişim/kişi bilgileri |
| `turq_current_groups` | Cari gruplar |
| `turq_current_transactions` | Cari hareketler |
| `turq_current_transaction_bill` | Cari işlem fişleri |
| `turq_current_transaction_types` | Cari işlem türleri |
| `turq_current_accounting_accounts` | Cari ↔ muhasebe hesabı eşleşmesi |
| `turq_current_accounting_types` | Cari muhasebeleştirme türleri |

### Stok (inventory)
| Tablo | Açıklama |
|-------|----------|
| `turq_inventory_cards` | Stok kartları (ürünler) |
| `turq_inventory_card_groups` | Stok kartı ↔ grup ilişkisi |
| `turq_inventory_card_units` | Stok kartı birim tanımları |
| `turq_inventory_groups` | Stok grupları |
| `turq_inventory_units` | Birimler (adet, kg, lt, ...) |
| `turq_inventory_warehouses` | Depolar |
| `turq_inventory_prices` | Stok fiyatları |
| `turq_inventory_transactions` | Stok hareketleri |
| `turq_inventory_transaction_bills` | Stok işlem fişleri |
| `turq_inventory_transaction_types` | Stok işlem türleri |
| `turq_inventory_accounting_accounts` | Stok ↔ muhasebe hesabı eşleşmesi |
| `turq_inventory_accounting_types` | Stok muhasebeleştirme türleri |
| `turq_inventory_customize_fields` | Stok özel (kullanıcı tanımlı) alanları |
| `turq_inventory_customize_types` | Stok özel alan türleri |

### Fatura / Sipariş / İrsaliye (bill, order, tradebill, services)
| Tablo | Açıklama |
|-------|----------|
| `turq_bills` | Faturalar |
| `turq_bill_groups` | Fatura grupları |
| `turq_bill_in_groups` | Fatura ↔ grup ilişkisi |
| `turq_bill_in_engine_sequences` | Fatura numara sıraları |
| `turq_orders` | Siparişler |
| `turq_order_groups` | Sipariş grupları |
| `turq_order_in_groups` | Sipariş ↔ grup ilişkisi |
| `turq_tradebill_tradebills` | Ticari belgeler (irsaliye vb.) |
| `turq_tradebill_rolls` | Ticari belge ruloları |
| `turq_tradebill_tradebills_rolls` | Ticari belge ↔ rulo ilişkisi |
| `turq_tradebill_transaction_types` | Ticari belge işlem türleri |
| `turq_services` | Hizmet kalemleri (fatura hizmet satırları) |

### Banka (bank)
| Tablo | Açıklama |
|-------|----------|
| `turq_banks_cards` | Banka hesap kartları |
| `turq_banks_transactions` | Banka hareketleri |
| `turq_banks_transaction_bills` | Banka işlem fişleri |
| `turq_banks_transaction_types` | Banka işlem türleri |
| `turq_bank_secondary_accounts` | Banka ikincil hesap tanımları |
| `turq_bank_cards_secondary_accounts` | Banka kartı ↔ ikincil hesap ilişkisi |
| `turq_bank_accounting_accounts` | Banka ↔ muhasebe hesabı eşleşmesi |
| `turq_bank_accounting_types` | Banka muhasebeleştirme türleri |

### Çek-Senet (cheque)
| Tablo | Açıklama |
|-------|----------|
| `turq_cheque_cheques` | Çek/senetler |
| `turq_cheque_rolls` | Çek/senet portföyleri (rulolar) |
| `turq_cheque_cheque_in_rolls` | Çek ↔ portföy ilişkisi |
| `turq_cheque_roll_accounting_accounts` | Çek portföyü ↔ muhasebe hesabı |
| `turq_cheque_transaction_types` | Çek işlem türleri |
| `turq_cheque_transaction_type_groups` | Çek işlem türü grupları |

### Kasa (cash)
| Tablo | Açıklama |
|-------|----------|
| `turq_cash_cards` | Kasa kartları |
| `turq_cash_transactions` | Kasa hareketleri |
| `turq_cash_transaction_rows` | Kasa işlem satırları |
| `turq_cash_transaction_types` | Kasa işlem türleri |

### Konsinye (consignment)
| Tablo | Açıklama |
|-------|----------|
| `turq_consignments` | Konsinye işlemleri |
| `turq_consignment_groups` | Konsinye grupları |
| `turq_consignments_in_group` | Konsinye ↔ grup ilişkisi |

### Yönetim / Güvenlik (admin)
| Tablo | Açıklama |
|-------|----------|
| `turq_users` | Kullanıcılar |
| `turq_groups` | Kullanıcı grupları (roller) |
| `turq_user_group` | Kullanıcı ↔ grup ilişkisi |
| `turq_user_permissions` | Kullanıcı izinleri |
| `turq_group_permissions` | Grup izinleri |
| `turq_user_permission_levels` | İzin seviyeleri |

### Motor / Sistem (engine)
| Tablo | Açıklama |
|-------|----------|
| `turq_modules` | Modüller |
| `turq_module_components` | Modül bileşenleri |
| `turq_engine_menu` | Uygulama menü yapısı |
| `turq_engine_sequences` | Numara sıra üreticileri |
| `turq_settings` | Sistem ayarları |

### Ortak (currency / company)
| Tablo | Açıklama |
|-------|----------|
| `turq_companies` | Şirketler (firma bilgisi) |
| `turq_currencies` | Para birimleri |
| `turq_currency_exchange_rates` | Döviz kurları |

## Modern Şema Dönüşüm Kuralları (Faz 1'de uygulanacak)

1. Birincil anahtar: `integer` → `UUID` (`uuid_generate_v4()` / uygulama tarafı).
2. Para: `numeric` → `NUMERIC(19,4)`, Java tarafı `BigDecimal`.
3. Zaman: `date` → `TIMESTAMPTZ`.
4. Çok kiracılılık: her iş tablosuna `company_id UUID NOT NULL`.
5. Denetim alanları: ortak `AuditableEntity` üst sınıfı.
6. Tablo/sütun adları `snake_case` korunur; `turq_` ön eki korunur.
7. Tüm para/miktar sütunlarına `NOT NULL DEFAULT 0` yerine iş kuralına uygun
   kısıtlar (Faz 1'de tablo bazında değerlendirilecek).
