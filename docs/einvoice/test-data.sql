-- =====================================================================
-- Turquaz e-Arşiv TEST VERİSİ
-- Şirket + cari + satış faturası (başlık + KDV'li stok satırı) ekler.
-- Tüm referanslar (para birimi, kur, modül, depo, birim, işlem tipleri,
-- stok kartı) sıfırdan kurulur; temiz bir veritabanında çalışır.
--
-- ID'ler 9001+ seçildi (uygulamanın ürettiği düşük ID'lerle çakışmasın).
-- Tarih formatı: 'YYYY-MM-DD'.
-- =====================================================================

-- 1) ŞİRKET (kendi firman). Uygulamada zaten şirket tanımladıysan bu bloğu atla.
INSERT INTO turq_companies
  (id, company_name, company_address, company_telephone, company_fax,
   created_by, creation_date, updated_by, update_date)
VALUES
  (9001, 'Benim Test Şirketim A.Ş.', 'Bağdat Cad. No:1 Kadıköy/İstanbul',
   '02161112233', '02161112234', 'admin', '2026-06-03', 'admin', '2026-06-03');

-- 2) PARA BİRİMİ (TRY, varsayılan)
INSERT INTO turq_currencies
  (id, currencies_name, currencies_abbreviation, currencies_country,
   created_by, creation_date, updated_by, last_modified, default_currency, constant)
VALUES
  (9001, 'Türk Lirası', 'TL', 'Türkiye',
   'admin', '2026-06-03', 'admin', '2026-06-03', TRUE, FALSE);

-- 3) KUR (1 TL = 1 TL)
INSERT INTO turq_currency_exchange_rates
  (id, exhange_rates_date, exchange_ratio, base_currency_id, exchange_currency_id)
VALUES
  (9001, '2026-06-03', 1, 9001, 9001);

-- 4) MODÜL (engine sequence için)
INSERT INTO turq_modules
  (id, modules_name, module_description, created_by, creation_date, updated_by, update_date)
VALUES
  (9001, 'TEST-EBELGE', 'e-Belge test modulu', 'admin', '2026-06-03', 'admin', '2026-06-03');

-- 5) ENGINE SEQUENCE (fatura no'yu temsil eder; başlık ve stok satırı AYNI sequence'i paylaşır)
INSERT INTO turq_engine_sequences (id, modules_id) VALUES (9001, 9001);

-- 6) BİRİM (Adet)
INSERT INTO turq_inventory_units
  (id, units_name, created_by, creation_date, updated_by, last_modified)
VALUES
  (9001, 'Adet', 'admin', '2026-06-03', 'admin', '2026-06-03');

-- 7) DEPO
INSERT INTO turq_inventory_warehouses
  (id, warehouses_name, warehouses_code, created_by, creation_date, updated_by, last_modified)
VALUES
  (9001, 'Merkez Depo', 'MRK', 'admin', '2026-06-03', 'admin', '2026-06-03');

-- 8) STOK İŞLEM TİPİ (Satış)
INSERT INTO turq_inventory_transaction_types
  (id, type_name, created_by, creation_date, updated_by, last_modified)
VALUES
  (9001, 'Satış', 'admin', '2026-06-03', 'admin', '2026-06-03');

-- 9) CARİ İŞLEM TİPİ (Satış Faturası)
INSERT INTO turq_current_transaction_types
  (id, transaction_type_name, created_by, creation_date, updated_by, last_modified)
VALUES
  (9001, 'Satış Faturası', 'admin', '2026-06-03', 'admin', '2026-06-03');

-- 10) STOK KARTI (ürün) - KDV %20
INSERT INTO turq_inventory_cards
  (id, card_inventory_code, card_name, card_definition,
   card_minimum_amount, card_maximum_amount, card_vat, card_discount,
   card_special_vat, card_special_vat_each,
   created_by, creation_date, updated_by, update_date, spec_vat_for_each)
VALUES
  (9001, 'URN-001', 'Test Ürünü', 'Test amaçlı ürün',
   0, 0, 20, 0, 0, 0,
   'admin', '2026-06-03', 'admin', '2026-06-03', FALSE);

-- 11) CARİ (müşteri) - VKN + vergi dairesi DOLU (e-Arşiv alıcısı)
INSERT INTO turq_current_cards
  (id, cards_current_code, cards_name, cards_definition, cards_address,
   cards_discount_rate, cards_discount_payment, cards_credit_limit, cards_risk_limit,
   cards_tax_department, cards_tax_number,
   created_by, creation_date, updated_by, last_modified, days_to_value)
VALUES
  (9001, 'CARI-001', 'Müşteri Örnek A.Ş.', 'Test müşterisi', 'Atatürk Cad. No:5 Çankaya/Ankara',
   0, 0, 0, 0,
   'Çankaya', '1234567890',
   'admin', '2026-06-03', 'admin', '2026-06-03', 0);

-- 12) FATURA BAŞLIĞI (cari hareket). Borç=1200 (müşteri bize borçlandı = satış).
INSERT INTO turq_current_transactions
  (id, transactions_date, transactions_document_no,
   transactions_total_credit, transactions_total_discount, transactions_total_dept,
   created_by, creation_date, updated_by, last_modified, transactions_definition,
   total_credit_in_foreign_currency, total_dept_in_foreign_currency, total_discount_in_foreign_currency,
   exchange_rate_id, current_transaction_types_id, engine_sequences_id, current_cards_id)
VALUES
  (9001, '2026-06-03', 'SF-2026-0001',
   0, 0, 1200,
   'admin', '2026-06-03', 'admin', '2026-06-03', 'Test satış faturası',
   0, 1200, 0,
   9001, 9001, 9001, 9001);

-- 13) FATURA SATIRI (stok hareketi): 10 Adet x 100 = 1000 matrah, %20 KDV = 200.
--     amount_out=10 (satış stoğu azaltır). AYNI engine_sequences_id=9001.
INSERT INTO turq_inventory_transactions
  (id, amount_in, unit_price, total_price, discount_rate, discount_amount,
   vat_amount, vat_special_unit_price, vat_special_rate, vat_special_amount,
   cumilative_price, amount_out, created_by, creation_date, updated_by, last_modified,
   transactions_date, document_no, definition, vat_rate,
   unit_price_in_foreign_currency, total_price_in_foreign_currency,
   discount_amount_in_foreign_currency, vat_amount_in_foreign_currency,
   vat_special_unit_price_in_foreign_currency, vat_special_amount_in_foreign_currency,
   cumilative_price_in_foreign_currency,
   inventory_warehouses_id, transaction_type, exchange_rate_id, inventory_units_id,
   engine_sequences_id, inventory_cards_id, current_cards_id)
VALUES
  (9001, 0, 100, 1000, 0, 0,
   200, 0, 0, 0,
   1000, 10, 'admin', '2026-06-03', 'admin', '2026-06-03',
   '2026-06-03', 'SF-2026-0001', 'Test Ürünü satışı', 20,
   100, 1000, 0, 200, 0, 0, 1000,
   9001, 9001, 9001, 9001, 9001, 9001, 9001);

-- Kalıcı yaz ve kapat (HSQLDB file DB .script'e yazsın)
COMMIT;
SHUTDOWN;
