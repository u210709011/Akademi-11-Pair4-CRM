-- GENDER/CUSTOMER_TYPE/ACCOUNT_TYPE/CNTC_MEDIUM/PARTY_ROLE_TYPE gruplarinin cogu hic
-- anglicize edilmemisti (V15 sadece PARTY_ROLE_TYPE/CUSTOMER'i duzeltmisti). Bu gruplar
-- arayuzde gosterilen alanlar (gender/customer type/account type/contact medium/role) - ayni
-- desen (bkz. V15) burada tum ilgili gruplara uygulanir: taban deger Ingilizce olur, Turkce
-- V25'te translation tablosuna eklenir.
UPDATE gnl_tp SET name = 'Supplier/Partner', descr = 'Supplier/Partner'
WHERE ent_code_name = 'PARTY_ROLE_TYPE' AND shrt_code = 'PARTNER';

UPDATE gnl_tp SET name = 'Male', descr = 'Male'
WHERE ent_code_name = 'GENDER' AND shrt_code = 'MALE';

UPDATE gnl_tp SET name = 'Female', descr = 'Female'
WHERE ent_code_name = 'GENDER' AND shrt_code = 'FEMALE';

UPDATE gnl_tp SET name = 'Young Customer', descr = 'Young Customer'
WHERE ent_code_name = 'CUSTOMER_TYPE' AND shrt_code = 'YOUNG';

UPDATE gnl_tp SET name = 'Retired Customer', descr = 'Retired Customer'
WHERE ent_code_name = 'CUSTOMER_TYPE' AND shrt_code = 'RETIRED';

UPDATE gnl_tp SET name = 'Customer Account', descr = 'Customer Account'
WHERE ent_code_name = 'ACCOUNT_TYPE' AND shrt_code = 'CUST_ACCT';

UPDATE gnl_tp SET name = 'Billing Account', descr = 'Billing Account'
WHERE ent_code_name = 'ACCOUNT_TYPE' AND shrt_code = 'BILL_ACCT';

UPDATE gnl_tp SET name = 'Landline', descr = 'Landline'
WHERE ent_code_name = 'CNTC_MEDIUM' AND shrt_code = 'PSTN';

UPDATE gnl_tp SET name = 'Fax', descr = 'Fax'
WHERE ent_code_name = 'CNTC_MEDIUM' AND shrt_code = 'FAX';

UPDATE gnl_tp SET name = 'Mobile', descr = 'Mobile'
WHERE ent_code_name = 'CNTC_MEDIUM' AND shrt_code = 'GSM';

UPDATE gnl_tp SET name = 'Email', descr = 'Email'
WHERE ent_code_name = 'CNTC_MEDIUM' AND shrt_code = 'EML';
