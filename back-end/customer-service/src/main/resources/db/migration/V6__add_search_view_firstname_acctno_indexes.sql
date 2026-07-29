-- firstName/acctNo, lastName/tcNo/gsm ile ayni arama akisinda (CustomerSearchSpecifications)
-- kullaniliyordu ama index'leri eksikti (bkz. CustomerSearchView.java @Index listesi).

CREATE INDEX idx_csv_firstname ON customer_search_view (first_name);
CREATE INDEX idx_csv_acctno ON customer_search_view (acct_no);
