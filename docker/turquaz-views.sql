-- Original Turquaz 0.8 Beta 4 SQL VIEW definitions
-- Extracted from database/turquaz.script (HSQLDB 1.7.3 source)
-- Restored at runtime by SeedRunner; Hibernate hbm2ddl creates empty
-- tables for these names instead of views (because .hbm.xml maps them
-- as <class table=turq_view_*>). Without views the chart-of-accounts
-- query returns 0 rows because it inner-joins TurqViewAccTotal.

CREATE VIEW TURQ_VIEW_ACC_TOTALS (ACCOUNTING_ACCOUNTS_ID,TOTALCREDITAMOUNT,TOTALDEPTAMOUNT) AS SELECT account.id AS accounting_accounts_id, tabletranscolumns.totalcreditamount, tabletranscolumns.totaldeptamount
   FROM turq_accounting_accounts account
   LEFT JOIN ( SELECT transcolumns.accounting_accounts_id, sum(transcolumns.rows_credit_in_base_currency) AS totalcreditamount, sum(transcolumns.rows_dept_in_base_currency) AS totaldeptamount
                FROM turq_accounting_transaction_columns transcolumns
               GROUP BY transcolumns.accounting_accounts_id) tabletranscolumns ON tabletranscolumns.accounting_accounts_id = account.id;

CREATE VIEW TURQ_VIEW_ACC_TRANS_TOTAL_AMOUNT (ACCOUNTING_TRANSACTIONS_ID,TOTALCREDITAMOUNT,TOTALDEPTAMOUNT) AS SELECT trans.id AS accounting_transactions_id, tabletranscolumns.totalcreditamount, tabletranscolumns.totaldeptamount
   FROM turq_accounting_transactions trans
   LEFT JOIN ( SELECT transcolumns.accounting_transactions_id, sum(transcolumns.rows_credit_in_base_currency) AS totalcreditamount, sum(transcolumns.rows_dept_in_base_currency) AS totaldeptamount
                FROM turq_accounting_transaction_columns transcolumns
               GROUP BY transcolumns.accounting_transactions_id) tabletranscolumns ON tabletranscolumns.accounting_transactions_id = trans.id;

CREATE VIEW TURQ_VIEW_BILL_TRANS_TOTAL (BILLS_ID,TOTALPRICE,VATAMOUNT,SPECIALVATAMOUNT,DISCOUNTAMOUNT) AS SELECT billin.bills_id, sum(invtrans.total_price_in_foreign_currency) AS totalprice, sum(invtrans.vat_amount_in_foreign_currency) AS vatamount, sum(invtrans.vat_special_amount_in_foreign_currency) AS specialvatamount, sum(invtrans.discount_amount_in_foreign_currency) AS discountamount
   FROM turq_inventory_transactions invtrans, turq_bill_in_engine_sequences billin
  WHERE invtrans.engine_sequences_id = billin.engine_sequences_id
  GROUP BY billin.bills_id;

CREATE VIEW TURQ_VIEW_CHEQUE_STATUS (CHEQUE_CHEQUES_ID,CHEQUES_PORTFOLIO_NO,CHEQUES_NO,BANKS_ID,CHEQUES_DUE_DATE,CHEQUES_DEBTOR,CHEQUES_PAYMENT_PLACE,CHEQUES_VALUE_DATE,CHEQUES_AMOUNT,CURRENCIES_ID,CHEQUE_TRANSACTION_TYPES_ID,TRANSACTION_TYPS_NAME,TRANSACTION_TYPES_PARENT,CHEQUE_ROLLS_ID,CHEQUE_ROLLS_DATE,ENGINE_SEQUENCES_ID,CHEQUE_ROLL_NO,CURRENT_CARDS_ID,BANKS_CARDS_ID) AS SELECT cheq.id AS cheque_cheques_id, cheq.cheques_portfolio_no, cheq.cheques_no, cheq.banks_id, cheq.cheques_due_date, cheq.cheques_debtor, cheq.cheques_payment_place, cheq.cheques_value_date, cheq.cheques_amount, cheq.currencies_id, transtype.id AS cheque_transaction_types_id, transtype.transaction_typs_name, transtype.transaction_types_parent, rolls.id AS cheque_rolls_id, rolls.cheque_rolls_date, rolls.engine_sequences_id, rolls.cheque_roll_no, rolls.current_cards_id, rolls.banks_cards_id
   FROM turq_cheque_cheques cheq, turq_cheque_rolls rolls, turq_cheque_cheque_in_rolls cheqrolls, turq_cheque_transaction_types transtype
  WHERE cheq.id = cheqrolls.cheque_cheques_id AND rolls.id = cheqrolls.cheque_rolls_id AND transtype.id = rolls.cheque_transaction_types_id AND rolls.id = (( SELECT max(roll_inner.id) 
           FROM turq_cheque_rolls roll_inner, turq_cheque_cheque_in_rolls cheqroll_inner, turq_cheque_cheques cheq_inner
          WHERE cheqroll_inner.cheque_rolls_id = roll_inner.id AND cheqroll_inner.cheque_cheques_id = cheq_inner.id AND cheqroll_inner.cheque_cheques_id = cheq.id));

