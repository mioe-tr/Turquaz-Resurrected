--
-- PostgreSQL database dump
--

\restrict jNn5frb7e9ouAVKgU7Tn8CYUOZqhTyhYC0fVKqt4hVtCc7ki3k0pBbhQ5WXVMaj

-- Dumped from database version 8.0.1
-- Dumped by pg_dump version 8.0.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'SQL_ASCII';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET escape_string_warning = off;
SET row_security = off;

--
-- Name: DUMP TIMESTAMP; Type: DUMP TIMESTAMP; Schema: -; Owner: -
--

-- Started on 2005-08-31 15:12:56 GTB Standard Time


--
-- Name: public; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA public;


--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON SCHEMA public IS 'Standard public schema';


SET search_path = public, pg_catalog;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE hibernate_sequence
    START WITH 10000
    INCREMENT BY 1
    NO MAXVALUE
    MINVALUE 10000
    CACHE 1;


--
-- Name: service_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE service_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


SET default_tablespace = '';

--
-- Name: turq_accounting_account_classes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_accounting_account_classes (
    id integer NOT NULL,
    accounting_classes_name character varying(50) NOT NULL,
    accounting_classes_definition character varying(50) NOT NULL,
    created_by character varying(25) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(25) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_accounting_account_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_accounting_account_types (
    id integer NOT NULL,
    accounting_types_name character varying(50) NOT NULL,
    accounting_types_definition character varying(250) NOT NULL,
    created_by character varying(25) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(25) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_accounting_accounts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_accounting_accounts (
    id integer NOT NULL,
    account_name character varying(250) NOT NULL,
    account_code character varying(50) NOT NULL,
    parent_account integer NOT NULL,
    top_account integer NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    update_date date NOT NULL,
    accounting_types_id integer,
    accounting_class_id integer
);


--
-- Name: turq_accounting_journal; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_accounting_journal (
    id integer NOT NULL,
    journal_date date NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_accounting_transaction_columns; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_accounting_transaction_columns (
    id integer NOT NULL,
    accounting_accounts_id integer NOT NULL,
    dept_amount numeric NOT NULL,
    credit_amount numeric NOT NULL,
    accounting_transactions_id integer NOT NULL,
    transaction_definition character varying(250) NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL,
    rows_dept_in_base_currency numeric NOT NULL,
    rows_credit_in_base_currency numeric NOT NULL,
    exchange_rate_id integer NOT NULL
);


--
-- Name: turq_accounting_transaction_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_accounting_transaction_types (
    id integer NOT NULL,
    types_name character varying NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_accounting_transactions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_accounting_transactions (
    id integer NOT NULL,
    accounting_journal_id integer NOT NULL,
    accounting_transaction_types_id integer NOT NULL,
    transactions_date date NOT NULL,
    module_id integer NOT NULL,
    transaction_document_no character varying(50) NOT NULL,
    engine_sequences_id integer NOT NULL,
    transaction_description character varying(250) NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL,
    exchange_rate_id integer NOT NULL
);


--
-- Name: turq_bank_accounting_accounts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_bank_accounting_accounts (
    id integer NOT NULL,
    banks_cards_id integer NOT NULL,
    accounting_accounts_id integer NOT NULL,
    bank_accounting_types_id integer NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_bank_accounting_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_bank_accounting_types (
    id integer NOT NULL,
    type_name character varying(100) NOT NULL,
    definition character varying(100) NOT NULL
);


--
-- Name: turq_bank_cards_secondary_accounts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_bank_cards_secondary_accounts (
    id integer NOT NULL,
    bank_cards_id integer NOT NULL,
    bank_secondary_accounts_id integer NOT NULL,
    accounting_accounts_id integer NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL,
    bank_definition character varying(250) NOT NULL
);


--
-- Name: turq_bank_secondary_accounts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_bank_secondary_accounts (
    id integer NOT NULL,
    account_name character varying(50) NOT NULL,
    account_code character varying(5) NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_banks_cards; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_banks_cards (
    id integer NOT NULL,
    bank_name character varying(50) NOT NULL,
    bank_branch_name character varying(50) NOT NULL,
    bank_account_no character varying(50) NOT NULL,
    currencies_id integer NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL,
    bank_definition character varying(250) NOT NULL,
    bank_code character varying(100) NOT NULL
);


--
-- Name: turq_banks_transaction_bills; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_banks_transaction_bills (
    id integer NOT NULL,
    transaction_bill_date date NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL,
    engine_sequences_id integer NOT NULL,
    transaction_bill_definition character varying(250) NOT NULL,
    transaction_bill_no character varying(100) NOT NULL,
    banks_transaction_types_id integer NOT NULL
);


--
-- Name: turq_banks_transaction_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_banks_transaction_types (
    id integer NOT NULL,
    transaction_type_name character varying(50) NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_banks_transactions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_banks_transactions (
    id integer NOT NULL,
    bank_transactions_bills_id integer NOT NULL,
    dept_amount numeric NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL,
    credit_amount numeric NOT NULL,
    banks_cards_id integer NOT NULL,
    dept_amount_in_foreign_currency numeric NOT NULL,
    credit_amount_in_foreign_currency numeric NOT NULL,
    exchange_rate_id integer NOT NULL
);


--
-- Name: turq_bill_groups; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_bill_groups (
    id integer NOT NULL,
    groups_name character varying(50) NOT NULL,
    group_description character varying(250) NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_bill_in_engine_sequences; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_bill_in_engine_sequences (
    engine_sequences_id integer NOT NULL,
    bills_id integer NOT NULL,
    id integer NOT NULL
);


--
-- Name: turq_bill_in_groups; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_bill_in_groups (
    id integer NOT NULL,
    bills_id integer NOT NULL,
    bill_groups_id integer NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_bills; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_bills (
    id integer NOT NULL,
    bills_type integer NOT NULL,
    bills_date date NOT NULL,
    bills_definition character varying(250) NOT NULL,
    bills_printed boolean NOT NULL,
    is_open boolean NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL,
    due_date date NOT NULL,
    bill_document_no character varying(50) NOT NULL,
    current_cards_id integer NOT NULL,
    exchange_rate_id integer NOT NULL,
    engine_sequences_id integer NOT NULL
);


--
-- Name: turq_cash_cards; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_cash_cards (
    id integer NOT NULL,
    cash_card_name character varying(250) NOT NULL,
    cash_card_definition character varying(250) NOT NULL,
    accounting_accounts_id integer NOT NULL,
    created_by character varying(100) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(100) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_cash_transaction_rows; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_cash_transaction_rows (
    id integer NOT NULL,
    accounting_accounts_id integer NOT NULL,
    dept_amount numeric NOT NULL,
    credit_amount numeric NOT NULL,
    cash_transactions_id integer NOT NULL,
    transaction_definition character varying(250),
    created_by character varying(100) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(100) NOT NULL,
    last_modified date NOT NULL,
    cash_cards_id integer NOT NULL,
    dept_amount_in_foreign_currency numeric NOT NULL,
    credit_amount_in_foreign_currency numeric NOT NULL,
    exchange_rate_id integer NOT NULL
);


--
-- Name: turq_cash_transaction_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_cash_transaction_types (
    id integer NOT NULL,
    cash_transation_type_name character varying(100) NOT NULL,
    created_by character varying(100) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(100) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_cash_transactions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_cash_transactions (
    id integer NOT NULL,
    cash_transactions_types_id integer NOT NULL,
    engine_sequences_id integer NOT NULL,
    transaction_date date NOT NULL,
    transaction_definition character varying(250),
    document_no character varying(100),
    created_by character varying(100) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(100) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_cheque_cheque_in_rolls; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_cheque_cheque_in_rolls (
    id integer NOT NULL,
    cheque_rolls_id integer NOT NULL,
    cheque_cheques_id integer NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_cheque_cheques; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_cheque_cheques (
    id integer NOT NULL,
    cheques_portfolio_no character varying(30) NOT NULL,
    cheques_no character varying(50) NOT NULL,
    banks_id integer NOT NULL,
    cheques_due_date date NOT NULL,
    cheques_debtor character varying(100) NOT NULL,
    cheques_payment_place character varying(50),
    cheques_value_date date NOT NULL,
    cheques_amount numeric NOT NULL,
    currencies_id integer NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL,
    bank_name character varying(100) NOT NULL,
    bank_branch_name character varying(100) NOT NULL,
    cheques_type integer NOT NULL,
    bank_account_no character varying(100),
    cheques_amount_in_foreign_currency numeric NOT NULL,
    exchange_rate_id integer NOT NULL
);


--
-- Name: turq_cheque_roll_accounting_accounts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_cheque_roll_accounting_accounts (
    id integer NOT NULL,
    accounting_accounts_id integer NOT NULL
);


--
-- Name: turq_cheque_rolls; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_cheque_rolls (
    id integer NOT NULL,
    cheque_transaction_types_id integer NOT NULL,
    cheque_rolls_date date NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL,
    engine_sequences_id integer NOT NULL,
    cheque_roll_no character varying(50) NOT NULL,
    current_cards_id integer NOT NULL,
    banks_cards_id integer NOT NULL,
    sum_cheque_amounts boolean NOT NULL
);


--
-- Name: turq_cheque_transaction_type_groups; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_cheque_transaction_type_groups (
    id integer NOT NULL,
    group_name character varying(100),
    definition character varying(250)
);


--
-- Name: turq_cheque_transaction_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_cheque_transaction_types (
    id integer NOT NULL,
    transaction_typs_name character varying(50) NOT NULL,
    transaction_types_parent smallint NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_companies; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_companies (
    id integer NOT NULL,
    company_name character varying(250) NOT NULL,
    company_address character varying(250) NOT NULL,
    company_telephone character varying(100) NOT NULL,
    company_fax character varying(100) NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    update_date date NOT NULL
);


--
-- Name: turq_consignment_groups; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_consignment_groups (
    id integer NOT NULL,
    groups_name character varying(50) NOT NULL,
    groups_description character varying(250) NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_consignments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_consignments (
    id integer NOT NULL,
    consignments_date date NOT NULL,
    consignments_definition character varying(250) NOT NULL,
    consignments_type integer NOT NULL,
    consignments_printed boolean NOT NULL,
    engine_sequences_id integer NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL,
    current_cards_id integer NOT NULL,
    consignment_document_no character varying(50) NOT NULL,
    exchange_rate_id integer NOT NULL,
    bill_document_no character varying(50) NOT NULL
);


--
-- Name: turq_consignments_in_group; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_consignments_in_group (
    id integer NOT NULL,
    consignment_id integer NOT NULL,
    consignments_groups_id integer NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_currencies; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_currencies (
    id integer NOT NULL,
    currencies_name character varying(30) NOT NULL,
    currencies_abbreviation character varying(5) NOT NULL,
    currencies_country character varying(50) NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL,
    default_currency boolean NOT NULL,
    constant boolean
);


--
-- Name: turq_currency_exchange_rates; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_currency_exchange_rates (
    id integer NOT NULL,
    exhange_rates_date date NOT NULL,
    base_currency_id integer NOT NULL,
    exchange_currency_id integer NOT NULL,
    exchange_ratio numeric NOT NULL
);


--
-- Name: turq_current_accounting_accounts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_current_accounting_accounts (
    id integer NOT NULL,
    current_cards_id integer NOT NULL,
    accounting_accounts_id integer NOT NULL,
    current_accounting_types_id integer NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_current_accounting_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_current_accounting_types (
    id integer NOT NULL,
    type_name character varying(100) NOT NULL,
    definition character varying(100) NOT NULL
);


--
-- Name: turq_current_cards; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_current_cards (
    id integer NOT NULL,
    cards_current_code character varying(25) NOT NULL,
    cards_name character varying(250) NOT NULL,
    cards_definition character varying(250) NOT NULL,
    cards_address character varying(250) NOT NULL,
    cards_discount_rate numeric NOT NULL,
    cards_discount_payment numeric NOT NULL,
    cards_credit_limit numeric NOT NULL,
    cards_risk_limit numeric NOT NULL,
    cards_tax_department character varying(50) NOT NULL,
    cards_tax_number character varying(50) NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL,
    days_to_value integer
);


--
-- Name: turq_current_cards_groups; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_current_cards_groups (
    id integer NOT NULL,
    current_cards_id integer NOT NULL,
    current_groups_id integer NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_current_cards_phones; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_current_cards_phones (
    id integer NOT NULL,
    current_cards_id integer NOT NULL,
    phones_country_code integer NOT NULL,
    phones_city_code integer NOT NULL,
    phones_number integer NOT NULL,
    phones_type character varying(50) NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date,
    updated_by character varying NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_current_contacts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_current_contacts (
    id integer NOT NULL,
    current_cards_id integer NOT NULL,
    contacts_name character varying(100) NOT NULL,
    contact_address character varying(250) NOT NULL,
    contacts_phone1 character varying(30) NOT NULL,
    contacts_phone2 character varying(30) NOT NULL,
    contacts_fax_number character varying(30) NOT NULL,
    contacts_email character varying(100) NOT NULL,
    contacts_web_site character varying(250),
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_current_groups; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_current_groups (
    id integer NOT NULL,
    groups_name character varying(50) NOT NULL,
    groups_description character varying(250) NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_current_transaction_bill; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_current_transaction_bill (
    id integer NOT NULL,
    current_transactions_id_close integer NOT NULL,
    current_transactions_id_open integer NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_current_transaction_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_current_transaction_types (
    id integer NOT NULL,
    transaction_type_name character varying(50) NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_current_transactions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_current_transactions (
    id integer NOT NULL,
    current_cards_id integer NOT NULL,
    transactions_date date NOT NULL,
    transactions_document_no character varying NOT NULL,
    current_transaction_types_id integer NOT NULL,
    transactions_total_credit numeric NOT NULL,
    transactions_total_discount numeric NOT NULL,
    transactions_total_dept numeric NOT NULL,
    engine_sequences_id integer NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL,
    transactions_definition character varying(250) NOT NULL,
    total_credit_in_foreign_currency numeric NOT NULL,
    total_dept_in_foreign_currency numeric NOT NULL,
    total_discount_in_foreign_currency numeric NOT NULL,
    exchange_rate_id integer NOT NULL
);


--
-- Name: turq_engine_menu; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_engine_menu (
    id integer NOT NULL,
    menu_name character varying(100) NOT NULL,
    menu_image character varying(100),
    menu_type integer NOT NULL,
    menu_module_component integer NOT NULL,
    parent_id integer NOT NULL
);


--
-- Name: turq_engine_sequences; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_engine_sequences (
    id integer NOT NULL,
    modules_id integer NOT NULL
);


--
-- Name: turq_group_permissions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_group_permissions (
    id integer NOT NULL,
    groups_id integer NOT NULL,
    modules_id integer NOT NULL,
    module_components_id integer NOT NULL,
    group_permissions_level integer NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    update_date date NOT NULL
);


--
-- Name: turq_groups; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_groups (
    id integer NOT NULL,
    groups_name character varying(100) NOT NULL,
    groups_description character varying(250) NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    update_date date NOT NULL
);


--
-- Name: turq_inventory_accounting_accounts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_inventory_accounting_accounts (
    id integer NOT NULL,
    inventory_cards_id integer NOT NULL,
    accounting_accounts_id integer NOT NULL,
    inventory_accounting_types_id integer NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_inventory_accounting_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_inventory_accounting_types (
    id integer NOT NULL,
    type_name character varying(100) NOT NULL,
    definition character varying(100) NOT NULL
);


--
-- Name: turq_inventory_card_groups; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_inventory_card_groups (
    id integer NOT NULL,
    inventory_cards_id integer NOT NULL,
    inventory_groups_id integer NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_inventory_card_units; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_inventory_card_units (
    id integer NOT NULL,
    inventory_cards_id integer NOT NULL,
    card_units_factor numeric NOT NULL,
    inventory_units_id integer NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_inventory_cards; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_inventory_cards (
    id integer NOT NULL,
    card_inventory_code character varying(25) NOT NULL,
    card_name character varying(50) NOT NULL,
    card_definition character varying(50) NOT NULL,
    card_minimum_amount integer NOT NULL,
    card_maximum_amount integer NOT NULL,
    card_vat integer NOT NULL,
    card_discount integer NOT NULL,
    card_special_vat integer NOT NULL,
    card_special_vat_each numeric NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    update_date date NOT NULL,
    spec_vat_for_each boolean NOT NULL
);


--
-- Name: turq_inventory_customize_fields; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_inventory_customize_fields (
    id integer NOT NULL,
    customize_type_id integer NOT NULL,
    field_value character varying(50) NOT NULL,
    inventory_card_id integer NOT NULL
);


--
-- Name: turq_inventory_customize_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_inventory_customize_types (
    id integer NOT NULL,
    field_name character varying(50) NOT NULL
);


--
-- Name: turq_inventory_groups; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_inventory_groups (
    id integer NOT NULL,
    groups_name character varying(50) NOT NULL,
    groups_description character varying(250) NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL,
    parent_group integer NOT NULL
);


--
-- Name: turq_inventory_prices; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_inventory_prices (
    id integer NOT NULL,
    inventory_cards_id integer NOT NULL,
    prices_type boolean NOT NULL,
    currencies_id integer NOT NULL,
    prices_amount numeric NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_inventory_transaction_bills; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_inventory_transaction_bills (
    id integer NOT NULL,
    bill_date date NOT NULL,
    bill_definition character varying(250) NOT NULL,
    bill_type integer NOT NULL,
    engine_sequence integer NOT NULL,
    bill_document_no character varying(25) NOT NULL,
    created_by character varying(25) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(25) NOT NULL,
    last_modified date NOT NULL,
    warehouse_in integer NOT NULL,
    warehouse_out integer NOT NULL
);


--
-- Name: turq_inventory_transaction_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_inventory_transaction_types (
    id integer NOT NULL,
    type_name character varying(100) NOT NULL,
    created_by character varying(100) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(100) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_inventory_transactions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_inventory_transactions (
    id integer NOT NULL,
    inventory_cards_id integer NOT NULL,
    inventory_warehouses_id integer NOT NULL,
    engine_sequences_id integer NOT NULL,
    amount_in numeric NOT NULL,
    inventory_units_id integer NOT NULL,
    unit_price numeric NOT NULL,
    total_price numeric NOT NULL,
    discount_rate numeric NOT NULL,
    discount_amount numeric NOT NULL,
    vat_amount numeric NOT NULL,
    vat_special_unit_price numeric NOT NULL,
    vat_special_rate numeric NOT NULL,
    vat_special_amount numeric NOT NULL,
    cumilative_price numeric NOT NULL,
    amount_out numeric NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL,
    transactions_date date NOT NULL,
    transaction_type integer NOT NULL,
    document_no character varying(100) NOT NULL,
    definition character varying(250) NOT NULL,
    exchange_rate_id integer NOT NULL,
    vat_rate numeric NOT NULL,
    unit_price_in_foreign_currency numeric NOT NULL,
    total_price_in_foreign_currency numeric NOT NULL,
    discount_amount_in_foreign_currency numeric NOT NULL,
    vat_amount_in_foreign_currency numeric NOT NULL,
    vat_special_unit_price_in_foreign_currency numeric NOT NULL,
    vat_special_amount_in_foreign_currency numeric NOT NULL,
    cumilative_price_in_foreign_currency numeric NOT NULL,
    current_cards_id integer NOT NULL
);


--
-- Name: turq_inventory_units; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_inventory_units (
    id integer NOT NULL,
    units_name character varying(50) NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_inventory_warehouses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_inventory_warehouses (
    id integer NOT NULL,
    warehouses_name character varying(50) NOT NULL,
    warehouses_address character varying(250),
    warehouses_description character varying(250),
    warehouses_city character varying(25),
    warehouses_telephone character varying(25),
    warehouses_code character varying(25) NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_module_components; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_module_components (
    id integer NOT NULL,
    modules_id integer NOT NULL,
    components_name character varying(100) NOT NULL,
    components_description character varying(250) NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    update_date date NOT NULL
);


--
-- Name: turq_modules; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_modules (
    id integer NOT NULL,
    modules_name character varying(100) NOT NULL,
    module_description character varying(250) NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    update_date date NOT NULL
);


--
-- Name: turq_order_groups; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_order_groups (
    id integer NOT NULL,
    groups_name character varying(50) NOT NULL,
    groups_description character varying(250) NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_order_in_groups; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_order_in_groups (
    id integer NOT NULL,
    orders_id integer NOT NULL,
    order_groups_id integer NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_orders; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_orders (
    id integer NOT NULL,
    orders_document_no integer NOT NULL,
    bills_id integer NOT NULL,
    orders_date date NOT NULL,
    current_cards_id integer NOT NULL,
    orders_definition character varying(250) NOT NULL,
    orders_discount_rate integer NOT NULL,
    orders_vat integer NOT NULL,
    orders_discount_amount numeric NOT NULL,
    orders_charges numeric NOT NULL,
    orders_vat_amount numeric NOT NULL,
    orders_total_amount numeric NOT NULL,
    orders_due_date date NOT NULL,
    orders_deliver_date date NOT NULL,
    orders_delivered integer NOT NULL,
    orders_type integer NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_services; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_services (
    id integer DEFAULT nextval('service_seq'::text) NOT NULL,
    service_name character varying(250) NOT NULL,
    class_name character varying(250) NOT NULL,
    method_name character varying(250) NOT NULL
);


--
-- Name: turq_settings; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_settings (
    id integer NOT NULL,
    database_version character varying(50) NOT NULL
);


--
-- Name: turq_tradebill_rolls; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_tradebill_rolls (
    id integer NOT NULL,
    current_cards_id integer NOT NULL,
    banks_cards_id integer NOT NULL,
    tradebill_transaction_types_id integer NOT NULL,
    tradebill_rolls_date date NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_tradebill_tradebills; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_tradebill_tradebills (
    id integer NOT NULL,
    tradebills_portfolio_no character varying(50) NOT NULL,
    tradebill_due_date date NOT NULL,
    tradebill_debtor character varying(100) NOT NULL,
    tradebill_guarantor character varying(100) NOT NULL,
    tradebill_payment_place character varying(100) NOT NULL,
    tradebill_value_date integer NOT NULL,
    tradebill_amount numeric NOT NULL,
    currencies_id integer NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_tradebill_tradebills_rolls; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_tradebill_tradebills_rolls (
    id integer NOT NULL,
    tradebill_rolls_id integer NOT NULL,
    tradebill_tradebills_id integer NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_tradebill_transaction_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_tradebill_transaction_types (
    id integer NOT NULL,
    transaction_types_name character varying(50) NOT NULL,
    transaction_types_parent smallint NOT NULL,
    accounting_accounts_id integer NOT NULL,
    created_by date NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    last_modified date NOT NULL
);


--
-- Name: turq_user_group; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_user_group (
    id integer NOT NULL,
    groups_id integer NOT NULL,
    users_id integer NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    update_date date NOT NULL
);


--
-- Name: turq_user_permission_levels; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_user_permission_levels (
    id integer NOT NULL,
    permission_level integer NOT NULL,
    permission_name character varying(50) NOT NULL,
    permission_description character varying(50) NOT NULL
);


--
-- Name: turq_user_permissions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_user_permissions (
    id integer NOT NULL,
    users_id integer NOT NULL,
    modules_id integer NOT NULL,
    module_components_id integer NOT NULL,
    user_permissions_level integer NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    update_date date NOT NULL
);


--
-- Name: turq_users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE turq_users (
    id integer NOT NULL,
    username character varying(30) NOT NULL,
    users_password character varying(250) NOT NULL,
    users_real_name character varying(250) NOT NULL,
    users_description character varying(250) NOT NULL,
    created_by character varying(50) NOT NULL,
    creation_date date NOT NULL,
    updated_by character varying(50) NOT NULL,
    update_date date NOT NULL
);


--
-- Name: turq_view_acc_totals; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW turq_view_acc_totals AS
    SELECT account.id AS accounting_accounts_id, tabletranscolumns.totalcreditamount, tabletranscolumns.totaldeptamount FROM (turq_accounting_accounts account LEFT JOIN (SELECT transcolumns.accounting_accounts_id, sum(transcolumns.rows_credit_in_base_currency) AS totalcreditamount, sum(transcolumns.rows_dept_in_base_currency) AS totaldeptamount FROM turq_accounting_transaction_columns transcolumns GROUP BY transcolumns.accounting_accounts_id) tabletranscolumns ON ((tabletranscolumns.accounting_accounts_id = account.id))) ORDER BY account.id;


--
-- Name: turq_view_acc_trans_total_amount; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW turq_view_acc_trans_total_amount AS
    SELECT trans.id AS accounting_transactions_id, tabletranscolumns.totalcreditamount, tabletranscolumns.totaldeptamount FROM (turq_accounting_transactions trans LEFT JOIN (SELECT transcolumns.accounting_transactions_id, sum(transcolumns.rows_credit_in_base_currency) AS totalcreditamount, sum(transcolumns.rows_dept_in_base_currency) AS totaldeptamount FROM turq_accounting_transaction_columns transcolumns GROUP BY transcolumns.accounting_transactions_id) tabletranscolumns ON ((tabletranscolumns.accounting_transactions_id = trans.id)));


--
-- Name: turq_view_bill_trans_total; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW turq_view_bill_trans_total AS
    SELECT billin.bills_id, sum(invtrans.total_price_in_foreign_currency) AS totalprice, sum(invtrans.vat_amount_in_foreign_currency) AS vatamount, sum(invtrans.vat_special_amount_in_foreign_currency) AS specialvatamount, sum(invtrans.discount_amount_in_foreign_currency) AS discountamount FROM turq_inventory_transactions invtrans, turq_bill_in_engine_sequences billin WHERE (invtrans.engine_sequences_id = billin.engine_sequences_id) GROUP BY billin.bills_id;


--
-- Name: turq_view_cheque_status; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW turq_view_cheque_status AS
    SELECT cheq.id AS cheque_cheques_id, cheq.cheques_portfolio_no, cheq.cheques_no, cheq.banks_id, cheq.cheques_due_date, cheq.cheques_debtor, cheq.cheques_payment_place, cheq.cheques_value_date, cheq.cheques_amount, cheq.currencies_id, transtype.id AS cheque_transaction_types_id, transtype.transaction_typs_name, transtype.transaction_types_parent, rolls.id AS cheque_rolls_id, rolls.cheque_rolls_date, rolls.engine_sequences_id, rolls.cheque_roll_no, rolls.current_cards_id, rolls.banks_cards_id FROM turq_cheque_cheques cheq, turq_cheque_rolls rolls, turq_cheque_cheque_in_rolls cheqrolls, turq_cheque_transaction_types transtype WHERE ((((cheq.id = cheqrolls.cheque_cheques_id) AND (rolls.id = cheqrolls.cheque_rolls_id)) AND (transtype.id = rolls.cheque_transaction_types_id)) AND (rolls.id = (SELECT max(roll_inner.id) AS max FROM turq_cheque_rolls roll_inner, turq_cheque_cheque_in_rolls cheqroll_inner, turq_cheque_cheques cheq_inner WHERE (((cheqroll_inner.cheque_rolls_id = roll_inner.id) AND (cheqroll_inner.cheque_cheques_id = cheq_inner.id)) AND (cheqroll_inner.cheque_cheques_id = cheq.id)))));


--
-- Name: turq_view_current_amount_total; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW turq_view_current_amount_total AS
    SELECT cards.id AS current_cards_id, tabletrans.transactions_total_credit, tabletrans.transactions_total_dept, tabletrans.transactions_balance_now FROM (turq_current_cards cards LEFT JOIN (SELECT trans.current_cards_id, sum(trans.transactions_total_credit) AS transactions_total_credit, sum(trans.transactions_total_dept) AS transactions_total_dept, sum((trans.transactions_total_credit - trans.transactions_total_dept)) AS transactions_balance_now FROM turq_current_transactions trans GROUP BY trans.current_cards_id) tabletrans ON ((tabletrans.current_cards_id = cards.id)));


--
-- Name: turq_view_inv_price_totals; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW turq_view_inv_price_totals AS
    SELECT invtrans.engine_sequences_id, sum(invtrans.total_price_in_foreign_currency) AS totalprice, sum(invtrans.vat_amount_in_foreign_currency) AS vatamount, sum(invtrans.vat_special_amount_in_foreign_currency) AS specialvatamount, sum(invtrans.discount_amount_in_foreign_currency) AS discountamount FROM turq_inventory_transactions invtrans GROUP BY invtrans.engine_sequences_id;


--
-- Name: turq_view_inventory_amount_total; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW turq_view_inventory_amount_total AS
    SELECT cards.id AS inventory_cards_id, tabletrans.transactions_amount_in, tabletrans.transactions_total_amount_out, tabletrans.transactions_total_amount_now FROM (turq_inventory_cards cards LEFT JOIN (SELECT trans.inventory_cards_id, sum(trans.amount_in) AS transactions_amount_in, sum(trans.amount_out) AS transactions_total_amount_out, sum((trans.amount_in - trans.amount_out)) AS transactions_total_amount_now FROM turq_inventory_transactions trans GROUP BY trans.inventory_cards_id) tabletrans ON ((tabletrans.inventory_cards_id = cards.id)));


--
-- Name: turq_view_inventory_totals; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW turq_view_inventory_totals AS
    SELECT trans.id AS inventory_cards_id, transin.totalamountin AS total_amount_in, transin.totalpricein AS total_price_in, transout.totalamountout AS total_amount_out, transout.totalpriceout AS total_price_out, transoverin.totalamountin AS total_transover_amount_in, transoverin.totalpricein AS total_transover_price_in, transoverout.totalamountout AS total_transover_amount_out, transoverout.totalpriceout AS total_transover_price_out FROM ((((turq_inventory_cards trans LEFT JOIN (SELECT turq_inventory_transactions.inventory_cards_id, sum(turq_inventory_transactions.total_price) AS totalpricein, sum(turq_inventory_transactions.amount_in) AS totalamountin FROM turq_inventory_transactions WHERE ((turq_inventory_transactions.amount_in <> (0)::numeric) AND (turq_inventory_transactions.transaction_type = 1)) GROUP BY turq_inventory_transactions.inventory_cards_id) transin ON ((trans.id = transin.inventory_cards_id))) LEFT JOIN (SELECT turq_inventory_transactions.inventory_cards_id, sum(turq_inventory_transactions.total_price) AS totalpriceout, sum(turq_inventory_transactions.amount_out) AS totalamountout FROM turq_inventory_transactions WHERE ((turq_inventory_transactions.transaction_type = 1) AND (turq_inventory_transactions.amount_out <> (0)::numeric)) GROUP BY turq_inventory_transactions.inventory_cards_id) transout ON ((trans.id = transout.inventory_cards_id))) LEFT JOIN (SELECT turq_inventory_transactions.inventory_cards_id, sum(turq_inventory_transactions.total_price) AS totalpricein, sum(turq_inventory_transactions.amount_in) AS totalamountin FROM turq_inventory_transactions WHERE ((turq_inventory_transactions.amount_in <> (0)::numeric) AND (turq_inventory_transactions.transaction_type = 0)) GROUP BY turq_inventory_transactions.inventory_cards_id) transoverin ON ((trans.id = transoverin.inventory_cards_id))) LEFT JOIN (SELECT turq_inventory_transactions.inventory_cards_id, sum(turq_inventory_transactions.total_price) AS totalpriceout, sum(turq_inventory_transactions.amount_out) AS totalamountout FROM turq_inventory_transactions WHERE ((turq_inventory_transactions.transaction_type = 0) AND (turq_inventory_transactions.amount_out <> (0)::numeric)) GROUP BY turq_inventory_transactions.inventory_cards_id) transoverout ON ((trans.id = transoverout.inventory_cards_id)));


--
-- Name: cheque_transaction_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cheque_transaction_types
    ADD CONSTRAINT cheque_transaction_types_pkey PRIMARY KEY (id);


--
-- Name: id; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_transaction_types
    ADD CONSTRAINT id PRIMARY KEY (id);


--
-- Name: p_id; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_accounting_types
    ADD CONSTRAINT p_id PRIMARY KEY (id);


--
-- Name: p_iddd; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_bill_in_engine_sequences
    ADD CONSTRAINT p_iddd PRIMARY KEY (id);


--
-- Name: p_key_type_id; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_customize_types
    ADD CONSTRAINT p_key_type_id PRIMARY KEY (id);


--
-- Name: p_level_id; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_user_permission_levels
    ADD CONSTRAINT p_level_id PRIMARY KEY (id);


--
-- Name: prim_id; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_accounting_accounts
    ADD CONSTRAINT prim_id PRIMARY KEY (id);


--
-- Name: tradebill_transaction_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_tradebill_transaction_types
    ADD CONSTRAINT tradebill_transaction_types_pkey PRIMARY KEY (id);


--
-- Name: turq_accounting_account_classes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_accounting_account_classes
    ADD CONSTRAINT turq_accounting_account_classes_pkey PRIMARY KEY (id);


--
-- Name: turq_accounting_account_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_accounting_account_types
    ADD CONSTRAINT turq_accounting_account_types_pkey PRIMARY KEY (id);


--
-- Name: turq_accounting_accounts_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_accounting_accounts
    ADD CONSTRAINT turq_accounting_accounts_pkey PRIMARY KEY (id);


--
-- Name: turq_accounting_journal_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_accounting_journal
    ADD CONSTRAINT turq_accounting_journal_pkey PRIMARY KEY (id);


--
-- Name: turq_accounting_transaction_columns_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_accounting_transaction_columns
    ADD CONSTRAINT turq_accounting_transaction_columns_pkey PRIMARY KEY (id);


--
-- Name: turq_accounting_transaction_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_accounting_transaction_types
    ADD CONSTRAINT turq_accounting_transaction_types_pkey PRIMARY KEY (id);


--
-- Name: turq_accounting_transactions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_accounting_transactions
    ADD CONSTRAINT turq_accounting_transactions_pkey PRIMARY KEY (id);


--
-- Name: turq_bank_accounting_accounts_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_bank_accounting_accounts
    ADD CONSTRAINT turq_bank_accounting_accounts_pkey PRIMARY KEY (id);


--
-- Name: turq_bank_accounting_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_bank_accounting_types
    ADD CONSTRAINT turq_bank_accounting_types_pkey PRIMARY KEY (id);


--
-- Name: turq_bank_cards_secondary_accounts_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_bank_cards_secondary_accounts
    ADD CONSTRAINT turq_bank_cards_secondary_accounts_pkey PRIMARY KEY (id);


--
-- Name: turq_bank_secondary_accounts_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_bank_secondary_accounts
    ADD CONSTRAINT turq_bank_secondary_accounts_pkey PRIMARY KEY (id);


--
-- Name: turq_banks_cards_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_banks_cards
    ADD CONSTRAINT turq_banks_cards_pkey PRIMARY KEY (id);


--
-- Name: turq_banks_transaction_bills_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_banks_transaction_bills
    ADD CONSTRAINT turq_banks_transaction_bills_pkey PRIMARY KEY (id);


--
-- Name: turq_banks_transaction_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_banks_transaction_types
    ADD CONSTRAINT turq_banks_transaction_types_pkey PRIMARY KEY (id);


--
-- Name: turq_banks_transactions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_banks_transactions
    ADD CONSTRAINT turq_banks_transactions_pkey PRIMARY KEY (id);


--
-- Name: turq_bill_groups_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_bill_groups
    ADD CONSTRAINT turq_bill_groups_pkey PRIMARY KEY (id);


--
-- Name: turq_bill_in_groups_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_bill_in_groups
    ADD CONSTRAINT turq_bill_in_groups_pkey PRIMARY KEY (id);


--
-- Name: turq_bills_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_bills
    ADD CONSTRAINT turq_bills_pkey PRIMARY KEY (id);


--
-- Name: turq_cash_cards_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cash_cards
    ADD CONSTRAINT turq_cash_cards_pkey PRIMARY KEY (id);


--
-- Name: turq_cash_transaction_rows_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cash_transaction_rows
    ADD CONSTRAINT turq_cash_transaction_rows_pkey PRIMARY KEY (id);


--
-- Name: turq_cash_transaction_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cash_transaction_types
    ADD CONSTRAINT turq_cash_transaction_types_pkey PRIMARY KEY (id);


--
-- Name: turq_cash_transactions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cash_transactions
    ADD CONSTRAINT turq_cash_transactions_pkey PRIMARY KEY (id);


--
-- Name: turq_cheque_cheques_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cheque_cheques
    ADD CONSTRAINT turq_cheque_cheques_pkey PRIMARY KEY (id);


--
-- Name: turq_cheque_cheques_rolls_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cheque_cheque_in_rolls
    ADD CONSTRAINT turq_cheque_cheques_rolls_pkey PRIMARY KEY (id);


--
-- Name: turq_cheque_roll_accounting_accounts_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cheque_roll_accounting_accounts
    ADD CONSTRAINT turq_cheque_roll_accounting_accounts_pkey PRIMARY KEY (id);


--
-- Name: turq_cheque_rolls_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cheque_rolls
    ADD CONSTRAINT turq_cheque_rolls_pkey PRIMARY KEY (id);


--
-- Name: turq_cheque_transaction_type_groups_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cheque_transaction_type_groups
    ADD CONSTRAINT turq_cheque_transaction_type_groups_pkey PRIMARY KEY (id);


--
-- Name: turq_companies_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_companies
    ADD CONSTRAINT turq_companies_pkey PRIMARY KEY (id);


--
-- Name: turq_consignment_groups_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_consignment_groups
    ADD CONSTRAINT turq_consignment_groups_pkey PRIMARY KEY (id);


--
-- Name: turq_consignments_in_group_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_consignments_in_group
    ADD CONSTRAINT turq_consignments_in_group_pkey PRIMARY KEY (id);


--
-- Name: turq_consignments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_consignments
    ADD CONSTRAINT turq_consignments_pkey PRIMARY KEY (id);


--
-- Name: turq_currencies_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_currencies
    ADD CONSTRAINT turq_currencies_pkey PRIMARY KEY (id);


--
-- Name: turq_currency_exchange_rates_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_currency_exchange_rates
    ADD CONSTRAINT turq_currency_exchange_rates_pkey PRIMARY KEY (id);


--
-- Name: turq_current_accounting_accounts_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_current_accounting_accounts
    ADD CONSTRAINT turq_current_accounting_accounts_pkey PRIMARY KEY (id);


--
-- Name: turq_current_accounting_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_current_accounting_types
    ADD CONSTRAINT turq_current_accounting_types_pkey PRIMARY KEY (id);


--
-- Name: turq_current_cards_cards_current_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_current_cards
    ADD CONSTRAINT turq_current_cards_cards_current_code_key UNIQUE (cards_current_code);


--
-- Name: turq_current_cards_groups_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_current_cards_groups
    ADD CONSTRAINT turq_current_cards_groups_pkey PRIMARY KEY (id);


--
-- Name: turq_current_cards_phones_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_current_cards_phones
    ADD CONSTRAINT turq_current_cards_phones_pkey PRIMARY KEY (id);


--
-- Name: turq_current_cards_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_current_cards
    ADD CONSTRAINT turq_current_cards_pkey PRIMARY KEY (id);


--
-- Name: turq_current_contacts_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_current_contacts
    ADD CONSTRAINT turq_current_contacts_pkey PRIMARY KEY (id);


--
-- Name: turq_current_groups_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_current_groups
    ADD CONSTRAINT turq_current_groups_pkey PRIMARY KEY (id);


--
-- Name: turq_current_transaction_bill_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_current_transaction_bill
    ADD CONSTRAINT turq_current_transaction_bill_pkey PRIMARY KEY (id);


--
-- Name: turq_current_transaction_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_current_transaction_types
    ADD CONSTRAINT turq_current_transaction_types_pkey PRIMARY KEY (id);


--
-- Name: turq_current_transactions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_current_transactions
    ADD CONSTRAINT turq_current_transactions_pkey PRIMARY KEY (id);


--
-- Name: turq_engine_menu_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_engine_menu
    ADD CONSTRAINT turq_engine_menu_pkey PRIMARY KEY (id);


--
-- Name: turq_engine_sequences_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_engine_sequences
    ADD CONSTRAINT turq_engine_sequences_pkey PRIMARY KEY (id);


--
-- Name: turq_group_permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_group_permissions
    ADD CONSTRAINT turq_group_permissions_pkey PRIMARY KEY (id);


--
-- Name: turq_groups_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_groups
    ADD CONSTRAINT turq_groups_pkey PRIMARY KEY (id);


--
-- Name: turq_inventory_card_groups_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_card_groups
    ADD CONSTRAINT turq_inventory_card_groups_pkey PRIMARY KEY (id);


--
-- Name: turq_inventory_card_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_cards
    ADD CONSTRAINT turq_inventory_card_pkey PRIMARY KEY (id);


--
-- Name: turq_inventory_card_units_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_card_units
    ADD CONSTRAINT turq_inventory_card_units_pkey PRIMARY KEY (id);


--
-- Name: turq_inventory_cards_card_inventory_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_cards
    ADD CONSTRAINT turq_inventory_cards_card_inventory_code_key UNIQUE (card_inventory_code);


--
-- Name: turq_inventory_groups_groups_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_groups
    ADD CONSTRAINT turq_inventory_groups_groups_name_key UNIQUE (groups_name);


--
-- Name: turq_inventory_groups_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_groups
    ADD CONSTRAINT turq_inventory_groups_pkey PRIMARY KEY (id);


--
-- Name: turq_inventory_prices_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_prices
    ADD CONSTRAINT turq_inventory_prices_pkey PRIMARY KEY (id);


--
-- Name: turq_inventory_transaction_bills_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_transaction_bills
    ADD CONSTRAINT turq_inventory_transaction_bills_pkey PRIMARY KEY (id);


--
-- Name: turq_inventory_transactions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_transactions
    ADD CONSTRAINT turq_inventory_transactions_pkey PRIMARY KEY (id);


--
-- Name: turq_inventory_units_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_units
    ADD CONSTRAINT turq_inventory_units_pkey PRIMARY KEY (id);


--
-- Name: turq_inventory_units_units_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_units
    ADD CONSTRAINT turq_inventory_units_units_name_key UNIQUE (units_name);


--
-- Name: turq_inventory_warehouses_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_warehouses
    ADD CONSTRAINT turq_inventory_warehouses_pkey PRIMARY KEY (id);


--
-- Name: turq_module_components_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_module_components
    ADD CONSTRAINT turq_module_components_pkey PRIMARY KEY (id);


--
-- Name: turq_modules_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_modules
    ADD CONSTRAINT turq_modules_pkey PRIMARY KEY (id);


--
-- Name: turq_order_groups_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_order_groups
    ADD CONSTRAINT turq_order_groups_pkey PRIMARY KEY (id);


--
-- Name: turq_order_in_groups_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_order_in_groups
    ADD CONSTRAINT turq_order_in_groups_pkey PRIMARY KEY (id);


--
-- Name: turq_orders_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_orders
    ADD CONSTRAINT turq_orders_pkey PRIMARY KEY (id);


--
-- Name: turq_services_class_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_services
    ADD CONSTRAINT turq_services_class_name_key UNIQUE (class_name, method_name);


--
-- Name: turq_services_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_services
    ADD CONSTRAINT turq_services_pkey PRIMARY KEY (id);


--
-- Name: turq_services_service_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_services
    ADD CONSTRAINT turq_services_service_name_key UNIQUE (service_name);


--
-- Name: turq_settings_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_settings
    ADD CONSTRAINT turq_settings_pkey PRIMARY KEY (id);


--
-- Name: turq_tradebill_rolls_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_tradebill_rolls
    ADD CONSTRAINT turq_tradebill_rolls_pkey PRIMARY KEY (id);


--
-- Name: turq_tradebill_tradebills_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_tradebill_tradebills
    ADD CONSTRAINT turq_tradebill_tradebills_pkey PRIMARY KEY (id);


--
-- Name: turq_tradebill_tradebills_rolls_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_tradebill_tradebills_rolls
    ADD CONSTRAINT turq_tradebill_tradebills_rolls_pkey PRIMARY KEY (id);


--
-- Name: turq_user_group_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_user_group
    ADD CONSTRAINT turq_user_group_pkey PRIMARY KEY (id);


--
-- Name: turq_user_permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_user_permissions
    ADD CONSTRAINT turq_user_permissions_pkey PRIMARY KEY (id);


--
-- Name: turq_users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_users
    ADD CONSTRAINT turq_users_pkey PRIMARY KEY (id);


--
-- Name: u_name; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_users
    ADD CONSTRAINT u_name UNIQUE (username);


--
-- Name: uniq; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_accounting_accounts
    ADD CONSTRAINT uniq UNIQUE (inventory_cards_id, accounting_accounts_id, inventory_accounting_types_id);


--
-- Name: uniq_cur_abbr; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_currencies
    ADD CONSTRAINT uniq_cur_abbr UNIQUE (currencies_abbreviation);


--
-- Name: uniq_value; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_currency_exchange_rates
    ADD CONSTRAINT uniq_value UNIQUE (exhange_rates_date, base_currency_id, exchange_currency_id);


--
-- Name: unique_customize_fields; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_customize_fields
    ADD CONSTRAINT unique_customize_fields UNIQUE (customize_type_id, inventory_card_id);


--
-- Name: unique_customize_type_name; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_customize_types
    ADD CONSTRAINT unique_customize_type_name UNIQUE (field_name);


--
-- Name: index_accounting_trans_date; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_accounting_trans_date ON turq_accounting_transactions USING btree (transactions_date);


--
-- Name: index_accounting_trans_doc_no; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_accounting_trans_doc_no ON turq_accounting_transactions USING btree (transaction_document_no);


--
-- Name: index_bank_account_no; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_bank_account_no ON turq_banks_cards USING btree (bank_account_no);


--
-- Name: index_bank_branch_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_bank_branch_name ON turq_banks_cards USING btree (bank_branch_name);


--
-- Name: index_bank_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_bank_name ON turq_banks_cards USING btree (bank_name);


--
-- Name: index_bank_trans_bill_no; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_bank_trans_bill_no ON turq_banks_transaction_bills USING btree (transaction_bill_no);


--
-- Name: index_bank_trans_date; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_bank_trans_date ON turq_banks_transaction_bills USING btree (transaction_bill_date);


--
-- Name: index_bill_bill_date; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_bill_bill_date ON turq_bills USING btree (bills_date);


--
-- Name: index_bill_doc_no; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_bill_doc_no ON turq_bills USING btree (bill_document_no);


--
-- Name: index_bill_due_date; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_bill_due_date ON turq_bills USING btree (due_date);


--
-- Name: index_cash_card_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_cash_card_name ON turq_cash_cards USING btree (cash_card_name);


--
-- Name: index_cash_trans_date; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_cash_trans_date ON turq_cash_transactions USING btree (transaction_date);


--
-- Name: index_cheque_roll_date; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_cheque_roll_date ON turq_cheque_rolls USING btree (cheque_rolls_date);


--
-- Name: index_cheque_roll_no; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_cheque_roll_no ON turq_cheque_rolls USING btree (cheque_roll_no);


--
-- Name: index_cons_date; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_cons_date ON turq_consignments USING btree (consignments_date);


--
-- Name: index_cur_card_code; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_cur_card_code ON turq_current_cards USING btree (cards_current_code);


--
-- Name: index_cur_card_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_cur_card_name ON turq_current_cards USING btree (cards_name);


--
-- Name: index_cur_trans_definition; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_cur_trans_definition ON turq_current_transactions USING btree (transactions_definition);


--
-- Name: index_inv_card_code; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_inv_card_code ON turq_inventory_cards USING btree (card_inventory_code);


--
-- Name: index_inv_card_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_inv_card_name ON turq_inventory_cards USING btree (card_name);


--
-- Name: index_inv_warehouse_city; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_inv_warehouse_city ON turq_inventory_warehouses USING btree (warehouses_city);


--
-- Name: index_inv_warehouse_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_inv_warehouse_name ON turq_inventory_warehouses USING btree (warehouses_name);


--
-- Name: $1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_module_components
    ADD CONSTRAINT "$1" FOREIGN KEY (modules_id) REFERENCES turq_modules(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $100; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_transactions
    ADD CONSTRAINT "$100" FOREIGN KEY (engine_sequences_id) REFERENCES turq_engine_sequences(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $101; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_consignments
    ADD CONSTRAINT "$101" FOREIGN KEY (engine_sequences_id) REFERENCES turq_engine_sequences(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $106; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cash_cards
    ADD CONSTRAINT "$106" FOREIGN KEY (accounting_accounts_id) REFERENCES turq_accounting_accounts(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $11; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_prices
    ADD CONSTRAINT "$11" FOREIGN KEY (inventory_cards_id) REFERENCES turq_inventory_cards(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $12; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_card_units
    ADD CONSTRAINT "$12" FOREIGN KEY (inventory_cards_id) REFERENCES turq_inventory_cards(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $13; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_card_units
    ADD CONSTRAINT "$13" FOREIGN KEY (inventory_units_id) REFERENCES turq_inventory_units(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $15; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_transactions
    ADD CONSTRAINT "$15" FOREIGN KEY (inventory_warehouses_id) REFERENCES turq_inventory_warehouses(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $16; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_transactions
    ADD CONSTRAINT "$16" FOREIGN KEY (inventory_cards_id) REFERENCES turq_inventory_cards(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $17; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_transactions
    ADD CONSTRAINT "$17" FOREIGN KEY (inventory_units_id) REFERENCES turq_inventory_units(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $18; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_user_group
    ADD CONSTRAINT "$18" FOREIGN KEY (groups_id) REFERENCES turq_groups(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $19; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_user_group
    ADD CONSTRAINT "$19" FOREIGN KEY (users_id) REFERENCES turq_users(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_user_permissions
    ADD CONSTRAINT "$2" FOREIGN KEY (users_id) REFERENCES turq_users(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $21; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_prices
    ADD CONSTRAINT "$21" FOREIGN KEY (currencies_id) REFERENCES turq_currencies(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $23; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_accounting_accounts
    ADD CONSTRAINT "$23" FOREIGN KEY (parent_account) REFERENCES turq_accounting_accounts(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $24; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_accounting_transactions
    ADD CONSTRAINT "$24" FOREIGN KEY (accounting_transaction_types_id) REFERENCES turq_accounting_transaction_types(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $25; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_accounting_transactions
    ADD CONSTRAINT "$25" FOREIGN KEY (accounting_journal_id) REFERENCES turq_accounting_journal(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $26; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_accounting_transactions
    ADD CONSTRAINT "$26" FOREIGN KEY (module_id) REFERENCES turq_modules(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $29; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_current_cards_phones
    ADD CONSTRAINT "$29" FOREIGN KEY (current_cards_id) REFERENCES turq_current_cards(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_user_permissions
    ADD CONSTRAINT "$3" FOREIGN KEY (modules_id) REFERENCES turq_modules(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $32; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_current_cards_groups
    ADD CONSTRAINT "$32" FOREIGN KEY (current_cards_id) REFERENCES turq_current_cards(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $33; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_current_cards_groups
    ADD CONSTRAINT "$33" FOREIGN KEY (current_groups_id) REFERENCES turq_current_groups(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $34; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_current_contacts
    ADD CONSTRAINT "$34" FOREIGN KEY (current_cards_id) REFERENCES turq_current_cards(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $35; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_current_transactions
    ADD CONSTRAINT "$35" FOREIGN KEY (current_cards_id) REFERENCES turq_current_cards(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $37; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_current_transaction_bill
    ADD CONSTRAINT "$37" FOREIGN KEY (current_transactions_id_close) REFERENCES turq_current_transactions(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $38; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_current_transaction_bill
    ADD CONSTRAINT "$38" FOREIGN KEY (current_transactions_id_open) REFERENCES turq_current_transactions(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $4; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_user_permissions
    ADD CONSTRAINT "$4" FOREIGN KEY (module_components_id) REFERENCES turq_module_components(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $41; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_bank_cards_secondary_accounts
    ADD CONSTRAINT "$41" FOREIGN KEY (bank_cards_id) REFERENCES turq_banks_cards(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $42; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_bank_cards_secondary_accounts
    ADD CONSTRAINT "$42" FOREIGN KEY (bank_secondary_accounts_id) REFERENCES turq_bank_secondary_accounts(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $43; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_bank_cards_secondary_accounts
    ADD CONSTRAINT "$43" FOREIGN KEY (accounting_accounts_id) REFERENCES turq_accounting_accounts(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $45; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_banks_transactions
    ADD CONSTRAINT "$45" FOREIGN KEY (bank_transactions_bills_id) REFERENCES turq_banks_transaction_bills(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_group_permissions
    ADD CONSTRAINT "$5" FOREIGN KEY (modules_id) REFERENCES turq_modules(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $51; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cheque_cheques
    ADD CONSTRAINT "$51" FOREIGN KEY (banks_id) REFERENCES turq_banks_cards(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $52; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cheque_cheques
    ADD CONSTRAINT "$52" FOREIGN KEY (currencies_id) REFERENCES turq_currencies(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $58; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cheque_cheque_in_rolls
    ADD CONSTRAINT "$58" FOREIGN KEY (cheque_rolls_id) REFERENCES turq_cheque_rolls(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $59; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cheque_cheque_in_rolls
    ADD CONSTRAINT "$59" FOREIGN KEY (cheque_cheques_id) REFERENCES turq_cheque_cheques(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_group_permissions
    ADD CONSTRAINT "$6" FOREIGN KEY (module_components_id) REFERENCES turq_module_components(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $61; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_tradebill_tradebills
    ADD CONSTRAINT "$61" FOREIGN KEY (currencies_id) REFERENCES turq_currencies(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $63; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_tradebill_rolls
    ADD CONSTRAINT "$63" FOREIGN KEY (current_cards_id) REFERENCES turq_current_cards(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $64; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_tradebill_rolls
    ADD CONSTRAINT "$64" FOREIGN KEY (banks_cards_id) REFERENCES turq_banks_cards(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $65; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_tradebill_transaction_types
    ADD CONSTRAINT "$65" FOREIGN KEY (accounting_accounts_id) REFERENCES turq_accounting_accounts(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $66; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_tradebill_rolls
    ADD CONSTRAINT "$66" FOREIGN KEY (tradebill_transaction_types_id) REFERENCES turq_tradebill_transaction_types(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $67; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_tradebill_tradebills_rolls
    ADD CONSTRAINT "$67" FOREIGN KEY (tradebill_rolls_id) REFERENCES turq_tradebill_rolls(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $68; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_tradebill_tradebills_rolls
    ADD CONSTRAINT "$68" FOREIGN KEY (tradebill_tradebills_id) REFERENCES turq_tradebill_tradebills(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $7; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_group_permissions
    ADD CONSTRAINT "$7" FOREIGN KEY (groups_id) REFERENCES turq_groups(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $71; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_bill_in_groups
    ADD CONSTRAINT "$71" FOREIGN KEY (bill_groups_id) REFERENCES turq_bill_groups(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $72; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_bill_in_groups
    ADD CONSTRAINT "$72" FOREIGN KEY (bills_id) REFERENCES turq_bills(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $74; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_orders
    ADD CONSTRAINT "$74" FOREIGN KEY (bills_id) REFERENCES turq_bills(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $76; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_orders
    ADD CONSTRAINT "$76" FOREIGN KEY (current_cards_id) REFERENCES turq_current_cards(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $78; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_order_in_groups
    ADD CONSTRAINT "$78" FOREIGN KEY (orders_id) REFERENCES turq_orders(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $79; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_order_in_groups
    ADD CONSTRAINT "$79" FOREIGN KEY (order_groups_id) REFERENCES turq_order_groups(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $83; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_card_groups
    ADD CONSTRAINT "$83" FOREIGN KEY (inventory_cards_id) REFERENCES turq_inventory_cards(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $84; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_card_groups
    ADD CONSTRAINT "$84" FOREIGN KEY (inventory_groups_id) REFERENCES turq_inventory_groups(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $85; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_banks_cards
    ADD CONSTRAINT "$85" FOREIGN KEY (currencies_id) REFERENCES turq_currencies(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $86; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_accounting_transaction_columns
    ADD CONSTRAINT "$86" FOREIGN KEY (accounting_transactions_id) REFERENCES turq_accounting_transactions(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $87; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_accounting_transaction_columns
    ADD CONSTRAINT "$87" FOREIGN KEY (accounting_accounts_id) REFERENCES turq_accounting_accounts(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $88; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_current_transactions
    ADD CONSTRAINT "$88" FOREIGN KEY (current_transaction_types_id) REFERENCES turq_current_transaction_types(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $89; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_accounting_accounts
    ADD CONSTRAINT "$89" FOREIGN KEY (top_account) REFERENCES turq_accounting_accounts(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $90; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_consignments_in_group
    ADD CONSTRAINT "$90" FOREIGN KEY (consignments_groups_id) REFERENCES turq_consignment_groups(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $92; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_consignments_in_group
    ADD CONSTRAINT "$92" FOREIGN KEY (consignment_id) REFERENCES turq_consignments(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $94; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_current_transactions
    ADD CONSTRAINT "$94" FOREIGN KEY (engine_sequences_id) REFERENCES turq_engine_sequences(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $95; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_accounting_transactions
    ADD CONSTRAINT "$95" FOREIGN KEY (engine_sequences_id) REFERENCES turq_engine_sequences(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: $96; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_engine_sequences
    ADD CONSTRAINT "$96" FOREIGN KEY (modules_id) REFERENCES turq_modules(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: 112; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cash_transactions
    ADD CONSTRAINT "112" FOREIGN KEY (cash_transactions_types_id) REFERENCES turq_cash_transaction_types(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: 113; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cash_transactions
    ADD CONSTRAINT "113" FOREIGN KEY (engine_sequences_id) REFERENCES turq_engine_sequences(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: 120; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cash_transaction_rows
    ADD CONSTRAINT "120" FOREIGN KEY (accounting_accounts_id) REFERENCES turq_accounting_accounts(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: 121; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cash_transaction_rows
    ADD CONSTRAINT "121" FOREIGN KEY (cash_transactions_id) REFERENCES turq_cash_transactions(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: 55; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cheque_rolls
    ADD CONSTRAINT "55" FOREIGN KEY (engine_sequences_id) REFERENCES turq_engine_sequences(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: ac1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_accounting_accounts
    ADD CONSTRAINT ac1 FOREIGN KEY (accounting_class_id) REFERENCES turq_accounting_account_classes(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: at1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_accounting_accounts
    ADD CONSTRAINT at1 FOREIGN KEY (accounting_types_id) REFERENCES turq_accounting_account_types(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: bankAcc1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_bank_accounting_accounts
    ADD CONSTRAINT "bankAcc1" FOREIGN KEY (accounting_accounts_id) REFERENCES turq_accounting_accounts(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: bankAcc2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_bank_accounting_accounts
    ADD CONSTRAINT "bankAcc2" FOREIGN KEY (bank_accounting_types_id) REFERENCES turq_bank_accounting_types(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: bankAcc3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_bank_accounting_accounts
    ADD CONSTRAINT "bankAcc3" FOREIGN KEY (banks_cards_id) REFERENCES turq_banks_cards(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: be1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_banks_transaction_bills
    ADD CONSTRAINT be1 FOREIGN KEY (engine_sequences_id) REFERENCES turq_engine_sequences(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: bt2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_banks_transactions
    ADD CONSTRAINT bt2 FOREIGN KEY (banks_cards_id) REFERENCES turq_banks_cards(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: bt7; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_banks_transaction_bills
    ADD CONSTRAINT bt7 FOREIGN KEY (banks_transaction_types_id) REFERENCES turq_banks_transaction_types(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: cr1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cheque_rolls
    ADD CONSTRAINT cr1 FOREIGN KEY (cheque_transaction_types_id) REFERENCES turq_cheque_transaction_types(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: cr2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cheque_rolls
    ADD CONSTRAINT cr2 FOREIGN KEY (current_cards_id) REFERENCES turq_current_cards(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: cr3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cheque_rolls
    ADD CONSTRAINT cr3 FOREIGN KEY (banks_cards_id) REFERENCES turq_banks_cards(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: crac1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cheque_roll_accounting_accounts
    ADD CONSTRAINT crac1 FOREIGN KEY (accounting_accounts_id) REFERENCES turq_accounting_accounts(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: crac2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cheque_roll_accounting_accounts
    ADD CONSTRAINT crac2 FOREIGN KEY (id) REFERENCES turq_cheque_rolls(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: curAcc1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_current_accounting_accounts
    ADD CONSTRAINT "curAcc1" FOREIGN KEY (accounting_accounts_id) REFERENCES turq_accounting_accounts(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: curAcc2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_current_accounting_accounts
    ADD CONSTRAINT "curAcc2" FOREIGN KEY (current_accounting_types_id) REFERENCES turq_current_accounting_types(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: curAcc3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_current_accounting_accounts
    ADD CONSTRAINT "curAcc3" FOREIGN KEY (current_cards_id) REFERENCES turq_current_cards(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: eng_seq_trans; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_transaction_bills
    ADD CONSTRAINT eng_seq_trans FOREIGN KEY (engine_sequence) REFERENCES turq_engine_sequences(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: ex_rate; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_accounting_transaction_columns
    ADD CONSTRAINT ex_rate FOREIGN KEY (exchange_rate_id) REFERENCES turq_currency_exchange_rates(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: ex_rate; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_accounting_transactions
    ADD CONSTRAINT ex_rate FOREIGN KEY (exchange_rate_id) REFERENCES turq_currency_exchange_rates(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: ex_rate; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_current_transactions
    ADD CONSTRAINT ex_rate FOREIGN KEY (exchange_rate_id) REFERENCES turq_currency_exchange_rates(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: ex_rate; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cash_transaction_rows
    ADD CONSTRAINT ex_rate FOREIGN KEY (exchange_rate_id) REFERENCES turq_currency_exchange_rates(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: ex_rate; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_banks_transactions
    ADD CONSTRAINT ex_rate FOREIGN KEY (exchange_rate_id) REFERENCES turq_currency_exchange_rates(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: ex_rate_cheque1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cheque_cheques
    ADD CONSTRAINT ex_rate_cheque1 FOREIGN KEY (exchange_rate_id) REFERENCES turq_currency_exchange_rates(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: f_acc; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_accounting_accounts
    ADD CONSTRAINT f_acc FOREIGN KEY (accounting_accounts_id) REFERENCES turq_accounting_accounts(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: f_bill_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_bill_in_engine_sequences
    ADD CONSTRAINT f_bill_id FOREIGN KEY (bills_id) REFERENCES turq_bills(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: f_cur_card; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_consignments
    ADD CONSTRAINT f_cur_card FOREIGN KEY (current_cards_id) REFERENCES turq_current_cards(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: f_cur_card; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_bills
    ADD CONSTRAINT f_cur_card FOREIGN KEY (current_cards_id) REFERENCES turq_current_cards(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: f_cur_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_transactions
    ADD CONSTRAINT f_cur_id FOREIGN KEY (current_cards_id) REFERENCES turq_current_cards(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: f_en_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_bill_in_engine_sequences
    ADD CONSTRAINT f_en_id FOREIGN KEY (engine_sequences_id) REFERENCES turq_engine_sequences(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: f_eng_seq_bill; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_bills
    ADD CONSTRAINT f_eng_seq_bill FOREIGN KEY (engine_sequences_id) REFERENCES turq_engine_sequences(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: f_ex_rate; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_transactions
    ADD CONSTRAINT f_ex_rate FOREIGN KEY (exchange_rate_id) REFERENCES turq_currency_exchange_rates(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: f_ex_rate; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_bills
    ADD CONSTRAINT f_ex_rate FOREIGN KEY (exchange_rate_id) REFERENCES turq_currency_exchange_rates(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: f_ex_rate; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_consignments
    ADD CONSTRAINT f_ex_rate FOREIGN KEY (exchange_rate_id) REFERENCES turq_currency_exchange_rates(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: f_inv_card; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_accounting_accounts
    ADD CONSTRAINT f_inv_card FOREIGN KEY (inventory_cards_id) REFERENCES turq_inventory_cards(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: f_key_inv_card_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_customize_fields
    ADD CONSTRAINT f_key_inv_card_id FOREIGN KEY (inventory_card_id) REFERENCES turq_inventory_cards(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: f_level; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_user_permissions
    ADD CONSTRAINT f_level FOREIGN KEY (user_permissions_level) REFERENCES turq_user_permission_levels(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: f_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_accounting_accounts
    ADD CONSTRAINT f_type FOREIGN KEY (inventory_accounting_types_id) REFERENCES turq_inventory_accounting_types(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: fc1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_currency_exchange_rates
    ADD CONSTRAINT fc1 FOREIGN KEY (base_currency_id) REFERENCES turq_currencies(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: fc2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_currency_exchange_rates
    ADD CONSTRAINT fc2 FOREIGN KEY (exchange_currency_id) REFERENCES turq_currencies(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: ig1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_groups
    ADD CONSTRAINT ig1 FOREIGN KEY (parent_group) REFERENCES turq_inventory_groups(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: inv_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_transactions
    ADD CONSTRAINT inv_type FOREIGN KEY (transaction_type) REFERENCES turq_inventory_transaction_types(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: menu_module; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_engine_menu
    ADD CONSTRAINT menu_module FOREIGN KEY (menu_module_component) REFERENCES turq_module_components(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: row_to_card; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cash_transaction_rows
    ADD CONSTRAINT row_to_card FOREIGN KEY (cash_cards_id) REFERENCES turq_cash_cards(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: type2groups; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_cheque_transaction_types
    ADD CONSTRAINT type2groups FOREIGN KEY (transaction_types_parent) REFERENCES turq_cheque_transaction_type_groups(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: warehouse_in; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_transaction_bills
    ADD CONSTRAINT warehouse_in FOREIGN KEY (warehouse_in) REFERENCES turq_inventory_warehouses(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: warehouse_out; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY turq_inventory_transaction_bills
    ADD CONSTRAINT warehouse_out FOREIGN KEY (warehouse_out) REFERENCES turq_accounting_account_classes(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: DUMP TIMESTAMP; Type: DUMP TIMESTAMP; Schema: -; Owner: -
--

-- Completed on 2005-08-31 15:12:56 GTB Standard Time


--
-- PostgreSQL database dump complete
--

\unrestrict jNn5frb7e9ouAVKgU7Tn8CYUOZqhTyhYC0fVKqt4hVtCc7ki3k0pBbhQ5WXVMaj

