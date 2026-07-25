-- Yeni semanin seed verisi - gercek DBeaver referans verisinden alinmistir (id'ler dahil,
-- birebir korunur). Sadece PARTY_ROLE_TYPE/GENDER/CITY/CUSTOMER_TYPE gruplari henuz
-- dogrulanmadi (TAHMINI, yorumlarda belirtilmistir). "deneme..." isimli test/junk satirlar
-- (id=86, id=42) kasten atlandi.
--
-- NOT: CUST_TP (Ozel/Resmi Daireler/Belediyeler/... - id=10000-12000 serisi, GNL_TP.163/164'e
-- bagli hiyerarsik veri) henuz eklenmedi - GNL_TP'nin bir parent/hiyerarsi kolonuna ihtiyaci
-- var gibi gorunuyor, şemaya eklemeden once onaylanmasi gerekiyor.

-- ---- GNL_TP: gercek veriden, explicit id ----
INSERT INTO gnl_tp (gnl_tp_id, name, descr, shrt_code, ent_code_name, ent_name, is_actv, cuser) VALUES
    (21,  'Genel',         'Genel Kullanim', 'GNL',      'PARTY',                'PARTY',                true, 'system'),
    (47,  'Gerceklenir',   'Gerceklenir',    'REALIZED', 'PROD_SPEC_SRVC_SPEC',  'PROD_SPEC_SRVC_SPEC',  true, 'system'),
    (48,  'Gerceklenir',   'Gerceklenir',    'REALIZED', 'PROD_SPEC_RSRC_SPEC',  'PROD_SPEC_RSRC_SPEC',  true, 'system'),
    (163, 'Kurumsal',      'Kurumsal',       'ORG',      'CAM_PARTY_TYPE',       'CAM_PARTY_TYPE',       true, 'system'),
    (164, 'Bireysel',      'Bireysel',       'INDV',     'CAM_PARTY_TYPE',       'CAM_PARTY_TYPE',       true, 'system'),
    (170, 'Sabit Hat',     'Sabit Hat',      'PSTN',     'CNTC_MEDIUM',          'CNTC_MEDIUM',          true, 'system'),
    (171, 'Faks',          'Faks',           'FAX',      'CNTC_MEDIUM',          'CNTC_MEDIUM',          true, 'system'),
    (172, 'Mobil Hat',     'Mobil Hat',      'GSM',      'CNTC_MEDIUM',          'CNTC_MEDIUM',          true, 'system'),
    (173, 'E Posta',       'E Posta',        'EML',      'CNTC_MEDIUM',          'CNTC_MEDIUM',          true, 'system'),
    (223, 'Musteri Hesap', 'Musteri Hesap',  'CUST_ACCT','ACCOUNT_TYPE',         'ACCOUNT_TYPE',         true, 'system'),
    (224, 'Fatura Hesap',  'Fatura Hesap',   'BILL_ACCT','ACCOUNT_TYPE',         'ACCOUNT_TYPE',         true, 'system'),
    (591, 'Ana Urun',      'Ana Urun',       'PRNTPROD', 'PROD_REL',             'PROD_REL',             true, 'system');

-- ---- GNL_TP: henuz DBeaver ile dogrulanmadi, TAHMINI - id'ler auto-increment ----
INSERT INTO gnl_tp (name, descr, shrt_code, ent_code_name, ent_name, is_actv, cuser) VALUES
    ('Musteri',          'Musteri',          'CUSTOMER',  'PARTY_ROLE_TYPE','PARTY_ROLE_TYPE', true, 'system'),
    ('Tedarikci/Partner','Tedarikci/Partner','PARTNER',   'PARTY_ROLE_TYPE','PARTY_ROLE_TYPE', true, 'system'),
    ('Erkek',            'Erkek',            'MALE',      'GENDER',        'GENDER',        true, 'system'),
    ('Bayan',            'Bayan',            'FEMALE',    'GENDER',        'GENDER',        true, 'system'),
    ('Ankara',           'Ankara',           'ANKARA',    'CITY',          'CITY',          true, 'system'),
    ('Genc Musteri',     'Genc Musteri',     'YOUNG',     'CUSTOMER_TYPE', 'CUSTOMER_TYPE', true, 'system'),
    ('Emekli Musteri',   'Emekli Musteri',   'RETIRED',   'CUSTOMER_TYPE', 'CUSTOMER_TYPE', true, 'system');