CREATE VIEW TURQ_VIEW_CURRENT_AMOUNT_TOTAL (CURRENT_CARDS_ID,TRANSACTIONS_TOTAL_CREDIT,TRANSACTIONS_TOTAL_DEPT,TRANSACTIONS_BALANCE_NOW) AS SELECT cards.id AS current_cards_id, tabletrans.transactions_total_credit, tabletrans.transactions_total_dept, tabletrans.transactions_balance_now
   FROM turq_current_cards cards
   LEFT JOIN ( SELECT trans.current_cards_id, sum(trans.transactions_total_credit) AS transactions_total_credit, sum(trans.transactions_total_dept) AS transactions_total_dept, sum(trans.transactions_total_credit - trans.transactions_total_dept) AS transactions_balance_now
                FROM turq_current_transactions trans
               GROUP BY trans.current_cards_id) tabletrans ON tabletrans.current_cards_id = cards.id;

CREATE VIEW TURQ_VIEW_INV_PRICE_TOTALS (ENGINE_SEQUENCES_ID,TOTALPRICE,VATAMOUNT,SPECIALVATAMOUNT,DISCOUNTAMOUNT) AS SELECT invtrans.engine_sequences_id, sum(invtrans.total_price_in_foreign_currency) AS totalprice, sum(invtrans.vat_amount_in_foreign_currency) AS vatamount, sum(invtrans.vat_special_amount_in_foreign_currency) AS specialvatamount, sum(invtrans.discount_amount_in_foreign_currency) AS discountamount
   FROM turq_inventory_transactions invtrans
  GROUP BY invtrans.engine_sequences_id;

CREATE VIEW TURQ_VIEW_INVENTORY_AMOUNT_TOTAL (INVENTORY_CARDS_ID,TRANSACTIONS_AMOUNT_IN,TRANSACTIONS_TOTAL_AMOUNT_OUT,TRANSACTIONS_TOTAL_AMOUNT_NOW) AS SELECT cards.id AS inventory_cards_id, tabletrans.transactions_amount_in, tabletrans.transactions_total_amount_out, tabletrans.transactions_total_amount_now
   FROM turq_inventory_cards cards
   LEFT JOIN ( SELECT trans.inventory_cards_id, sum(trans.amount_in) AS transactions_amount_in, sum(trans.amount_out) AS transactions_total_amount_out, sum(trans.amount_in - trans.amount_out) AS transactions_total_amount_now
                FROM turq_inventory_transactions trans
               GROUP BY trans.inventory_cards_id) tabletrans ON tabletrans.inventory_cards_id = cards.id;

CREATE VIEW TURQ_VIEW_INVENTORY_TOTALS (INVENTORY_CARDS_ID,TOTAL_AMOUNT_IN,TOTAL_PRICE_IN,TOTAL_AMOUNT_OUT,TOTAL_PRICE_OUT,TOTAL_TRANSOVER_AMOUNT_IN,TOTAL_TRANSOVER_PRICE_IN,TOTAL_TRANSOVER_AMOUNT_OUT,TOTAL_TRANSOVER_PRICE_OUT) AS SELECT trans.id AS inventory_cards_id, transin.totalamountin AS total_amount_in, transin.totalpricein AS total_price_in, transout.totalamountout AS total_amount_out, transout.totalpriceout AS total_price_out, transoverin.totalamountin AS total_transover_amount_in, transoverin.totalpricein AS total_transover_price_in, transoverout.totalamountout AS total_transover_amount_out, transoverout.totalpriceout AS total_transover_price_out
   FROM turq_inventory_cards trans
   LEFT JOIN ( SELECT turq_inventory_transactions.inventory_cards_id, sum(turq_inventory_transactions.total_price) AS totalpricein, sum(turq_inventory_transactions.amount_in) AS totalamountin
                FROM turq_inventory_transactions
               WHERE turq_inventory_transactions.amount_in <> 0 AND turq_inventory_transactions.transaction_type = 1
               GROUP BY turq_inventory_transactions.inventory_cards_id) transin ON trans.id = transin.inventory_cards_id
   LEFT JOIN ( SELECT turq_inventory_transactions.inventory_cards_id, sum(turq_inventory_transactions.total_price) AS totalpriceout, sum(turq_inventory_transactions.amount_out) AS totalamountout
                FROM turq_inventory_transactions
               WHERE turq_inventory_transactions.transaction_type = 1 AND turq_inventory_transactions.amount_out <> 0
               GROUP BY turq_inventory_transactions.inventory_cards_id) transout ON trans.id = transout.inventory_cards_id
   LEFT JOIN ( SELECT turq_inventory_transactions.inventory_cards_id, sum(turq_inventory_transactions.total_price) AS totalpricein, sum(turq_inventory_transactions.amount_in) AS totalamountin
                FROM turq_inventory_transactions
               WHERE turq_inventory_transactions.amount_in <> 0 AND turq_inventory_transactions.transaction_type = 0
               GROUP BY turq_inventory_transactions.inventory_cards_id) transoverin ON trans.id = transoverin.inventory_cards_id
   LEFT JOIN ( SELECT turq_inventory_transactions.inventory_cards_id, sum(turq_inventory_transactions.total_price) AS totalpriceout, sum(turq_inventory_transactions.amount_out) AS totalamountout
                FROM turq_inventory_transactions
               WHERE turq_inventory_transactions.transaction_type = 0 AND turq_inventory_transactions.amount_out <> 0
               GROUP BY turq_inventory_transactions.inventory_cards_id) transoverout ON trans.id = transoverout.inventory_cards_id;

