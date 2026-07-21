-- Yeni semanin ilk (sifirdan) seed verisi. Eski 'lookup' tablosundan veri tasinmiyor, id'leri
-- korunmuyor - id'ler bu tablonun kendi sequence'inden normal sekilde atanir.

-- ---- GNL_TP: "type" stilindeki gruplar ----
INSERT INTO gnl_tp (name, descr, shrt_code, ent_code_name, ent_name, is_actv, cuser) VALUES
    ('Party Type',          'Bireysel/Kurumsal parti tipi', 'PARTY_TYPE',       'PARTY_TYPE',       'Party',         true, 'system'),
    ('Party Role Type',     'Partinin sistemdeki rolu',     'PARTY_ROLE_TYPE',  'PARTY_ROLE_TYPE',  'PartyRole',     true, 'system'),
    ('Gender',              'Cinsiyet',                     'GENDER',           'GENDER',           'Individual',    true, 'system'),
    ('City',                'Sehir',                        'CITY',             'CITY',             'Address',       true, 'system'),
    ('Data Type',           'Polimorfik veri sahibi tipi',  'DATA_TYPE',        'DATA_TYPE',         NULL,           true, 'system'),
    ('Contact Medium Type', 'Iletisim kanali tipi',         'CNTC_MEDIUM_TYPE', 'CNTC_MEDIUM_TYPE',  'ContactMedium', true, 'system'),
    ('Account Type',        'Musteri hesap tipi',           'ACCT_TP',          'ACCT_TP',           'CustAcct',      true, 'system'),
    ('Customer Type',       'Musteri segment tipi',         'CUST_TP',          'CUST_TP',            NULL,           true, 'system');

-- ---- GNL_ST: "status" stilindeki gruplar ----
INSERT INTO gnl_st (name, descr, shrt_code, is_actv, ent_code_name, ent_name, cuser) VALUES
    ('Customer Status', 'Musteri durumu', 'CUST_STATUS', true, 'CUST_STATUS', 'Cust',     'system'),
    ('Account Status',  'Hesap durumu',   'ACCT_STATUS', true, 'ACCT_STATUS', 'CustAcct', 'system');

-- ---- TYPE_VALUE: GNL_TP altindaki degerler ----
INSERT INTO type_value (table_name, field_name, description, value, using_module_name, cuser)
SELECT 'GNL_TP', gnl_tp_id, 'Bireysel', 'INDIVIDUAL', NULL, 'system' FROM gnl_tp WHERE shrt_code = 'PARTY_TYPE'
UNION ALL
SELECT 'GNL_TP', gnl_tp_id, 'Kurumsal', 'CORPORATE', NULL, 'system' FROM gnl_tp WHERE shrt_code = 'PARTY_TYPE'
UNION ALL
SELECT 'GNL_TP', gnl_tp_id, 'Musteri', 'CUSTOMER', NULL, 'system' FROM gnl_tp WHERE shrt_code = 'PARTY_ROLE_TYPE'
UNION ALL
SELECT 'GNL_TP', gnl_tp_id, 'Tedarikci/Partner', 'PARTNER', NULL, 'system' FROM gnl_tp WHERE shrt_code = 'PARTY_ROLE_TYPE'
UNION ALL
SELECT 'GNL_TP', gnl_tp_id, 'Erkek', 'MALE', NULL, 'system' FROM gnl_tp WHERE shrt_code = 'GENDER'
UNION ALL
SELECT 'GNL_TP', gnl_tp_id, 'Bayan', 'FEMALE', NULL, 'system' FROM gnl_tp WHERE shrt_code = 'GENDER'
UNION ALL
SELECT 'GNL_TP', gnl_tp_id, 'Ankara', NULL, NULL, 'system' FROM gnl_tp WHERE shrt_code = 'CITY'
UNION ALL
SELECT 'GNL_TP', gnl_tp_id, 'Party', 'PARTY', NULL, 'system' FROM gnl_tp WHERE shrt_code = 'DATA_TYPE'
UNION ALL
SELECT 'GNL_TP', gnl_tp_id, 'Musteri', 'CUST', NULL, 'system' FROM gnl_tp WHERE shrt_code = 'DATA_TYPE'
UNION ALL
SELECT 'GNL_TP', gnl_tp_id, 'E-Posta', 'EMAIL', NULL, 'system' FROM gnl_tp WHERE shrt_code = 'CNTC_MEDIUM_TYPE'
UNION ALL
SELECT 'GNL_TP', gnl_tp_id, 'Cep Telefonu', 'MOBILE_PHONE', NULL, 'system' FROM gnl_tp WHERE shrt_code = 'CNTC_MEDIUM_TYPE'
UNION ALL
SELECT 'GNL_TP', gnl_tp_id, 'Ev Telefonu', 'HOME_PHONE', NULL, 'system' FROM gnl_tp WHERE shrt_code = 'CNTC_MEDIUM_TYPE'
UNION ALL
SELECT 'GNL_TP', gnl_tp_id, 'Faks', 'FAX', NULL, 'system' FROM gnl_tp WHERE shrt_code = 'CNTC_MEDIUM_TYPE'
UNION ALL
SELECT 'GNL_TP', gnl_tp_id, 'Standart Hesap', 'STANDARD', NULL, 'system' FROM gnl_tp WHERE shrt_code = 'ACCT_TP'
UNION ALL
SELECT 'GNL_TP', gnl_tp_id, 'Fatura Hesabi', 'BILLING', NULL, 'system' FROM gnl_tp WHERE shrt_code = 'ACCT_TP'
UNION ALL
SELECT 'GNL_TP', gnl_tp_id, 'Genc Musteri', 'YOUNG', NULL, 'system' FROM gnl_tp WHERE shrt_code = 'CUST_TP'
UNION ALL
SELECT 'GNL_TP', gnl_tp_id, 'Emekli Musteri', 'RETIRED', NULL, 'system' FROM gnl_tp WHERE shrt_code = 'CUST_TP';

-- ---- TYPE_VALUE: GNL_ST altindaki degerler ----
INSERT INTO type_value (table_name, field_name, description, value, using_module_name, cuser)
SELECT 'GNL_ST', gnl_st_id, 'Aktif', 'ACTIVE', NULL, 'system' FROM gnl_st WHERE shrt_code = 'CUST_STATUS'
UNION ALL
SELECT 'GNL_ST', gnl_st_id, 'Pasif', 'PASSIVE', NULL, 'system' FROM gnl_st WHERE shrt_code = 'CUST_STATUS'
UNION ALL
SELECT 'GNL_ST', gnl_st_id, 'Silinmis', 'DELETED', NULL, 'system' FROM gnl_st WHERE shrt_code = 'CUST_STATUS'
UNION ALL
SELECT 'GNL_ST', gnl_st_id, 'Aktif', 'ACTIVE', NULL, 'system' FROM gnl_st WHERE shrt_code = 'ACCT_STATUS'
UNION ALL
SELECT 'GNL_ST', gnl_st_id, 'Pasif', 'PASSIVE', NULL, 'system' FROM gnl_st WHERE shrt_code = 'ACCT_STATUS';
