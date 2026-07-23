-- Yeni semanin sifirdan seed verisi.
--
-- GNL_TP/GNL_ST: her satir tek bir deger, ent_code_name o degerin grubunu belirtir. Asagidaki
-- CNTC_MEDIUM (Sabit Hat/PSTN, Faks/FAX, Mobil Hat/GSM, E Posta/EML) ve ACCOUNT_TYPE
-- (Musteri Hesap/CUST_ACCT, Fatura Hesap/BILL_ACCT) gruplari gercek veriden alinmistir.
-- Diger gruplarin (PARTY_TYPE, PARTY_ROLE_TYPE, GENDER, CITY, CUSTOMER_TYPE, CUST_STATUS,
-- ACCOUNT_STATUS) ent_code_name degerleri TAHMINIDIR - gercek DB'deki karsiliklariyla
-- (DBeaver) karsilastirilip gerekirse duzeltilmelidir.
--
-- TYPE_VALUE: GNL_TP/GNL_ST'den bagimsiz, gercek veriden alinan tablo->tip-etiketi kaydi.

-- ---- GNL_TP ----
INSERT INTO gnl_tp (name, descr, shrt_code, ent_code_name, ent_name, is_actv, cuser) VALUES
    ('Sabit Hat',        'Sabit Hat',        'PSTN',      'CNTC_MEDIUM',   'CNTC_MEDIUM',   true, 'system'),
    ('Faks',             'Faks',             'FAX',       'CNTC_MEDIUM',   'CNTC_MEDIUM',   true, 'system'),
    ('Mobil Hat',        'Mobil Hat',        'GSM',       'CNTC_MEDIUM',   'CNTC_MEDIUM',   true, 'system'),
    ('E Posta',          'E Posta',          'EML',       'CNTC_MEDIUM',   'CNTC_MEDIUM',   true, 'system'),
    ('Musteri Hesap',    'Musteri Hesap',    'CUST_ACCT', 'ACCOUNT_TYPE',  'ACCOUNT_TYPE',  true, 'system'),
    ('Fatura Hesap',     'Fatura Hesap',     'BILL_ACCT', 'ACCOUNT_TYPE',  'ACCOUNT_TYPE',  true, 'system'),
    ('Bireysel',         'Bireysel',         'INDIVIDUAL','PARTY_TYPE',    'PARTY_TYPE',    true, 'system'),
    ('Kurumsal',         'Kurumsal',         'CORPORATE', 'PARTY_TYPE',    'PARTY_TYPE',    true, 'system'),
    ('Musteri',          'Musteri',          'CUSTOMER',  'PARTY_ROLE_TYPE','PARTY_ROLE_TYPE', true, 'system'),
    ('Tedarikci/Partner','Tedarikci/Partner','PARTNER',   'PARTY_ROLE_TYPE','PARTY_ROLE_TYPE', true, 'system'),
    ('Erkek',            'Erkek',            'MALE',      'GENDER',        'GENDER',        true, 'system'),
    ('Bayan',            'Bayan',            'FEMALE',    'GENDER',        'GENDER',        true, 'system'),
    ('Ankara',           'Ankara',           'ANKARA',    'CITY',          'CITY',          true, 'system'),
    ('Genc Musteri',     'Genc Musteri',     'YOUNG',     'CUSTOMER_TYPE', 'CUSTOMER_TYPE', true, 'system'),
    ('Emekli Musteri',   'Emekli Musteri',   'RETIRED',   'CUSTOMER_TYPE', 'CUSTOMER_TYPE', true, 'system');

-- ---- GNL_ST ----
INSERT INTO gnl_st (name, descr, shrt_code, is_actv, ent_code_name, ent_name, cuser) VALUES
    ('Aktif',    'Aktif',    'ACTIVE',  true, 'CUST_STATUS',    'CUST_STATUS',    'system'),
    ('Pasif',    'Pasif',    'PASSIVE', true, 'CUST_STATUS',    'CUST_STATUS',    'system'),
    ('Silinmis', 'Silinmis', 'DELETED', true, 'CUST_STATUS',    'CUST_STATUS',    'system'),
    ('Aktif',    'Aktif',    'ACTIVE',  true, 'ACCOUNT_STATUS', 'ACCOUNT_STATUS', 'system'),
    ('Pasif',    'Pasif',    'PASSIVE', true, 'ACCOUNT_STATUS', 'ACCOUNT_STATUS', 'system');

-- ---- TYPE_VALUE ----
-- Gercek veriden: PROD/20, PARTY/9, CUST_ACCT/13, CUST/12 (field_name = o tabloya atanmis
-- polimorfik tip etiketi numarasi).
INSERT INTO type_value (table_name, field_name, description, cuser) VALUES
    ('PARTY',     9,  'Party_id',     'system'),
    ('CUST_ACCT', 13, 'Cust_acct_id', 'system'),
    ('PROD',      20, 'Prod_id',      'system'),
    ('CUST',      12, 'Cust_id',      'system');
