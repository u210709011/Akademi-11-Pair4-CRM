-- V24'te Ingilizceye cevrilen taban degerlerin 'tr' locale'indeki karsiligi (bkz. V16 ile ayni
-- desen). CUSTOMER (PARTY_ROLE_TYPE) haric hepsi yeni INSERT - CUSTOMER'in V16'da eklenen
-- "Musteri" (yazim hatali, ü eksik) satiri burada UPDATE ile duzeltilir.
UPDATE translation SET value = 'Müşteri'
WHERE entity_name = 'GNL_TP' AND locale = 'tr' AND field_name IN ('NAME', 'DESCR')
  AND entity_id IN (SELECT gnl_tp_id FROM gnl_tp WHERE ent_code_name = 'PARTY_ROLE_TYPE' AND shrt_code = 'CUSTOMER');

INSERT INTO translation (entity_name, entity_id, field_name, locale, value, cuser)
SELECT 'GNL_TP', gnl_tp.gnl_tp_id, fields.field_name, 'tr', tr_names.tr_value, 'system'
FROM gnl_tp,
    (VALUES
        ('PARTY_ROLE_TYPE', 'PARTNER',    'Tedarikçi/Partner'),
        ('GENDER',          'MALE',       'Erkek'),
        ('GENDER',          'FEMALE',     'Kadın'),
        ('CUSTOMER_TYPE',   'YOUNG',      'Genç Müşteri'),
        ('CUSTOMER_TYPE',   'RETIRED',    'Emekli Müşteri'),
        ('ACCOUNT_TYPE',    'CUST_ACCT',  'Müşteri Hesap'),
        ('ACCOUNT_TYPE',    'BILL_ACCT',  'Fatura Hesap'),
        ('CNTC_MEDIUM',     'PSTN',       'Sabit Hat'),
        ('CNTC_MEDIUM',     'FAX',        'Faks'),
        ('CNTC_MEDIUM',     'GSM',        'Mobil Hat'),
        ('CNTC_MEDIUM',     'EML',        'E-Posta')
    ) AS tr_names(ent_code_name, shrt_code, tr_value),
    (VALUES ('NAME'), ('DESCR')) AS fields(field_name)
WHERE gnl_tp.ent_code_name = tr_names.ent_code_name AND gnl_tp.shrt_code = tr_names.shrt_code;