-- ---- GNL_ST: gercek veriden, explicit id ----
INSERT INTO gnl_st (gnl_st_id, name, descr, shrt_code, is_actv, ent_code_name, ent_name, cuser) VALUES
    (51,    'Beklemede',                  'Beklemede',                  'WAIT',      true, 'CUST_ORD',             'CUST_ORD',             'system'),
    (52,    'Siparis Alindi Isleniyor',   'Siparis Alindi Isleniyor',   'MIDLWARE',  true, 'CUST_ORD',             'CUST_ORD',             'system'),
    (53,    'Tamamlandi',                 'Tamamlandi',                 'FINISHED',  true, 'CUST_ORD',             'CUST_ORD',             'system'),
    (54,    'Rededildi',                  'Rededildi',                  'REJECTED',  true, 'CUST_ORD',             'CUST_ORD',             'system'),
    (9001,  'Iptal edilmis',              'Iptal edilmis',              'CNCL',      true, 'CUST_ACCT_PROD_INVL', 'CUST_ACCT_PROD_INVL', 'system'),
    (9004,  'Beklemede',                  'Beklemede',                  'PNDG',      true, 'CUST_ACCT_PROD_INVL', 'CUST_ACCT_PROD_INVL', 'system'),
    (9009,  'Silinmis',                   'Silinmis',                   'DEL',       true, 'CUST_ACCT_PROD_INVL', 'CUST_ACCT_PROD_INVL', 'system'),
    (9010,  'Aktif',                      'Aktif',                      'ACTV',      true, 'CUST_ACCT_PROD_INVL', 'CUST_ACCT_PROD_INVL', 'system'),
    (10620, 'Askida',                     'Askida',                     'SPND',      true, 'CUST_ACCT_PROD_INVL', 'CUST_ACCT_PROD_INVL', 'system'),
    (115,   'Silinmis',                   'Silinmis',                   'DEL',       true, 'PROD',                 'PROD',                 'system'),
    (116,   'Aktif',                      'Aktif',                      'ACTV',      true, 'PROD',                 'PROD',                 'system'),
    (1500,  'Beklemede',                  'Beklemede',                  'PNDG',      true, 'PROD',                 'PROD',                 'system'),
    (10600, 'Askida',                     'Askida',                     'SPND',      true, 'PROD',                 'PROD',                 'system'),
    (75690, 'Siparis Iptal',              'Siparis Iptal',              'QUOTE_DEL', true, 'PROD',                 'PROD',                 'system'),
    (123,   'Silinmis',                   'Silinmis',                   'DEL',       true, 'PROD_CHAR_VAL',        'PROD_CHAR_VAL',        'system'),
    (124,   'Aktif',                      'Aktif',                      'ACTV',      true, 'PROD_CHAR_VAL',        'PROD_CHAR_VAL',        'system'),
    (11,    'Silinmis',                   'Silinmis',                   'DEL',       true, 'RSRC_SPEC',            'RSRC_SPEC',            'system'),
    (12,    'Aktif',                      'Aktif',                      'ACTV',      true, 'RSRC_SPEC',            'RSRC_SPEC',            'system'),
    (14,    'Pasif',                      'Pasif',                      'PASS',      true, 'RSRC_SPEC',            'RSRC_SPEC',            'system'),
    (156,   'Silinmis',                   'Silinmis',                   'DEL',       true, 'IND',                  'IND',                  'system'),
    (157,   'Aktif',                      'Aktif',                      'ACTV',      true, 'IND',                  'IND',                  'system'),
    (158,   'Pasif',                      'Pasif',                      'PASS',      true, 'IND',                  'IND',                  'system'),
    (164,   'Aktif',                      'Aktif',                      'ACTV',      true, 'CUST_ACCT',            'CUST_ACCT',            'system'),
    (170,   'Silinmis',                   'Silinmis',                   'DEL',       true, 'CUST_ACCT',            'CUST_ACCT',            'system'),
    (171,   'Pasif',                      'Pasif',                      'PASS',      true, 'CUST_ACCT',            'CUST_ACCT',            'system'),
    (9,     'Silinmis',                   'Silinmis',                   'DEL',       true, 'PROD_SPEC',            'PROD_SPEC',            'system'),
    (10,    'Aktif',                      'Aktif',                      'ACTV',      true, 'PROD_SPEC',            'PROD_SPEC',            'system'),
    (13,    'Pasif',                      'Pasif',                      'PASS',      true, 'PROD_SPEC',            'PROD_SPEC',            'system'),
    (239,   'Aktif',                      'Aktif',                      'ACTV',      true, 'PROD_SPEC_SRVC_SPEC',  'PROD_SPEC_SRVC_SPEC',  'system'),
    (240,   'Silinmis',                   'Silinmis',                   'DEL',       true, 'PROD_SPEC_SRVC_SPEC',  'PROD_SPEC_SRVC_SPEC',  'system'),
    (16,    'Silinmis',                   'Silinmis',                   'DEL',       true, 'PARTY',                'PARTY',                'system'),
    (17,    'Aktif',                      'Aktif',                      'ACTV',      true, 'PARTY',                'PARTY',                'system'),
    (18,    'Pasif',                      'Pasif',                      'PASS',      true, 'PARTY',                'PARTY',                'system'),
    (61,    'Genel',                      'Genel Kullanim',             'GNL',       true, 'PARTY',                'PARTY',                'system'),
    (79,    'Silinmis',                   'Silinmis',                   'DEL',       true, 'PARTY_ROLE',           'PARTY_ROLE',           'system'),
    (80,    'Aktif',                      'Aktif',                      'ACTV',      true, 'PARTY_ROLE',           'PARTY_ROLE',           'system'),
    (81,    'Pasif',                      'Pasif',                      'PASS',      true, 'PARTY_ROLE',           'PARTY_ROLE',           'system'),
    (19,    'Aktif',                      'Aktif',                      'ACTV',      true, 'PROD_SPEC_RSRC_SPEC',  'PROD_SPEC_RSRC_SPEC',  'system'),
    (20,    'Pasif',                      'Pasif',                      'PASS',      true, 'PROD_SPEC_RSRC_SPEC',  'PROD_SPEC_RSRC_SPEC',  'system'),
    (21,    'Silinmis',                   'Silinmis',                   'DEL',       true, 'PROD_SPEC_RSRC_SPEC',  'PROD_SPEC_RSRC_SPEC',  'system'),
    (22,    'Aktif',                      'Aktif',                      'ACTV',      true, 'SRVC_SPEC',            'SRVC_SPEC',            'system'),
    (23,    'Silinmis',                   'Silinmis',                   'DEL',       true, 'SRVC_SPEC',            'SRVC_SPEC',            'system'),
    (33,    'Silinmis',                   'Silinmis',                   'DEL',       true, 'PROD_CATAL',           'PROD_CATAL',           'system'),
    (34,    'Aktif',                      'Aktif',                      'ACTV',      true, 'PROD_CATAL',           'PROD_CATAL',           'system'),
    (35,    'Pasif',                      'Pasif',                      'PASS',      true, 'PROD_CATAL',           'PROD_CATAL',           'system'),
    (70,    'Aktif',                      'Aktif',                      'ACTV',      true, 'PROD_OFR',             'PROD_OFR',             'system'),
    (71,    'Silinmis',                   'Silinmis',                   'DEL',       true, 'PROD_OFR',             'PROD_OFR',             'system'),
    (72,    'Pasif',                      'Pasif',                      'PASS',      true, 'PROD_OFR',             'PROD_OFR',             'system'),
    (82,    'Iptal edilmis',              'Iptal edilmis',              'CNCL',      true, 'CUST',                 'CUST',                 'system'),
    (83,    'Aktif',                      'Aktif',                      'ACTV',      true, 'CUST',                 'CUST',                 'system'),
    (84,    'Pasif',                      'Pasif',                      'PASS',      true, 'CUST',                 'CUST',                 'system'),
    (177,   'Aktif',                      'Aktif',                      'ACTV',      true, 'PROD_CATAL_PROD_OFR',  'PROD_CATAL_PROD_OFR',  'system'),
    (178,   'Silinmis',                   'Silinmis',                   'DEL',       true, 'PROD_CATAL_PROD_OFR',  'PROD_CATAL_PROD_OFR',  'system'),
    (179,   'Pasif',                      'Pasif',                      'PASS',      true, 'PROD_CATAL_PROD_OFR',  'PROD_CATAL_PROD_OFR',  'system'),
    (254,   'Silinmis',                   'Silinmis',                   'DEL',       true, 'CMPG',                 'CMPG',                 'system'),
    (255,   'Aktif',                      'Aktif',                      'ACTV',      true, 'CMPG',                 'CMPG',                 'system'),
    (256,   'Pasif',                      'Pasif',                      'PASS',      true, 'CMPG',                 'CMPG',                 'system'),
    (109,   'Silinmis',                   'Silinmis',                   'DEL',       true, 'CNTC_MEDIUM',          'CNTC_MEDIUM',          'system'),
    (110,   'Aktif',                      'Aktif',                      'ACTV',      true, 'CNTC_MEDIUM',          'CNTC_MEDIUM',          'system'),
    (111,   'Pasif',                      'Pasif',                      'PASS',      true, 'CNTC_MEDIUM',          'CNTC_MEDIUM',          'system');

-- Explicit PK'li insert'ler BIGSERIAL sequence'ini ilerletmez - sonraki uygulama-seviyesi
-- POST'larin mevcut id'lerle carpismamasi icin sequence'i mevcut MAX'a esitle.
SELECT setval(pg_get_serial_sequence('gnl_tp', 'gnl_tp_id'), (SELECT MAX(gnl_tp_id) FROM gnl_tp));
SELECT setval(pg_get_serial_sequence('gnl_st', 'gnl_st_id'), (SELECT MAX(gnl_st_id) FROM gnl_st));

-- ---- TYPE_VALUE ----
-- Gercek veriden: PROD/20, PARTY/9, CUST_ACCT/13, CUST/12 (field_name = o tabloya atanmis
-- polimorfik tip etiketi numarasi). ORDER icin henuz gercek deger yok - eklenmedi.
INSERT INTO type_value (table_name, field_name, description, cuser) VALUES
    ('PARTY',     9,  'Party_id',     'system'),
    ('CUST_ACCT', 13, 'Cust_acct_id', 'system'),
    ('PROD',      20, 'Prod_id',      'system'),
    ('CUST',      12, 'Cust_id',      'system');
