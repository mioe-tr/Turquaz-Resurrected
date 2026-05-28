-- Turquaz Resurrected — V1: temel şema
-- Üretildi: tools/gen_schema.py (deterministik). Elle düzenlemeyin; tekrar üretin.
-- Konvansiyonlar: ADR-0003.
--
-- Eski 2005/2018 turq_* tabloları modernize edildi:
--   * PK: id uuid (UUIDv7, uygulama tarafı üretim)
--   * FK'ler uuid
--   * Para: numeric(19,4); miktar/oran: numeric(19,6)
--   * Tarih/saat: timestamptz
--   * Çok kiracılılık: company_id uuid (sistem/katalog tabloları hariç)
--
SET client_min_messages = WARNING;


CREATE TABLE turq_companies (
    id uuid NOT NULL,
    company_name varchar(250) NOT NULL,
    company_address varchar(250) NOT NULL,
    company_telephone varchar(100) NOT NULL,
    company_fax varchar(100) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    update_date timestamptz NOT NULL
);
ALTER TABLE turq_companies ADD CONSTRAINT pk_companies PRIMARY KEY (id);

CREATE TABLE turq_accounting_account_classes (
    id uuid NOT NULL,
    accounting_classes_name varchar(50) NOT NULL,
    accounting_classes_definition varchar(50) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_accounting_account_classes ADD CONSTRAINT pk_accounting_account_classes PRIMARY KEY (id);
CREATE INDEX ix_accounting_account_classes_company ON turq_accounting_account_classes (company_id);

CREATE TABLE turq_accounting_account_types (
    id uuid NOT NULL,
    accounting_types_name varchar(50) NOT NULL,
    accounting_types_definition varchar(250) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_accounting_account_types ADD CONSTRAINT pk_accounting_account_types PRIMARY KEY (id);
CREATE INDEX ix_accounting_account_types_company ON turq_accounting_account_types (company_id);

CREATE TABLE turq_accounting_accounts (
    id uuid NOT NULL,
    account_name varchar(250) NOT NULL,
    account_code varchar(50) NOT NULL,
    parent_account uuid NOT NULL,
    top_account uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    update_date timestamptz NOT NULL,
    accounting_types_id uuid,
    accounting_class_id uuid,
    company_id uuid NOT NULL
);
ALTER TABLE turq_accounting_accounts ADD CONSTRAINT pk_accounting_accounts PRIMARY KEY (id);
CREATE INDEX ix_accounting_accounts_company ON turq_accounting_accounts (company_id);

CREATE TABLE turq_accounting_journal (
    id uuid NOT NULL,
    journal_date timestamptz NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_accounting_journal ADD CONSTRAINT pk_accounting_journal PRIMARY KEY (id);
CREATE INDEX ix_accounting_journal_company ON turq_accounting_journal (company_id);

CREATE TABLE turq_accounting_transaction_columns (
    id uuid NOT NULL,
    accounting_accounts_id uuid NOT NULL,
    dept_amount numeric(19,4) NOT NULL,
    credit_amount numeric(19,4) NOT NULL,
    accounting_transactions_id uuid NOT NULL,
    transaction_definition varchar(250) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    rows_dept_in_base_currency numeric(19,4) NOT NULL,
    rows_credit_in_base_currency numeric(19,4) NOT NULL,
    exchange_rate_id uuid NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_accounting_transaction_columns ADD CONSTRAINT pk_accounting_transaction_columns PRIMARY KEY (id);
CREATE INDEX ix_accounting_transaction_columns_company ON turq_accounting_transaction_columns (company_id);

CREATE TABLE turq_accounting_transaction_types (
    id uuid NOT NULL,
    types_name text NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_accounting_transaction_types ADD CONSTRAINT pk_accounting_transaction_types PRIMARY KEY (id);
CREATE INDEX ix_accounting_transaction_types_company ON turq_accounting_transaction_types (company_id);

CREATE TABLE turq_accounting_transactions (
    id uuid NOT NULL,
    accounting_journal_id uuid NOT NULL,
    accounting_transaction_types_id uuid NOT NULL,
    transactions_date timestamptz NOT NULL,
    module_id uuid NOT NULL,
    transaction_document_no varchar(50) NOT NULL,
    engine_sequences_id uuid NOT NULL,
    transaction_description varchar(250) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    exchange_rate_id uuid NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_accounting_transactions ADD CONSTRAINT pk_accounting_transactions PRIMARY KEY (id);
CREATE INDEX ix_accounting_transactions_company ON turq_accounting_transactions (company_id);

CREATE TABLE turq_bank_accounting_accounts (
    id uuid NOT NULL,
    banks_cards_id uuid NOT NULL,
    accounting_accounts_id uuid NOT NULL,
    bank_accounting_types_id uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_bank_accounting_accounts ADD CONSTRAINT pk_bank_accounting_accounts PRIMARY KEY (id);
CREATE INDEX ix_bank_accounting_accounts_company ON turq_bank_accounting_accounts (company_id);

CREATE TABLE turq_bank_accounting_types (
    id uuid NOT NULL,
    type_name varchar(100) NOT NULL,
    definition varchar(100) NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_bank_accounting_types ADD CONSTRAINT pk_bank_accounting_types PRIMARY KEY (id);
CREATE INDEX ix_bank_accounting_types_company ON turq_bank_accounting_types (company_id);

CREATE TABLE turq_bank_cards_secondary_accounts (
    id uuid NOT NULL,
    bank_cards_id uuid NOT NULL,
    bank_secondary_accounts_id uuid NOT NULL,
    accounting_accounts_id uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    bank_definition varchar(250) NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_bank_cards_secondary_accounts ADD CONSTRAINT pk_bank_cards_secondary_accounts PRIMARY KEY (id);
CREATE INDEX ix_bank_cards_secondary_accounts_company ON turq_bank_cards_secondary_accounts (company_id);

CREATE TABLE turq_bank_secondary_accounts (
    id uuid NOT NULL,
    account_name varchar(50) NOT NULL,
    account_code varchar(5) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_bank_secondary_accounts ADD CONSTRAINT pk_bank_secondary_accounts PRIMARY KEY (id);
CREATE INDEX ix_bank_secondary_accounts_company ON turq_bank_secondary_accounts (company_id);

CREATE TABLE turq_banks_cards (
    id uuid NOT NULL,
    bank_name varchar(50) NOT NULL,
    bank_branch_name varchar(50) NOT NULL,
    bank_account_no varchar(50) NOT NULL,
    currencies_id uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    bank_definition varchar(250) NOT NULL,
    bank_code varchar(100) NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_banks_cards ADD CONSTRAINT pk_banks_cards PRIMARY KEY (id);
CREATE INDEX ix_banks_cards_company ON turq_banks_cards (company_id);

CREATE TABLE turq_banks_transaction_bills (
    id uuid NOT NULL,
    transaction_bill_date timestamptz NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    engine_sequences_id uuid NOT NULL,
    transaction_bill_definition varchar(250) NOT NULL,
    transaction_bill_no varchar(100) NOT NULL,
    banks_transaction_types_id uuid NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_banks_transaction_bills ADD CONSTRAINT pk_banks_transaction_bills PRIMARY KEY (id);
CREATE INDEX ix_banks_transaction_bills_company ON turq_banks_transaction_bills (company_id);

CREATE TABLE turq_banks_transaction_types (
    id uuid NOT NULL,
    transaction_type_name varchar(50) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_banks_transaction_types ADD CONSTRAINT pk_banks_transaction_types PRIMARY KEY (id);
CREATE INDEX ix_banks_transaction_types_company ON turq_banks_transaction_types (company_id);

CREATE TABLE turq_banks_transactions (
    id uuid NOT NULL,
    bank_transactions_bills_id uuid NOT NULL,
    dept_amount numeric(19,4) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    credit_amount numeric(19,4) NOT NULL,
    banks_cards_id uuid NOT NULL,
    dept_amount_in_foreign_currency numeric(19,4) NOT NULL,
    credit_amount_in_foreign_currency numeric(19,4) NOT NULL,
    exchange_rate_id uuid NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_banks_transactions ADD CONSTRAINT pk_banks_transactions PRIMARY KEY (id);
CREATE INDEX ix_banks_transactions_company ON turq_banks_transactions (company_id);

CREATE TABLE turq_bill_groups (
    id uuid NOT NULL,
    groups_name varchar(50) NOT NULL,
    group_description varchar(250) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_bill_groups ADD CONSTRAINT pk_bill_groups PRIMARY KEY (id);
CREATE INDEX ix_bill_groups_company ON turq_bill_groups (company_id);

CREATE TABLE turq_bill_in_engine_sequences (
    engine_sequences_id uuid NOT NULL,
    bills_id uuid NOT NULL,
    id uuid NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_bill_in_engine_sequences ADD CONSTRAINT pk_bill_in_engine_sequences PRIMARY KEY (id);
CREATE INDEX ix_bill_in_engine_sequences_company ON turq_bill_in_engine_sequences (company_id);

CREATE TABLE turq_bill_in_groups (
    id uuid NOT NULL,
    bills_id uuid NOT NULL,
    bill_groups_id uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_bill_in_groups ADD CONSTRAINT pk_bill_in_groups PRIMARY KEY (id);
CREATE INDEX ix_bill_in_groups_company ON turq_bill_in_groups (company_id);

CREATE TABLE turq_bills (
    id uuid NOT NULL,
    bills_type integer NOT NULL,
    bills_date timestamptz NOT NULL,
    bills_definition varchar(250) NOT NULL,
    bills_printed boolean NOT NULL,
    is_open boolean NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    due_date timestamptz NOT NULL,
    bill_document_no varchar(50) NOT NULL,
    current_cards_id uuid NOT NULL,
    exchange_rate_id uuid NOT NULL,
    engine_sequences_id uuid NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_bills ADD CONSTRAINT pk_bills PRIMARY KEY (id);
CREATE INDEX ix_bills_company ON turq_bills (company_id);

CREATE TABLE turq_cash_cards (
    id uuid NOT NULL,
    cash_card_name varchar(250) NOT NULL,
    cash_card_definition varchar(250) NOT NULL,
    accounting_accounts_id uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_cash_cards ADD CONSTRAINT pk_cash_cards PRIMARY KEY (id);
CREATE INDEX ix_cash_cards_company ON turq_cash_cards (company_id);

CREATE TABLE turq_cash_transaction_rows (
    id uuid NOT NULL,
    accounting_accounts_id uuid NOT NULL,
    dept_amount numeric(19,4) NOT NULL,
    credit_amount numeric(19,4) NOT NULL,
    cash_transactions_id uuid NOT NULL,
    transaction_definition varchar(250),
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    cash_cards_id uuid NOT NULL,
    dept_amount_in_foreign_currency numeric(19,4) NOT NULL,
    credit_amount_in_foreign_currency numeric(19,4) NOT NULL,
    exchange_rate_id uuid NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_cash_transaction_rows ADD CONSTRAINT pk_cash_transaction_rows PRIMARY KEY (id);
CREATE INDEX ix_cash_transaction_rows_company ON turq_cash_transaction_rows (company_id);

CREATE TABLE turq_cash_transaction_types (
    id uuid NOT NULL,
    cash_transation_type_name varchar(100) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_cash_transaction_types ADD CONSTRAINT pk_cash_transaction_types PRIMARY KEY (id);
CREATE INDEX ix_cash_transaction_types_company ON turq_cash_transaction_types (company_id);

CREATE TABLE turq_cash_transactions (
    id uuid NOT NULL,
    cash_transactions_types_id uuid NOT NULL,
    engine_sequences_id uuid NOT NULL,
    transaction_date timestamptz NOT NULL,
    transaction_definition varchar(250),
    document_no varchar(100),
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_cash_transactions ADD CONSTRAINT pk_cash_transactions PRIMARY KEY (id);
CREATE INDEX ix_cash_transactions_company ON turq_cash_transactions (company_id);

CREATE TABLE turq_cheque_cheque_in_rolls (
    id uuid NOT NULL,
    cheque_rolls_id uuid NOT NULL,
    cheque_cheques_id uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_cheque_cheque_in_rolls ADD CONSTRAINT pk_cheque_cheque_in_rolls PRIMARY KEY (id);
CREATE INDEX ix_cheque_cheque_in_rolls_company ON turq_cheque_cheque_in_rolls (company_id);

CREATE TABLE turq_cheque_cheques (
    id uuid NOT NULL,
    cheques_portfolio_no varchar(30) NOT NULL,
    cheques_no varchar(50) NOT NULL,
    banks_id uuid NOT NULL,
    cheques_due_date timestamptz NOT NULL,
    cheques_debtor varchar(100) NOT NULL,
    cheques_payment_place varchar(50),
    cheques_value_date timestamptz NOT NULL,
    cheques_amount numeric(19,4) NOT NULL,
    currencies_id uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    bank_name varchar(100) NOT NULL,
    bank_branch_name varchar(100) NOT NULL,
    cheques_type integer NOT NULL,
    bank_account_no varchar(100),
    cheques_amount_in_foreign_currency numeric(19,4) NOT NULL,
    exchange_rate_id uuid NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_cheque_cheques ADD CONSTRAINT pk_cheque_cheques PRIMARY KEY (id);
CREATE INDEX ix_cheque_cheques_company ON turq_cheque_cheques (company_id);

CREATE TABLE turq_cheque_roll_accounting_accounts (
    id uuid NOT NULL,
    accounting_accounts_id uuid NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_cheque_roll_accounting_accounts ADD CONSTRAINT pk_cheque_roll_accounting_accounts PRIMARY KEY (id);
CREATE INDEX ix_cheque_roll_accounting_accounts_company ON turq_cheque_roll_accounting_accounts (company_id);

CREATE TABLE turq_cheque_rolls (
    id uuid NOT NULL,
    cheque_transaction_types_id uuid NOT NULL,
    cheque_rolls_date timestamptz NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    engine_sequences_id uuid NOT NULL,
    cheque_roll_no varchar(50) NOT NULL,
    current_cards_id uuid NOT NULL,
    banks_cards_id uuid NOT NULL,
    sum_cheque_amounts boolean NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_cheque_rolls ADD CONSTRAINT pk_cheque_rolls PRIMARY KEY (id);
CREATE INDEX ix_cheque_rolls_company ON turq_cheque_rolls (company_id);

CREATE TABLE turq_cheque_transaction_type_groups (
    id uuid NOT NULL,
    group_name varchar(100),
    definition varchar(250),
    company_id uuid NOT NULL
);
ALTER TABLE turq_cheque_transaction_type_groups ADD CONSTRAINT pk_cheque_transaction_type_groups PRIMARY KEY (id);
CREATE INDEX ix_cheque_transaction_type_groups_company ON turq_cheque_transaction_type_groups (company_id);

CREATE TABLE turq_cheque_transaction_types (
    id uuid NOT NULL,
    transaction_typs_name varchar(50) NOT NULL,
    transaction_types_parent uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_cheque_transaction_types ADD CONSTRAINT pk_cheque_transaction_types PRIMARY KEY (id);
CREATE INDEX ix_cheque_transaction_types_company ON turq_cheque_transaction_types (company_id);

CREATE TABLE turq_consignment_groups (
    id uuid NOT NULL,
    groups_name varchar(50) NOT NULL,
    groups_description varchar(250) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_consignment_groups ADD CONSTRAINT pk_consignment_groups PRIMARY KEY (id);
CREATE INDEX ix_consignment_groups_company ON turq_consignment_groups (company_id);

CREATE TABLE turq_consignments (
    id uuid NOT NULL,
    consignments_date timestamptz NOT NULL,
    consignments_definition varchar(250) NOT NULL,
    consignments_type integer NOT NULL,
    consignments_printed boolean NOT NULL,
    engine_sequences_id uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    current_cards_id uuid NOT NULL,
    consignment_document_no varchar(50) NOT NULL,
    exchange_rate_id uuid NOT NULL,
    bill_document_no varchar(50) NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_consignments ADD CONSTRAINT pk_consignments PRIMARY KEY (id);
CREATE INDEX ix_consignments_company ON turq_consignments (company_id);

CREATE TABLE turq_consignments_in_group (
    id uuid NOT NULL,
    consignment_id uuid NOT NULL,
    consignments_groups_id uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_consignments_in_group ADD CONSTRAINT pk_consignments_in_group PRIMARY KEY (id);
CREATE INDEX ix_consignments_in_group_company ON turq_consignments_in_group (company_id);

CREATE TABLE turq_currencies (
    id uuid NOT NULL,
    currencies_name varchar(30) NOT NULL,
    currencies_abbreviation varchar(5) NOT NULL,
    currencies_country varchar(50) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    default_currency boolean NOT NULL,
    constant boolean,
    company_id uuid NOT NULL
);
ALTER TABLE turq_currencies ADD CONSTRAINT pk_currencies PRIMARY KEY (id);
CREATE INDEX ix_currencies_company ON turq_currencies (company_id);

CREATE TABLE turq_currency_exchange_rates (
    id uuid NOT NULL,
    exhange_rates_date timestamptz NOT NULL,
    base_currency_id uuid NOT NULL,
    exchange_currency_id uuid NOT NULL,
    exchange_ratio numeric(19,6) NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_currency_exchange_rates ADD CONSTRAINT pk_currency_exchange_rates PRIMARY KEY (id);
CREATE INDEX ix_currency_exchange_rates_company ON turq_currency_exchange_rates (company_id);

CREATE TABLE turq_current_accounting_accounts (
    id uuid NOT NULL,
    current_cards_id uuid NOT NULL,
    accounting_accounts_id uuid NOT NULL,
    current_accounting_types_id uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_current_accounting_accounts ADD CONSTRAINT pk_current_accounting_accounts PRIMARY KEY (id);
CREATE INDEX ix_current_accounting_accounts_company ON turq_current_accounting_accounts (company_id);

CREATE TABLE turq_current_accounting_types (
    id uuid NOT NULL,
    type_name varchar(100) NOT NULL,
    definition varchar(100) NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_current_accounting_types ADD CONSTRAINT pk_current_accounting_types PRIMARY KEY (id);
CREATE INDEX ix_current_accounting_types_company ON turq_current_accounting_types (company_id);

CREATE TABLE turq_current_cards (
    id uuid NOT NULL,
    cards_current_code varchar(25) NOT NULL,
    cards_name varchar(250) NOT NULL,
    cards_definition varchar(250) NOT NULL,
    cards_address varchar(250) NOT NULL,
    cards_discount_rate numeric(19,6) NOT NULL,
    cards_discount_payment numeric(19,4) NOT NULL,
    cards_credit_limit numeric(19,4) NOT NULL,
    cards_risk_limit numeric(19,4) NOT NULL,
    cards_tax_department varchar(50) NOT NULL,
    cards_tax_number varchar(50) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    days_to_value integer,
    company_id uuid NOT NULL
);
ALTER TABLE turq_current_cards ADD CONSTRAINT pk_current_cards PRIMARY KEY (id);
CREATE INDEX ix_current_cards_company ON turq_current_cards (company_id);

CREATE TABLE turq_current_cards_groups (
    id uuid NOT NULL,
    current_cards_id uuid NOT NULL,
    current_groups_id uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_current_cards_groups ADD CONSTRAINT pk_current_cards_groups PRIMARY KEY (id);
CREATE INDEX ix_current_cards_groups_company ON turq_current_cards_groups (company_id);

CREATE TABLE turq_current_cards_phones (
    id uuid NOT NULL,
    current_cards_id uuid NOT NULL,
    phones_country_code integer NOT NULL,
    phones_city_code integer NOT NULL,
    phones_number integer NOT NULL,
    phones_type varchar(50) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_current_cards_phones ADD CONSTRAINT pk_current_cards_phones PRIMARY KEY (id);
CREATE INDEX ix_current_cards_phones_company ON turq_current_cards_phones (company_id);

CREATE TABLE turq_current_contacts (
    id uuid NOT NULL,
    current_cards_id uuid NOT NULL,
    contacts_name varchar(100) NOT NULL,
    contact_address varchar(250) NOT NULL,
    contacts_phone1 varchar(30) NOT NULL,
    contacts_phone2 varchar(30) NOT NULL,
    contacts_fax_number varchar(30) NOT NULL,
    contacts_email varchar(100) NOT NULL,
    contacts_web_site varchar(250),
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_current_contacts ADD CONSTRAINT pk_current_contacts PRIMARY KEY (id);
CREATE INDEX ix_current_contacts_company ON turq_current_contacts (company_id);

CREATE TABLE turq_current_groups (
    id uuid NOT NULL,
    groups_name varchar(50) NOT NULL,
    groups_description varchar(250) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_current_groups ADD CONSTRAINT pk_current_groups PRIMARY KEY (id);
CREATE INDEX ix_current_groups_company ON turq_current_groups (company_id);

CREATE TABLE turq_current_transaction_bill (
    id uuid NOT NULL,
    current_transactions_id_close uuid NOT NULL,
    current_transactions_id_open uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_current_transaction_bill ADD CONSTRAINT pk_current_transaction_bill PRIMARY KEY (id);
CREATE INDEX ix_current_transaction_bill_company ON turq_current_transaction_bill (company_id);

CREATE TABLE turq_current_transaction_types (
    id uuid NOT NULL,
    transaction_type_name varchar(50) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_current_transaction_types ADD CONSTRAINT pk_current_transaction_types PRIMARY KEY (id);
CREATE INDEX ix_current_transaction_types_company ON turq_current_transaction_types (company_id);

CREATE TABLE turq_current_transactions (
    id uuid NOT NULL,
    current_cards_id uuid NOT NULL,
    transactions_date timestamptz NOT NULL,
    transactions_document_no text NOT NULL,
    current_transaction_types_id uuid NOT NULL,
    transactions_total_credit numeric(19,4) NOT NULL,
    transactions_total_discount numeric(19,4) NOT NULL,
    transactions_total_dept numeric(19,4) NOT NULL,
    engine_sequences_id uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    transactions_definition varchar(250) NOT NULL,
    total_credit_in_foreign_currency numeric(19,4) NOT NULL,
    total_dept_in_foreign_currency numeric(19,4) NOT NULL,
    total_discount_in_foreign_currency numeric(19,4) NOT NULL,
    exchange_rate_id uuid NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_current_transactions ADD CONSTRAINT pk_current_transactions PRIMARY KEY (id);
CREATE INDEX ix_current_transactions_company ON turq_current_transactions (company_id);

CREATE TABLE turq_engine_menu (
    id uuid NOT NULL,
    menu_name varchar(100) NOT NULL,
    menu_image varchar(100),
    menu_type integer NOT NULL,
    menu_module_component uuid NOT NULL,
    parent_id uuid NOT NULL
);
ALTER TABLE turq_engine_menu ADD CONSTRAINT pk_engine_menu PRIMARY KEY (id);

CREATE TABLE turq_engine_sequences (
    id uuid NOT NULL,
    modules_id uuid NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_engine_sequences ADD CONSTRAINT pk_engine_sequences PRIMARY KEY (id);
CREATE INDEX ix_engine_sequences_company ON turq_engine_sequences (company_id);

CREATE TABLE turq_group_permissions (
    id uuid NOT NULL,
    groups_id uuid NOT NULL,
    modules_id uuid NOT NULL,
    module_components_id uuid NOT NULL,
    group_permissions_level integer NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    update_date timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_group_permissions ADD CONSTRAINT pk_group_permissions PRIMARY KEY (id);
CREATE INDEX ix_group_permissions_company ON turq_group_permissions (company_id);

CREATE TABLE turq_groups (
    id uuid NOT NULL,
    groups_name varchar(100) NOT NULL,
    groups_description varchar(250) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    update_date timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_groups ADD CONSTRAINT pk_groups PRIMARY KEY (id);
CREATE INDEX ix_groups_company ON turq_groups (company_id);

CREATE TABLE turq_inventory_accounting_accounts (
    id uuid NOT NULL,
    inventory_cards_id uuid NOT NULL,
    accounting_accounts_id uuid NOT NULL,
    inventory_accounting_types_id uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_inventory_accounting_accounts ADD CONSTRAINT pk_inventory_accounting_accounts PRIMARY KEY (id);
CREATE INDEX ix_inventory_accounting_accounts_company ON turq_inventory_accounting_accounts (company_id);

CREATE TABLE turq_inventory_accounting_types (
    id uuid NOT NULL,
    type_name varchar(100) NOT NULL,
    definition varchar(100) NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_inventory_accounting_types ADD CONSTRAINT pk_inventory_accounting_types PRIMARY KEY (id);
CREATE INDEX ix_inventory_accounting_types_company ON turq_inventory_accounting_types (company_id);

CREATE TABLE turq_inventory_card_groups (
    id uuid NOT NULL,
    inventory_cards_id uuid NOT NULL,
    inventory_groups_id uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_inventory_card_groups ADD CONSTRAINT pk_inventory_card_groups PRIMARY KEY (id);
CREATE INDEX ix_inventory_card_groups_company ON turq_inventory_card_groups (company_id);

CREATE TABLE turq_inventory_card_units (
    id uuid NOT NULL,
    inventory_cards_id uuid NOT NULL,
    card_units_factor numeric(19,6) NOT NULL,
    inventory_units_id uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_inventory_card_units ADD CONSTRAINT pk_inventory_card_units PRIMARY KEY (id);
CREATE INDEX ix_inventory_card_units_company ON turq_inventory_card_units (company_id);

CREATE TABLE turq_inventory_cards (
    id uuid NOT NULL,
    card_inventory_code varchar(25) NOT NULL,
    card_name varchar(50) NOT NULL,
    card_definition varchar(50) NOT NULL,
    card_minimum_amount integer NOT NULL,
    card_maximum_amount integer NOT NULL,
    card_vat integer NOT NULL,
    card_discount integer NOT NULL,
    card_special_vat integer NOT NULL,
    card_special_vat_each numeric(19,4) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    update_date timestamptz NOT NULL,
    spec_vat_for_each boolean NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_inventory_cards ADD CONSTRAINT pk_inventory_cards PRIMARY KEY (id);
CREATE INDEX ix_inventory_cards_company ON turq_inventory_cards (company_id);

CREATE TABLE turq_inventory_customize_fields (
    id uuid NOT NULL,
    customize_type_id uuid NOT NULL,
    field_value varchar(50) NOT NULL,
    inventory_card_id uuid NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_inventory_customize_fields ADD CONSTRAINT pk_inventory_customize_fields PRIMARY KEY (id);
CREATE INDEX ix_inventory_customize_fields_company ON turq_inventory_customize_fields (company_id);

CREATE TABLE turq_inventory_customize_types (
    id uuid NOT NULL,
    field_name varchar(50) NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_inventory_customize_types ADD CONSTRAINT pk_inventory_customize_types PRIMARY KEY (id);
CREATE INDEX ix_inventory_customize_types_company ON turq_inventory_customize_types (company_id);

CREATE TABLE turq_inventory_groups (
    id uuid NOT NULL,
    groups_name varchar(50) NOT NULL,
    groups_description varchar(250) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    parent_group uuid NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_inventory_groups ADD CONSTRAINT pk_inventory_groups PRIMARY KEY (id);
CREATE INDEX ix_inventory_groups_company ON turq_inventory_groups (company_id);

CREATE TABLE turq_inventory_prices (
    id uuid NOT NULL,
    inventory_cards_id uuid NOT NULL,
    prices_type boolean NOT NULL,
    currencies_id uuid NOT NULL,
    prices_amount numeric(19,4) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_inventory_prices ADD CONSTRAINT pk_inventory_prices PRIMARY KEY (id);
CREATE INDEX ix_inventory_prices_company ON turq_inventory_prices (company_id);

CREATE TABLE turq_inventory_transaction_bills (
    id uuid NOT NULL,
    bill_date timestamptz NOT NULL,
    bill_definition varchar(250) NOT NULL,
    bill_type integer NOT NULL,
    engine_sequence uuid NOT NULL,
    bill_document_no varchar(25) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    warehouse_in uuid NOT NULL,
    warehouse_out uuid NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_inventory_transaction_bills ADD CONSTRAINT pk_inventory_transaction_bills PRIMARY KEY (id);
CREATE INDEX ix_inventory_transaction_bills_company ON turq_inventory_transaction_bills (company_id);

CREATE TABLE turq_inventory_transaction_types (
    id uuid NOT NULL,
    type_name varchar(100) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_inventory_transaction_types ADD CONSTRAINT pk_inventory_transaction_types PRIMARY KEY (id);
CREATE INDEX ix_inventory_transaction_types_company ON turq_inventory_transaction_types (company_id);

CREATE TABLE turq_inventory_transactions (
    id uuid NOT NULL,
    inventory_cards_id uuid NOT NULL,
    inventory_warehouses_id uuid NOT NULL,
    engine_sequences_id uuid NOT NULL,
    amount_in numeric(19,6) NOT NULL,
    inventory_units_id uuid NOT NULL,
    unit_price numeric(19,4) NOT NULL,
    total_price numeric(19,4) NOT NULL,
    discount_rate numeric(19,6) NOT NULL,
    discount_amount numeric(19,4) NOT NULL,
    vat_amount numeric(19,4) NOT NULL,
    vat_special_unit_price numeric(19,4) NOT NULL,
    vat_special_rate numeric(19,6) NOT NULL,
    vat_special_amount numeric(19,4) NOT NULL,
    cumilative_price numeric(19,4) NOT NULL,
    amount_out numeric(19,6) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    transactions_date timestamptz NOT NULL,
    transaction_type uuid NOT NULL,
    document_no varchar(100) NOT NULL,
    definition varchar(250) NOT NULL,
    exchange_rate_id uuid NOT NULL,
    vat_rate numeric(19,6) NOT NULL,
    unit_price_in_foreign_currency numeric(19,4) NOT NULL,
    total_price_in_foreign_currency numeric(19,4) NOT NULL,
    discount_amount_in_foreign_currency numeric(19,4) NOT NULL,
    vat_amount_in_foreign_currency numeric(19,4) NOT NULL,
    vat_special_unit_price_in_foreign_currency numeric(19,4) NOT NULL,
    vat_special_amount_in_foreign_currency numeric(19,4) NOT NULL,
    cumilative_price_in_foreign_currency numeric(19,4) NOT NULL,
    current_cards_id uuid NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_inventory_transactions ADD CONSTRAINT pk_inventory_transactions PRIMARY KEY (id);
CREATE INDEX ix_inventory_transactions_company ON turq_inventory_transactions (company_id);

CREATE TABLE turq_inventory_units (
    id uuid NOT NULL,
    units_name varchar(50) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_inventory_units ADD CONSTRAINT pk_inventory_units PRIMARY KEY (id);
CREATE INDEX ix_inventory_units_company ON turq_inventory_units (company_id);

CREATE TABLE turq_inventory_warehouses (
    id uuid NOT NULL,
    warehouses_name varchar(50) NOT NULL,
    warehouses_address varchar(250),
    warehouses_description varchar(250),
    warehouses_city varchar(25),
    warehouses_telephone varchar(25),
    warehouses_code varchar(25) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_inventory_warehouses ADD CONSTRAINT pk_inventory_warehouses PRIMARY KEY (id);
CREATE INDEX ix_inventory_warehouses_company ON turq_inventory_warehouses (company_id);

CREATE TABLE turq_module_components (
    id uuid NOT NULL,
    modules_id uuid NOT NULL,
    components_name varchar(100) NOT NULL,
    components_description varchar(250) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    update_date timestamptz NOT NULL
);
ALTER TABLE turq_module_components ADD CONSTRAINT pk_module_components PRIMARY KEY (id);

CREATE TABLE turq_modules (
    id uuid NOT NULL,
    modules_name varchar(100) NOT NULL,
    module_description varchar(250) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    update_date timestamptz NOT NULL
);
ALTER TABLE turq_modules ADD CONSTRAINT pk_modules PRIMARY KEY (id);

CREATE TABLE turq_order_groups (
    id uuid NOT NULL,
    groups_name varchar(50) NOT NULL,
    groups_description varchar(250) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_order_groups ADD CONSTRAINT pk_order_groups PRIMARY KEY (id);
CREATE INDEX ix_order_groups_company ON turq_order_groups (company_id);

CREATE TABLE turq_order_in_groups (
    id uuid NOT NULL,
    orders_id uuid NOT NULL,
    order_groups_id uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_order_in_groups ADD CONSTRAINT pk_order_in_groups PRIMARY KEY (id);
CREATE INDEX ix_order_in_groups_company ON turq_order_in_groups (company_id);

CREATE TABLE turq_orders (
    id uuid NOT NULL,
    orders_document_no integer NOT NULL,
    bills_id uuid NOT NULL,
    orders_date timestamptz NOT NULL,
    current_cards_id uuid NOT NULL,
    orders_definition varchar(250) NOT NULL,
    orders_discount_rate integer NOT NULL,
    orders_vat integer NOT NULL,
    orders_discount_amount numeric(19,4) NOT NULL,
    orders_charges numeric(19,4) NOT NULL,
    orders_vat_amount numeric(19,4) NOT NULL,
    orders_total_amount numeric(19,4) NOT NULL,
    orders_due_date timestamptz NOT NULL,
    orders_deliver_date timestamptz NOT NULL,
    orders_delivered integer NOT NULL,
    orders_type integer NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_orders ADD CONSTRAINT pk_orders PRIMARY KEY (id);
CREATE INDEX ix_orders_company ON turq_orders (company_id);

CREATE TABLE turq_services (
    id uuid NOT NULL,
    service_name varchar(250) NOT NULL,
    class_name varchar(250) NOT NULL,
    method_name varchar(250) NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_services ADD CONSTRAINT pk_services PRIMARY KEY (id);
CREATE INDEX ix_services_company ON turq_services (company_id);

CREATE TABLE turq_settings (
    id uuid NOT NULL,
    database_version varchar(50) NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_settings ADD CONSTRAINT pk_settings PRIMARY KEY (id);
CREATE INDEX ix_settings_company ON turq_settings (company_id);

CREATE TABLE turq_tradebill_rolls (
    id uuid NOT NULL,
    current_cards_id uuid NOT NULL,
    banks_cards_id uuid NOT NULL,
    tradebill_transaction_types_id uuid NOT NULL,
    tradebill_rolls_date timestamptz NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_tradebill_rolls ADD CONSTRAINT pk_tradebill_rolls PRIMARY KEY (id);
CREATE INDEX ix_tradebill_rolls_company ON turq_tradebill_rolls (company_id);

CREATE TABLE turq_tradebill_tradebills (
    id uuid NOT NULL,
    tradebills_portfolio_no varchar(50) NOT NULL,
    tradebill_due_date timestamptz NOT NULL,
    tradebill_debtor varchar(100) NOT NULL,
    tradebill_guarantor varchar(100) NOT NULL,
    tradebill_payment_place varchar(100) NOT NULL,
    tradebill_value_date integer NOT NULL,
    tradebill_amount numeric(19,6) NOT NULL,
    currencies_id uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_tradebill_tradebills ADD CONSTRAINT pk_tradebill_tradebills PRIMARY KEY (id);
CREATE INDEX ix_tradebill_tradebills_company ON turq_tradebill_tradebills (company_id);

CREATE TABLE turq_tradebill_tradebills_rolls (
    id uuid NOT NULL,
    tradebill_rolls_id uuid NOT NULL,
    tradebill_tradebills_id uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_tradebill_tradebills_rolls ADD CONSTRAINT pk_tradebill_tradebills_rolls PRIMARY KEY (id);
CREATE INDEX ix_tradebill_tradebills_rolls_company ON turq_tradebill_tradebills_rolls (company_id);

CREATE TABLE turq_tradebill_transaction_types (
    id uuid NOT NULL,
    transaction_types_name varchar(50) NOT NULL,
    transaction_types_parent smallint NOT NULL,
    accounting_accounts_id uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_tradebill_transaction_types ADD CONSTRAINT pk_tradebill_transaction_types PRIMARY KEY (id);
CREATE INDEX ix_tradebill_transaction_types_company ON turq_tradebill_transaction_types (company_id);

CREATE TABLE turq_user_group (
    id uuid NOT NULL,
    groups_id uuid NOT NULL,
    users_id uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    update_date timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_user_group ADD CONSTRAINT pk_user_group PRIMARY KEY (id);
CREATE INDEX ix_user_group_company ON turq_user_group (company_id);

CREATE TABLE turq_user_permission_levels (
    id uuid NOT NULL,
    permission_level integer NOT NULL,
    permission_name varchar(50) NOT NULL,
    permission_description varchar(50) NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_user_permission_levels ADD CONSTRAINT pk_user_permission_levels PRIMARY KEY (id);
CREATE INDEX ix_user_permission_levels_company ON turq_user_permission_levels (company_id);

CREATE TABLE turq_user_permissions (
    id uuid NOT NULL,
    users_id uuid NOT NULL,
    modules_id uuid NOT NULL,
    module_components_id uuid NOT NULL,
    user_permissions_level uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    update_date timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_user_permissions ADD CONSTRAINT pk_user_permissions PRIMARY KEY (id);
CREATE INDEX ix_user_permissions_company ON turq_user_permissions (company_id);

CREATE TABLE turq_users (
    id uuid NOT NULL,
    username varchar(30) NOT NULL,
    users_password varchar(250) NOT NULL,
    users_real_name varchar(250) NOT NULL,
    users_description varchar(250) NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    update_date timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_users ADD CONSTRAINT pk_users PRIMARY KEY (id);
CREATE INDEX ix_users_company ON turq_users (company_id);

-- 2018'de eklenen ara tablo (çek ↔ portföy çoka-çok)
CREATE TABLE turq_cheque_cheques_rolls (
    id uuid NOT NULL,
    cheque_rolls_id uuid NOT NULL,
    cheque_cheques_id uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_cheque_cheques_rolls ADD CONSTRAINT pk_cheque_cheques_rolls PRIMARY KEY (id);
CREATE INDEX ix_cheque_cheques_rolls_company ON turq_cheque_cheques_rolls (company_id);
ALTER TABLE turq_cheque_cheques_rolls ADD CONSTRAINT fk_cheque_cheques_rolls__cheque_rolls_id FOREIGN KEY (cheque_rolls_id) REFERENCES turq_cheque_rolls (id);
ALTER TABLE turq_cheque_cheques_rolls ADD CONSTRAINT fk_cheque_cheques_rolls__cheque_cheques_id FOREIGN KEY (cheque_cheques_id) REFERENCES turq_cheque_cheques (id);
CREATE INDEX ix_cheque_cheques_rolls__cheque_rolls_id ON turq_cheque_cheques_rolls (cheque_rolls_id);
CREATE INDEX ix_cheque_cheques_rolls__cheque_cheques_id ON turq_cheque_cheques_rolls (cheque_cheques_id);

-- company_id → turq_companies FK'leri
ALTER TABLE turq_accounting_account_classes ADD CONSTRAINT fk_accounting_account_classes__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_accounting_account_types ADD CONSTRAINT fk_accounting_account_types__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_accounting_accounts ADD CONSTRAINT fk_accounting_accounts__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_accounting_journal ADD CONSTRAINT fk_accounting_journal__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_accounting_transaction_columns ADD CONSTRAINT fk_accounting_transaction_columns__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_accounting_transaction_types ADD CONSTRAINT fk_accounting_transaction_types__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_accounting_transactions ADD CONSTRAINT fk_accounting_transactions__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_bank_accounting_accounts ADD CONSTRAINT fk_bank_accounting_accounts__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_bank_accounting_types ADD CONSTRAINT fk_bank_accounting_types__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_bank_cards_secondary_accounts ADD CONSTRAINT fk_bank_cards_secondary_accounts__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_bank_secondary_accounts ADD CONSTRAINT fk_bank_secondary_accounts__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_banks_cards ADD CONSTRAINT fk_banks_cards__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_banks_transaction_bills ADD CONSTRAINT fk_banks_transaction_bills__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_banks_transaction_types ADD CONSTRAINT fk_banks_transaction_types__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_banks_transactions ADD CONSTRAINT fk_banks_transactions__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_bill_groups ADD CONSTRAINT fk_bill_groups__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_bill_in_engine_sequences ADD CONSTRAINT fk_bill_in_engine_sequences__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_bill_in_groups ADD CONSTRAINT fk_bill_in_groups__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_bills ADD CONSTRAINT fk_bills__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_cash_cards ADD CONSTRAINT fk_cash_cards__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_cash_transaction_rows ADD CONSTRAINT fk_cash_transaction_rows__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_cash_transaction_types ADD CONSTRAINT fk_cash_transaction_types__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_cash_transactions ADD CONSTRAINT fk_cash_transactions__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_cheque_cheque_in_rolls ADD CONSTRAINT fk_cheque_cheque_in_rolls__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_cheque_cheques ADD CONSTRAINT fk_cheque_cheques__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_cheque_roll_accounting_accounts ADD CONSTRAINT fk_cheque_roll_accounting_accounts__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_cheque_rolls ADD CONSTRAINT fk_cheque_rolls__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_cheque_transaction_type_groups ADD CONSTRAINT fk_cheque_transaction_type_groups__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_cheque_transaction_types ADD CONSTRAINT fk_cheque_transaction_types__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_consignment_groups ADD CONSTRAINT fk_consignment_groups__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_consignments ADD CONSTRAINT fk_consignments__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_consignments_in_group ADD CONSTRAINT fk_consignments_in_group__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_currencies ADD CONSTRAINT fk_currencies__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_currency_exchange_rates ADD CONSTRAINT fk_currency_exchange_rates__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_current_accounting_accounts ADD CONSTRAINT fk_current_accounting_accounts__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_current_accounting_types ADD CONSTRAINT fk_current_accounting_types__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_current_cards ADD CONSTRAINT fk_current_cards__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_current_cards_groups ADD CONSTRAINT fk_current_cards_groups__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_current_cards_phones ADD CONSTRAINT fk_current_cards_phones__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_current_contacts ADD CONSTRAINT fk_current_contacts__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_current_groups ADD CONSTRAINT fk_current_groups__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_current_transaction_bill ADD CONSTRAINT fk_current_transaction_bill__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_current_transaction_types ADD CONSTRAINT fk_current_transaction_types__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_current_transactions ADD CONSTRAINT fk_current_transactions__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_engine_sequences ADD CONSTRAINT fk_engine_sequences__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_group_permissions ADD CONSTRAINT fk_group_permissions__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_groups ADD CONSTRAINT fk_groups__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_inventory_accounting_accounts ADD CONSTRAINT fk_inventory_accounting_accounts__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_inventory_accounting_types ADD CONSTRAINT fk_inventory_accounting_types__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_inventory_card_groups ADD CONSTRAINT fk_inventory_card_groups__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_inventory_card_units ADD CONSTRAINT fk_inventory_card_units__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_inventory_cards ADD CONSTRAINT fk_inventory_cards__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_inventory_customize_fields ADD CONSTRAINT fk_inventory_customize_fields__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_inventory_customize_types ADD CONSTRAINT fk_inventory_customize_types__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_inventory_groups ADD CONSTRAINT fk_inventory_groups__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_inventory_prices ADD CONSTRAINT fk_inventory_prices__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_inventory_transaction_bills ADD CONSTRAINT fk_inventory_transaction_bills__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_inventory_transaction_types ADD CONSTRAINT fk_inventory_transaction_types__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_inventory_transactions ADD CONSTRAINT fk_inventory_transactions__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_inventory_units ADD CONSTRAINT fk_inventory_units__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_inventory_warehouses ADD CONSTRAINT fk_inventory_warehouses__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_order_groups ADD CONSTRAINT fk_order_groups__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_order_in_groups ADD CONSTRAINT fk_order_in_groups__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_orders ADD CONSTRAINT fk_orders__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_services ADD CONSTRAINT fk_services__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_settings ADD CONSTRAINT fk_settings__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_tradebill_rolls ADD CONSTRAINT fk_tradebill_rolls__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_tradebill_tradebills ADD CONSTRAINT fk_tradebill_tradebills__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_tradebill_tradebills_rolls ADD CONSTRAINT fk_tradebill_tradebills_rolls__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_tradebill_transaction_types ADD CONSTRAINT fk_tradebill_transaction_types__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_user_group ADD CONSTRAINT fk_user_group__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_user_permission_levels ADD CONSTRAINT fk_user_permission_levels__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_user_permissions ADD CONSTRAINT fk_user_permissions__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_users ADD CONSTRAINT fk_users__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);
ALTER TABLE turq_cheque_cheques_rolls ADD CONSTRAINT fk_cheque_cheques_rolls__company_id FOREIGN KEY (company_id) REFERENCES turq_companies (id);


-- Yabancı anahtar kısıtları (hbm many-to-one'lardan türetildi)
ALTER TABLE turq_current_transaction_bill ADD CONSTRAINT fk_current_transaction_bill__current_transactions_id_open FOREIGN KEY (current_transactions_id_open) REFERENCES turq_current_transactions (id);
CREATE INDEX ix_current_transaction_bill__current_transactions_id_open ON turq_current_transaction_bill (current_transactions_id_open);
ALTER TABLE turq_current_transaction_bill ADD CONSTRAINT fk_current_transaction_bill__current_transactions_id_close FOREIGN KEY (current_transactions_id_close) REFERENCES turq_current_transactions (id);
CREATE INDEX ix_current_transaction_bill__current_transactions_id_close ON turq_current_transaction_bill (current_transactions_id_close);
ALTER TABLE turq_cash_transactions ADD CONSTRAINT fk_cash_transactions__cash_transactions_types_id FOREIGN KEY (cash_transactions_types_id) REFERENCES turq_cash_transaction_types (id);
CREATE INDEX ix_cash_transactions__cash_transactions_types_id ON turq_cash_transactions (cash_transactions_types_id);
ALTER TABLE turq_cash_transactions ADD CONSTRAINT fk_cash_transactions__engine_sequences_id FOREIGN KEY (engine_sequences_id) REFERENCES turq_engine_sequences (id);
CREATE INDEX ix_cash_transactions__engine_sequences_id ON turq_cash_transactions (engine_sequences_id);
ALTER TABLE turq_tradebill_tradebills_rolls ADD CONSTRAINT fk_tradebill_tradebills_rolls__tradebill_tradebills_id FOREIGN KEY (tradebill_tradebills_id) REFERENCES turq_tradebill_tradebills (id);
CREATE INDEX ix_tradebill_tradebills_rolls__tradebill_tradebills_id ON turq_tradebill_tradebills_rolls (tradebill_tradebills_id);
ALTER TABLE turq_tradebill_tradebills_rolls ADD CONSTRAINT fk_tradebill_tradebills_rolls__tradebill_rolls_id FOREIGN KEY (tradebill_rolls_id) REFERENCES turq_tradebill_rolls (id);
CREATE INDEX ix_tradebill_tradebills_rolls__tradebill_rolls_id ON turq_tradebill_tradebills_rolls (tradebill_rolls_id);
ALTER TABLE turq_accounting_transactions ADD CONSTRAINT fk_accounting_transactions__accounting_journal_id FOREIGN KEY (accounting_journal_id) REFERENCES turq_accounting_journal (id);
CREATE INDEX ix_accounting_transactions__accounting_journal_id ON turq_accounting_transactions (accounting_journal_id);
ALTER TABLE turq_accounting_transactions ADD CONSTRAINT fk_accounting_transactions__exchange_rate_id FOREIGN KEY (exchange_rate_id) REFERENCES turq_currency_exchange_rates (id);
CREATE INDEX ix_accounting_transactions__exchange_rate_id ON turq_accounting_transactions (exchange_rate_id);
ALTER TABLE turq_accounting_transactions ADD CONSTRAINT fk_accounting_transactions__accounting_transaction_types_id FOREIGN KEY (accounting_transaction_types_id) REFERENCES turq_accounting_transaction_types (id);
CREATE INDEX ix_accounting_transactions__accounting_transaction_types_id ON turq_accounting_transactions (accounting_transaction_types_id);
ALTER TABLE turq_accounting_transactions ADD CONSTRAINT fk_accounting_transactions__module_id FOREIGN KEY (module_id) REFERENCES turq_modules (id);
CREATE INDEX ix_accounting_transactions__module_id ON turq_accounting_transactions (module_id);
ALTER TABLE turq_accounting_transactions ADD CONSTRAINT fk_accounting_transactions__engine_sequences_id FOREIGN KEY (engine_sequences_id) REFERENCES turq_engine_sequences (id);
CREATE INDEX ix_accounting_transactions__engine_sequences_id ON turq_accounting_transactions (engine_sequences_id);
ALTER TABLE turq_consignments ADD CONSTRAINT fk_consignments__exchange_rate_id FOREIGN KEY (exchange_rate_id) REFERENCES turq_currency_exchange_rates (id);
CREATE INDEX ix_consignments__exchange_rate_id ON turq_consignments (exchange_rate_id);
ALTER TABLE turq_consignments ADD CONSTRAINT fk_consignments__engine_sequences_id FOREIGN KEY (engine_sequences_id) REFERENCES turq_engine_sequences (id);
CREATE INDEX ix_consignments__engine_sequences_id ON turq_consignments (engine_sequences_id);
ALTER TABLE turq_consignments ADD CONSTRAINT fk_consignments__current_cards_id FOREIGN KEY (current_cards_id) REFERENCES turq_current_cards (id);
CREATE INDEX ix_consignments__current_cards_id ON turq_consignments (current_cards_id);
ALTER TABLE turq_bank_cards_secondary_accounts ADD CONSTRAINT fk_bank_cards_secondary_accounts__bank_secondary_accounts_id FOREIGN KEY (bank_secondary_accounts_id) REFERENCES turq_bank_secondary_accounts (id);
CREATE INDEX ix_bank_cards_secondary_accounts__bank_secondary_accounts_id ON turq_bank_cards_secondary_accounts (bank_secondary_accounts_id);
ALTER TABLE turq_bank_cards_secondary_accounts ADD CONSTRAINT fk_bank_cards_secondary_accounts__accounting_accounts_id FOREIGN KEY (accounting_accounts_id) REFERENCES turq_accounting_accounts (id);
CREATE INDEX ix_bank_cards_secondary_accounts__accounting_accounts_id ON turq_bank_cards_secondary_accounts (accounting_accounts_id);
ALTER TABLE turq_bank_cards_secondary_accounts ADD CONSTRAINT fk_bank_cards_secondary_accounts__bank_cards_id FOREIGN KEY (bank_cards_id) REFERENCES turq_banks_cards (id);
CREATE INDEX ix_bank_cards_secondary_accounts__bank_cards_id ON turq_bank_cards_secondary_accounts (bank_cards_id);
ALTER TABLE turq_banks_transaction_bills ADD CONSTRAINT fk_banks_transaction_bills__engine_sequences_id FOREIGN KEY (engine_sequences_id) REFERENCES turq_engine_sequences (id);
CREATE INDEX ix_banks_transaction_bills__engine_sequences_id ON turq_banks_transaction_bills (engine_sequences_id);
ALTER TABLE turq_banks_transaction_bills ADD CONSTRAINT fk_banks_transaction_bills__banks_transaction_types_id FOREIGN KEY (banks_transaction_types_id) REFERENCES turq_banks_transaction_types (id);
CREATE INDEX ix_banks_transaction_bills__banks_transaction_types_id ON turq_banks_transaction_bills (banks_transaction_types_id);
ALTER TABLE turq_cheque_cheque_in_rolls ADD CONSTRAINT fk_cheque_cheque_in_rolls__cheque_rolls_id FOREIGN KEY (cheque_rolls_id) REFERENCES turq_cheque_rolls (id);
CREATE INDEX ix_cheque_cheque_in_rolls__cheque_rolls_id ON turq_cheque_cheque_in_rolls (cheque_rolls_id);
ALTER TABLE turq_cheque_cheque_in_rolls ADD CONSTRAINT fk_cheque_cheque_in_rolls__cheque_cheques_id FOREIGN KEY (cheque_cheques_id) REFERENCES turq_cheque_cheques (id);
CREATE INDEX ix_cheque_cheque_in_rolls__cheque_cheques_id ON turq_cheque_cheque_in_rolls (cheque_cheques_id);
ALTER TABLE turq_bills ADD CONSTRAINT fk_bills__exchange_rate_id FOREIGN KEY (exchange_rate_id) REFERENCES turq_currency_exchange_rates (id);
CREATE INDEX ix_bills__exchange_rate_id ON turq_bills (exchange_rate_id);
ALTER TABLE turq_bills ADD CONSTRAINT fk_bills__engine_sequences_id FOREIGN KEY (engine_sequences_id) REFERENCES turq_engine_sequences (id);
CREATE INDEX ix_bills__engine_sequences_id ON turq_bills (engine_sequences_id);
ALTER TABLE turq_bills ADD CONSTRAINT fk_bills__current_cards_id FOREIGN KEY (current_cards_id) REFERENCES turq_current_cards (id);
CREATE INDEX ix_bills__current_cards_id ON turq_bills (current_cards_id);
ALTER TABLE turq_inventory_accounting_accounts ADD CONSTRAINT fk_inventory_accounting_accounts__accounting_accounts_id FOREIGN KEY (accounting_accounts_id) REFERENCES turq_accounting_accounts (id);
CREATE INDEX ix_inventory_accounting_accounts__accounting_accounts_id ON turq_inventory_accounting_accounts (accounting_accounts_id);
ALTER TABLE turq_inventory_accounting_accounts ADD CONSTRAINT fk_inventory_accounting_accounts__inventory_accounting_types_id FOREIGN KEY (inventory_accounting_types_id) REFERENCES turq_inventory_accounting_types (id);
CREATE INDEX ix_inventory_accounting_accounts__inventory_accounting_types_id ON turq_inventory_accounting_accounts (inventory_accounting_types_id);
ALTER TABLE turq_inventory_accounting_accounts ADD CONSTRAINT fk_inventory_accounting_accounts__inventory_cards_id FOREIGN KEY (inventory_cards_id) REFERENCES turq_inventory_cards (id);
CREATE INDEX ix_inventory_accounting_accounts__inventory_cards_id ON turq_inventory_accounting_accounts (inventory_cards_id);
ALTER TABLE turq_order_in_groups ADD CONSTRAINT fk_order_in_groups__orders_id FOREIGN KEY (orders_id) REFERENCES turq_orders (id);
CREATE INDEX ix_order_in_groups__orders_id ON turq_order_in_groups (orders_id);
ALTER TABLE turq_order_in_groups ADD CONSTRAINT fk_order_in_groups__order_groups_id FOREIGN KEY (order_groups_id) REFERENCES turq_order_groups (id);
CREATE INDEX ix_order_in_groups__order_groups_id ON turq_order_in_groups (order_groups_id);
ALTER TABLE turq_current_cards_phones ADD CONSTRAINT fk_current_cards_phones__current_cards_id FOREIGN KEY (current_cards_id) REFERENCES turq_current_cards (id);
CREATE INDEX ix_current_cards_phones__current_cards_id ON turq_current_cards_phones (current_cards_id);
ALTER TABLE turq_accounting_accounts ADD CONSTRAINT fk_accounting_accounts__accounting_class_id FOREIGN KEY (accounting_class_id) REFERENCES turq_accounting_account_classes (id);
CREATE INDEX ix_accounting_accounts__accounting_class_id ON turq_accounting_accounts (accounting_class_id);
ALTER TABLE turq_accounting_accounts ADD CONSTRAINT fk_accounting_accounts__accounting_types_id FOREIGN KEY (accounting_types_id) REFERENCES turq_accounting_account_types (id);
CREATE INDEX ix_accounting_accounts__accounting_types_id ON turq_accounting_accounts (accounting_types_id);
ALTER TABLE turq_accounting_accounts ADD CONSTRAINT fk_accounting_accounts__top_account FOREIGN KEY (top_account) REFERENCES turq_accounting_accounts (id);
CREATE INDEX ix_accounting_accounts__top_account ON turq_accounting_accounts (top_account);
ALTER TABLE turq_accounting_accounts ADD CONSTRAINT fk_accounting_accounts__parent_account FOREIGN KEY (parent_account) REFERENCES turq_accounting_accounts (id);
CREATE INDEX ix_accounting_accounts__parent_account ON turq_accounting_accounts (parent_account);
ALTER TABLE turq_bill_in_engine_sequences ADD CONSTRAINT fk_bill_in_engine_sequences__engine_sequences_id FOREIGN KEY (engine_sequences_id) REFERENCES turq_engine_sequences (id);
CREATE INDEX ix_bill_in_engine_sequences__engine_sequences_id ON turq_bill_in_engine_sequences (engine_sequences_id);
ALTER TABLE turq_bill_in_engine_sequences ADD CONSTRAINT fk_bill_in_engine_sequences__bills_id FOREIGN KEY (bills_id) REFERENCES turq_bills (id);
CREATE INDEX ix_bill_in_engine_sequences__bills_id ON turq_bill_in_engine_sequences (bills_id);
ALTER TABLE turq_module_components ADD CONSTRAINT fk_module_components__modules_id FOREIGN KEY (modules_id) REFERENCES turq_modules (id);
CREATE INDEX ix_module_components__modules_id ON turq_module_components (modules_id);
ALTER TABLE turq_cheque_cheques ADD CONSTRAINT fk_cheque_cheques__currencies_id FOREIGN KEY (currencies_id) REFERENCES turq_currencies (id);
CREATE INDEX ix_cheque_cheques__currencies_id ON turq_cheque_cheques (currencies_id);
ALTER TABLE turq_cheque_cheques ADD CONSTRAINT fk_cheque_cheques__exchange_rate_id FOREIGN KEY (exchange_rate_id) REFERENCES turq_currency_exchange_rates (id);
CREATE INDEX ix_cheque_cheques__exchange_rate_id ON turq_cheque_cheques (exchange_rate_id);
ALTER TABLE turq_cheque_cheques ADD CONSTRAINT fk_cheque_cheques__banks_id FOREIGN KEY (banks_id) REFERENCES turq_banks_cards (id);
CREATE INDEX ix_cheque_cheques__banks_id ON turq_cheque_cheques (banks_id);
ALTER TABLE turq_current_cards_groups ADD CONSTRAINT fk_current_cards_groups__current_groups_id FOREIGN KEY (current_groups_id) REFERENCES turq_current_groups (id);
CREATE INDEX ix_current_cards_groups__current_groups_id ON turq_current_cards_groups (current_groups_id);
ALTER TABLE turq_current_cards_groups ADD CONSTRAINT fk_current_cards_groups__current_cards_id FOREIGN KEY (current_cards_id) REFERENCES turq_current_cards (id);
CREATE INDEX ix_current_cards_groups__current_cards_id ON turq_current_cards_groups (current_cards_id);
ALTER TABLE turq_bill_in_groups ADD CONSTRAINT fk_bill_in_groups__bill_groups_id FOREIGN KEY (bill_groups_id) REFERENCES turq_bill_groups (id);
CREATE INDEX ix_bill_in_groups__bill_groups_id ON turq_bill_in_groups (bill_groups_id);
ALTER TABLE turq_bill_in_groups ADD CONSTRAINT fk_bill_in_groups__bills_id FOREIGN KEY (bills_id) REFERENCES turq_bills (id);
CREATE INDEX ix_bill_in_groups__bills_id ON turq_bill_in_groups (bills_id);
ALTER TABLE turq_inventory_prices ADD CONSTRAINT fk_inventory_prices__currencies_id FOREIGN KEY (currencies_id) REFERENCES turq_currencies (id);
CREATE INDEX ix_inventory_prices__currencies_id ON turq_inventory_prices (currencies_id);
ALTER TABLE turq_inventory_prices ADD CONSTRAINT fk_inventory_prices__inventory_cards_id FOREIGN KEY (inventory_cards_id) REFERENCES turq_inventory_cards (id);
CREATE INDEX ix_inventory_prices__inventory_cards_id ON turq_inventory_prices (inventory_cards_id);
ALTER TABLE turq_cash_transaction_rows ADD CONSTRAINT fk_cash_transaction_rows__cash_transactions_id FOREIGN KEY (cash_transactions_id) REFERENCES turq_cash_transactions (id);
CREATE INDEX ix_cash_transaction_rows__cash_transactions_id ON turq_cash_transaction_rows (cash_transactions_id);
ALTER TABLE turq_cash_transaction_rows ADD CONSTRAINT fk_cash_transaction_rows__cash_cards_id FOREIGN KEY (cash_cards_id) REFERENCES turq_cash_cards (id);
CREATE INDEX ix_cash_transaction_rows__cash_cards_id ON turq_cash_transaction_rows (cash_cards_id);
ALTER TABLE turq_cash_transaction_rows ADD CONSTRAINT fk_cash_transaction_rows__exchange_rate_id FOREIGN KEY (exchange_rate_id) REFERENCES turq_currency_exchange_rates (id);
CREATE INDEX ix_cash_transaction_rows__exchange_rate_id ON turq_cash_transaction_rows (exchange_rate_id);
ALTER TABLE turq_cash_transaction_rows ADD CONSTRAINT fk_cash_transaction_rows__accounting_accounts_id FOREIGN KEY (accounting_accounts_id) REFERENCES turq_accounting_accounts (id);
CREATE INDEX ix_cash_transaction_rows__accounting_accounts_id ON turq_cash_transaction_rows (accounting_accounts_id);
ALTER TABLE turq_current_transactions ADD CONSTRAINT fk_current_transactions__exchange_rate_id FOREIGN KEY (exchange_rate_id) REFERENCES turq_currency_exchange_rates (id);
CREATE INDEX ix_current_transactions__exchange_rate_id ON turq_current_transactions (exchange_rate_id);
ALTER TABLE turq_current_transactions ADD CONSTRAINT fk_current_transactions__current_transaction_types_id FOREIGN KEY (current_transaction_types_id) REFERENCES turq_current_transaction_types (id);
CREATE INDEX ix_current_transactions__current_transaction_types_id ON turq_current_transactions (current_transaction_types_id);
ALTER TABLE turq_current_transactions ADD CONSTRAINT fk_current_transactions__engine_sequences_id FOREIGN KEY (engine_sequences_id) REFERENCES turq_engine_sequences (id);
CREATE INDEX ix_current_transactions__engine_sequences_id ON turq_current_transactions (engine_sequences_id);
ALTER TABLE turq_current_transactions ADD CONSTRAINT fk_current_transactions__current_cards_id FOREIGN KEY (current_cards_id) REFERENCES turq_current_cards (id);
CREATE INDEX ix_current_transactions__current_cards_id ON turq_current_transactions (current_cards_id);
ALTER TABLE turq_banks_cards ADD CONSTRAINT fk_banks_cards__currencies_id FOREIGN KEY (currencies_id) REFERENCES turq_currencies (id);
CREATE INDEX ix_banks_cards__currencies_id ON turq_banks_cards (currencies_id);
ALTER TABLE turq_inventory_transaction_bills ADD CONSTRAINT fk_inventory_transaction_bills__warehouse_out FOREIGN KEY (warehouse_out) REFERENCES turq_accounting_account_classes (id);
CREATE INDEX ix_inventory_transaction_bills__warehouse_out ON turq_inventory_transaction_bills (warehouse_out);
ALTER TABLE turq_inventory_transaction_bills ADD CONSTRAINT fk_inventory_transaction_bills__warehouse_in FOREIGN KEY (warehouse_in) REFERENCES turq_inventory_warehouses (id);
CREATE INDEX ix_inventory_transaction_bills__warehouse_in ON turq_inventory_transaction_bills (warehouse_in);
ALTER TABLE turq_inventory_transaction_bills ADD CONSTRAINT fk_inventory_transaction_bills__engine_sequence FOREIGN KEY (engine_sequence) REFERENCES turq_engine_sequences (id);
CREATE INDEX ix_inventory_transaction_bills__engine_sequence ON turq_inventory_transaction_bills (engine_sequence);
ALTER TABLE turq_engine_sequences ADD CONSTRAINT fk_engine_sequences__modules_id FOREIGN KEY (modules_id) REFERENCES turq_modules (id);
CREATE INDEX ix_engine_sequences__modules_id ON turq_engine_sequences (modules_id);
ALTER TABLE turq_inventory_customize_fields ADD CONSTRAINT fk_inventory_customize_fields__customize_type_id FOREIGN KEY (customize_type_id) REFERENCES turq_inventory_customize_types (id);
CREATE INDEX ix_inventory_customize_fields__customize_type_id ON turq_inventory_customize_fields (customize_type_id);
ALTER TABLE turq_inventory_customize_fields ADD CONSTRAINT fk_inventory_customize_fields__inventory_card_id FOREIGN KEY (inventory_card_id) REFERENCES turq_inventory_cards (id);
CREATE INDEX ix_inventory_customize_fields__inventory_card_id ON turq_inventory_customize_fields (inventory_card_id);
ALTER TABLE turq_cheque_rolls ADD CONSTRAINT fk_cheque_rolls__cheque_transaction_types_id FOREIGN KEY (cheque_transaction_types_id) REFERENCES turq_cheque_transaction_types (id);
CREATE INDEX ix_cheque_rolls__cheque_transaction_types_id ON turq_cheque_rolls (cheque_transaction_types_id);
ALTER TABLE turq_cheque_rolls ADD CONSTRAINT fk_cheque_rolls__banks_cards_id FOREIGN KEY (banks_cards_id) REFERENCES turq_banks_cards (id);
CREATE INDEX ix_cheque_rolls__banks_cards_id ON turq_cheque_rolls (banks_cards_id);
ALTER TABLE turq_cheque_rolls ADD CONSTRAINT fk_cheque_rolls__engine_sequences_id FOREIGN KEY (engine_sequences_id) REFERENCES turq_engine_sequences (id);
CREATE INDEX ix_cheque_rolls__engine_sequences_id ON turq_cheque_rolls (engine_sequences_id);
ALTER TABLE turq_cheque_rolls ADD CONSTRAINT fk_cheque_rolls__current_cards_id FOREIGN KEY (current_cards_id) REFERENCES turq_current_cards (id);
CREATE INDEX ix_cheque_rolls__current_cards_id ON turq_cheque_rolls (current_cards_id);
ALTER TABLE turq_inventory_groups ADD CONSTRAINT fk_inventory_groups__parent_group FOREIGN KEY (parent_group) REFERENCES turq_inventory_groups (id);
CREATE INDEX ix_inventory_groups__parent_group ON turq_inventory_groups (parent_group);
ALTER TABLE turq_engine_menu ADD CONSTRAINT fk_engine_menu__menu_module_component FOREIGN KEY (menu_module_component) REFERENCES turq_module_components (id);
CREATE INDEX ix_engine_menu__menu_module_component ON turq_engine_menu (menu_module_component);
ALTER TABLE turq_current_accounting_accounts ADD CONSTRAINT fk_current_accounting_accounts__current_accounting_types_id FOREIGN KEY (current_accounting_types_id) REFERENCES turq_current_accounting_types (id);
CREATE INDEX ix_current_accounting_accounts__current_accounting_types_id ON turq_current_accounting_accounts (current_accounting_types_id);
ALTER TABLE turq_current_accounting_accounts ADD CONSTRAINT fk_current_accounting_accounts__accounting_accounts_id FOREIGN KEY (accounting_accounts_id) REFERENCES turq_accounting_accounts (id);
CREATE INDEX ix_current_accounting_accounts__accounting_accounts_id ON turq_current_accounting_accounts (accounting_accounts_id);
ALTER TABLE turq_current_accounting_accounts ADD CONSTRAINT fk_current_accounting_accounts__current_cards_id FOREIGN KEY (current_cards_id) REFERENCES turq_current_cards (id);
CREATE INDEX ix_current_accounting_accounts__current_cards_id ON turq_current_accounting_accounts (current_cards_id);
ALTER TABLE turq_cheque_transaction_types ADD CONSTRAINT fk_cheque_transaction_types__transaction_types_parent FOREIGN KEY (transaction_types_parent) REFERENCES turq_cheque_transaction_type_groups (id);
CREATE INDEX ix_cheque_transaction_types__transaction_types_parent ON turq_cheque_transaction_types (transaction_types_parent);
ALTER TABLE turq_tradebill_tradebills ADD CONSTRAINT fk_tradebill_tradebills__currencies_id FOREIGN KEY (currencies_id) REFERENCES turq_currencies (id);
CREATE INDEX ix_tradebill_tradebills__currencies_id ON turq_tradebill_tradebills (currencies_id);
ALTER TABLE turq_tradebill_transaction_types ADD CONSTRAINT fk_tradebill_transaction_types__accounting_accounts_id FOREIGN KEY (accounting_accounts_id) REFERENCES turq_accounting_accounts (id);
CREATE INDEX ix_tradebill_transaction_types__accounting_accounts_id ON turq_tradebill_transaction_types (accounting_accounts_id);
ALTER TABLE turq_inventory_card_units ADD CONSTRAINT fk_inventory_card_units__inventory_units_id FOREIGN KEY (inventory_units_id) REFERENCES turq_inventory_units (id);
CREATE INDEX ix_inventory_card_units__inventory_units_id ON turq_inventory_card_units (inventory_units_id);
ALTER TABLE turq_inventory_card_units ADD CONSTRAINT fk_inventory_card_units__inventory_cards_id FOREIGN KEY (inventory_cards_id) REFERENCES turq_inventory_cards (id);
CREATE INDEX ix_inventory_card_units__inventory_cards_id ON turq_inventory_card_units (inventory_cards_id);
ALTER TABLE turq_cash_cards ADD CONSTRAINT fk_cash_cards__accounting_accounts_id FOREIGN KEY (accounting_accounts_id) REFERENCES turq_accounting_accounts (id);
CREATE INDEX ix_cash_cards__accounting_accounts_id ON turq_cash_cards (accounting_accounts_id);
ALTER TABLE turq_inventory_transactions ADD CONSTRAINT fk_inventory_transactions__inventory_warehouses_id FOREIGN KEY (inventory_warehouses_id) REFERENCES turq_inventory_warehouses (id);
CREATE INDEX ix_inventory_transactions__inventory_warehouses_id ON turq_inventory_transactions (inventory_warehouses_id);
ALTER TABLE turq_inventory_transactions ADD CONSTRAINT fk_inventory_transactions__transaction_type FOREIGN KEY (transaction_type) REFERENCES turq_inventory_transaction_types (id);
CREATE INDEX ix_inventory_transactions__transaction_type ON turq_inventory_transactions (transaction_type);
ALTER TABLE turq_inventory_transactions ADD CONSTRAINT fk_inventory_transactions__exchange_rate_id FOREIGN KEY (exchange_rate_id) REFERENCES turq_currency_exchange_rates (id);
CREATE INDEX ix_inventory_transactions__exchange_rate_id ON turq_inventory_transactions (exchange_rate_id);
ALTER TABLE turq_inventory_transactions ADD CONSTRAINT fk_inventory_transactions__inventory_units_id FOREIGN KEY (inventory_units_id) REFERENCES turq_inventory_units (id);
CREATE INDEX ix_inventory_transactions__inventory_units_id ON turq_inventory_transactions (inventory_units_id);
ALTER TABLE turq_inventory_transactions ADD CONSTRAINT fk_inventory_transactions__engine_sequences_id FOREIGN KEY (engine_sequences_id) REFERENCES turq_engine_sequences (id);
CREATE INDEX ix_inventory_transactions__engine_sequences_id ON turq_inventory_transactions (engine_sequences_id);
ALTER TABLE turq_inventory_transactions ADD CONSTRAINT fk_inventory_transactions__inventory_cards_id FOREIGN KEY (inventory_cards_id) REFERENCES turq_inventory_cards (id);
CREATE INDEX ix_inventory_transactions__inventory_cards_id ON turq_inventory_transactions (inventory_cards_id);
ALTER TABLE turq_inventory_transactions ADD CONSTRAINT fk_inventory_transactions__current_cards_id FOREIGN KEY (current_cards_id) REFERENCES turq_current_cards (id);
CREATE INDEX ix_inventory_transactions__current_cards_id ON turq_inventory_transactions (current_cards_id);
ALTER TABLE turq_cheque_roll_accounting_accounts ADD CONSTRAINT fk_cheque_roll_accounting_accounts__accounting_accounts_id FOREIGN KEY (accounting_accounts_id) REFERENCES turq_accounting_accounts (id);
CREATE INDEX ix_cheque_roll_accounting_accounts__accounting_accounts_id ON turq_cheque_roll_accounting_accounts (accounting_accounts_id);
ALTER TABLE turq_orders ADD CONSTRAINT fk_orders__bills_id FOREIGN KEY (bills_id) REFERENCES turq_bills (id);
CREATE INDEX ix_orders__bills_id ON turq_orders (bills_id);
ALTER TABLE turq_orders ADD CONSTRAINT fk_orders__current_cards_id FOREIGN KEY (current_cards_id) REFERENCES turq_current_cards (id);
CREATE INDEX ix_orders__current_cards_id ON turq_orders (current_cards_id);
ALTER TABLE turq_tradebill_rolls ADD CONSTRAINT fk_tradebill_rolls__banks_cards_id FOREIGN KEY (banks_cards_id) REFERENCES turq_banks_cards (id);
CREATE INDEX ix_tradebill_rolls__banks_cards_id ON turq_tradebill_rolls (banks_cards_id);
ALTER TABLE turq_tradebill_rolls ADD CONSTRAINT fk_tradebill_rolls__current_cards_id FOREIGN KEY (current_cards_id) REFERENCES turq_current_cards (id);
CREATE INDEX ix_tradebill_rolls__current_cards_id ON turq_tradebill_rolls (current_cards_id);
ALTER TABLE turq_tradebill_rolls ADD CONSTRAINT fk_tradebill_rolls__tradebill_transaction_types_id FOREIGN KEY (tradebill_transaction_types_id) REFERENCES turq_tradebill_transaction_types (id);
CREATE INDEX ix_tradebill_rolls__tradebill_transaction_types_id ON turq_tradebill_rolls (tradebill_transaction_types_id);
ALTER TABLE turq_bank_accounting_accounts ADD CONSTRAINT fk_bank_accounting_accounts__bank_accounting_types_id FOREIGN KEY (bank_accounting_types_id) REFERENCES turq_bank_accounting_types (id);
CREATE INDEX ix_bank_accounting_accounts__bank_accounting_types_id ON turq_bank_accounting_accounts (bank_accounting_types_id);
ALTER TABLE turq_bank_accounting_accounts ADD CONSTRAINT fk_bank_accounting_accounts__accounting_accounts_id FOREIGN KEY (accounting_accounts_id) REFERENCES turq_accounting_accounts (id);
CREATE INDEX ix_bank_accounting_accounts__accounting_accounts_id ON turq_bank_accounting_accounts (accounting_accounts_id);
ALTER TABLE turq_bank_accounting_accounts ADD CONSTRAINT fk_bank_accounting_accounts__banks_cards_id FOREIGN KEY (banks_cards_id) REFERENCES turq_banks_cards (id);
CREATE INDEX ix_bank_accounting_accounts__banks_cards_id ON turq_bank_accounting_accounts (banks_cards_id);
ALTER TABLE turq_accounting_transaction_columns ADD CONSTRAINT fk_accounting_transaction_columns__exchange_rate_id FOREIGN KEY (exchange_rate_id) REFERENCES turq_currency_exchange_rates (id);
CREATE INDEX ix_accounting_transaction_columns__exchange_rate_id ON turq_accounting_transaction_columns (exchange_rate_id);
ALTER TABLE turq_accounting_transaction_columns ADD CONSTRAINT fk_accounting_transaction_columns__accounting_accounts_id FOREIGN KEY (accounting_accounts_id) REFERENCES turq_accounting_accounts (id);
CREATE INDEX ix_accounting_transaction_columns__accounting_accounts_id ON turq_accounting_transaction_columns (accounting_accounts_id);
ALTER TABLE turq_accounting_transaction_columns ADD CONSTRAINT fk_accounting_transaction_columns__accounting_transactions_id FOREIGN KEY (accounting_transactions_id) REFERENCES turq_accounting_transactions (id);
CREATE INDEX ix_accounting_transaction_columns__accounting_transactions_id ON turq_accounting_transaction_columns (accounting_transactions_id);
ALTER TABLE turq_currency_exchange_rates ADD CONSTRAINT fk_currency_exchange_rates__base_currency_id FOREIGN KEY (base_currency_id) REFERENCES turq_currencies (id);
CREATE INDEX ix_currency_exchange_rates__base_currency_id ON turq_currency_exchange_rates (base_currency_id);
ALTER TABLE turq_currency_exchange_rates ADD CONSTRAINT fk_currency_exchange_rates__exchange_currency_id FOREIGN KEY (exchange_currency_id) REFERENCES turq_currencies (id);
CREATE INDEX ix_currency_exchange_rates__exchange_currency_id ON turq_currency_exchange_rates (exchange_currency_id);
ALTER TABLE turq_inventory_card_groups ADD CONSTRAINT fk_inventory_card_groups__inventory_groups_id FOREIGN KEY (inventory_groups_id) REFERENCES turq_inventory_groups (id);
CREATE INDEX ix_inventory_card_groups__inventory_groups_id ON turq_inventory_card_groups (inventory_groups_id);
ALTER TABLE turq_inventory_card_groups ADD CONSTRAINT fk_inventory_card_groups__inventory_cards_id FOREIGN KEY (inventory_cards_id) REFERENCES turq_inventory_cards (id);
CREATE INDEX ix_inventory_card_groups__inventory_cards_id ON turq_inventory_card_groups (inventory_cards_id);
ALTER TABLE turq_user_permissions ADD CONSTRAINT fk_user_permissions__module_components_id FOREIGN KEY (module_components_id) REFERENCES turq_module_components (id);
CREATE INDEX ix_user_permissions__module_components_id ON turq_user_permissions (module_components_id);
ALTER TABLE turq_user_permissions ADD CONSTRAINT fk_user_permissions__users_id FOREIGN KEY (users_id) REFERENCES turq_users (id);
CREATE INDEX ix_user_permissions__users_id ON turq_user_permissions (users_id);
ALTER TABLE turq_user_permissions ADD CONSTRAINT fk_user_permissions__modules_id FOREIGN KEY (modules_id) REFERENCES turq_modules (id);
CREATE INDEX ix_user_permissions__modules_id ON turq_user_permissions (modules_id);
ALTER TABLE turq_user_permissions ADD CONSTRAINT fk_user_permissions__user_permissions_level FOREIGN KEY (user_permissions_level) REFERENCES turq_user_permission_levels (id);
CREATE INDEX ix_user_permissions__user_permissions_level ON turq_user_permissions (user_permissions_level);
ALTER TABLE turq_current_contacts ADD CONSTRAINT fk_current_contacts__current_cards_id FOREIGN KEY (current_cards_id) REFERENCES turq_current_cards (id);
CREATE INDEX ix_current_contacts__current_cards_id ON turq_current_contacts (current_cards_id);
ALTER TABLE turq_banks_transactions ADD CONSTRAINT fk_banks_transactions__exchange_rate_id FOREIGN KEY (exchange_rate_id) REFERENCES turq_currency_exchange_rates (id);
CREATE INDEX ix_banks_transactions__exchange_rate_id ON turq_banks_transactions (exchange_rate_id);
ALTER TABLE turq_banks_transactions ADD CONSTRAINT fk_banks_transactions__bank_transactions_bills_id FOREIGN KEY (bank_transactions_bills_id) REFERENCES turq_banks_transaction_bills (id);
CREATE INDEX ix_banks_transactions__bank_transactions_bills_id ON turq_banks_transactions (bank_transactions_bills_id);
ALTER TABLE turq_banks_transactions ADD CONSTRAINT fk_banks_transactions__banks_cards_id FOREIGN KEY (banks_cards_id) REFERENCES turq_banks_cards (id);
CREATE INDEX ix_banks_transactions__banks_cards_id ON turq_banks_transactions (banks_cards_id);
ALTER TABLE turq_user_group ADD CONSTRAINT fk_user_group__groups_id FOREIGN KEY (groups_id) REFERENCES turq_groups (id);
CREATE INDEX ix_user_group__groups_id ON turq_user_group (groups_id);
ALTER TABLE turq_user_group ADD CONSTRAINT fk_user_group__users_id FOREIGN KEY (users_id) REFERENCES turq_users (id);
CREATE INDEX ix_user_group__users_id ON turq_user_group (users_id);
ALTER TABLE turq_group_permissions ADD CONSTRAINT fk_group_permissions__module_components_id FOREIGN KEY (module_components_id) REFERENCES turq_module_components (id);
CREATE INDEX ix_group_permissions__module_components_id ON turq_group_permissions (module_components_id);
ALTER TABLE turq_group_permissions ADD CONSTRAINT fk_group_permissions__groups_id FOREIGN KEY (groups_id) REFERENCES turq_groups (id);
CREATE INDEX ix_group_permissions__groups_id ON turq_group_permissions (groups_id);
ALTER TABLE turq_group_permissions ADD CONSTRAINT fk_group_permissions__modules_id FOREIGN KEY (modules_id) REFERENCES turq_modules (id);
CREATE INDEX ix_group_permissions__modules_id ON turq_group_permissions (modules_id);
ALTER TABLE turq_consignments_in_group ADD CONSTRAINT fk_consignments_in_group__consignments_groups_id FOREIGN KEY (consignments_groups_id) REFERENCES turq_consignment_groups (id);
CREATE INDEX ix_consignments_in_group__consignments_groups_id ON turq_consignments_in_group (consignments_groups_id);
ALTER TABLE turq_consignments_in_group ADD CONSTRAINT fk_consignments_in_group__consignment_id FOREIGN KEY (consignment_id) REFERENCES turq_consignments (id);
CREATE INDEX ix_consignments_in_group__consignment_id ON turq_consignments_in_group (consignment_id);
