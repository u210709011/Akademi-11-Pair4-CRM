-- Seed verisi degismezlik kurallari (bkz. BRAIN.md SS7):
--   1. Mevcut bir VALUE_ID'nin anlamini asla degistirme (baska servis FK olarak sakliyor).
--   2. CODE string'lerini yeniden adlandirma (customer-service'te sabit gomulu).
--   3. Yeni deger eklemek guvenli; guncelleme/silme degil (soft-passive yap).
--   4. ACCT_TP=223 ve status/contact-medium kodlari customer-service kontratindan gelir -> dokunma.

INSERT INTO lookup (group_code, value_id, code, look_up_value) VALUES
  -- PARTY_TYPE: party-service kullaniyor
  ('PARTY_TYPE',       3001, 'INDIVIDUAL',   'Bireysel'),
  ('PARTY_TYPE',       3002, 'CORPORATE',    'Kurumsal'),

  -- PARTY_ROLE_TYPE: party-service kullaniyor
  ('PARTY_ROLE_TYPE',  2001, 'CUSTOMER',     'Musteri'),
  -- BRAIN.md SS9-C: 2002'nin kodu/anlami netlesmedi (resimde "COOPERATION" yaziyor ama
  -- CUSTOMER karsisinda anlamsiz, muhtemelen SUPPLIER/PARTNER kastedilmis). party-service
  -- kod tarafinda bu satiri zorlayan bir cagri yok; party ekibiyle netlesene kadar TODO.
  ('PARTY_ROLE_TYPE',  2002, 'COOPERATION',  'TODO: parti ekibiyle netlestir (SUPPLIER/PARTNER olabilir)'),

  -- DATA_TYPE: contact-info-service polimorfik ADDR/CNTC_MEDIUM.DATA_TP_ID icin
  ('DATA_TYPE',         101, 'PARTY',        'Party'),
  ('DATA_TYPE',         102, 'CUST',         'Musteri'),

  -- GENDER: sadece id ile tasinir, kod opsiyonel ama seed'de mevcut
  ('GENDER',              1, 'MALE',         'Erkek'),
  ('GENDER',              2, 'FEMALE',       'Bayan'),

  -- CITY: sadece id ile tasinir, code kasten NULL
  ('CITY',              201, NULL,           'Ankara'),

  -- CNTC_MEDIUM_TYPE: customer-service SS3.2 kontrati - kod bazli resolve edilir, DEGISMEZ
  ('CNTC_MEDIUM_TYPE', 4001, 'EMAIL',        'E-Posta'),
  ('CNTC_MEDIUM_TYPE', 4002, 'MOBILE_PHONE', 'Cep Telefonu'),
  ('CNTC_MEDIUM_TYPE', 4003, 'HOME_PHONE',   'Ev Telefonu'),
  ('CNTC_MEDIUM_TYPE', 4004, 'FAX',          'Faks'),

  -- CUST_STATUS: customer-service SS3.2 kontrati - ACTIVE kod bazli resolve edilir, DEGISMEZ
  -- (SS9-A karari: party'nin tek STATUS grubuyla cakismasini onlemek icin CUST_STATUS ve
  -- ACCT_STATUS ayri gruplar olarak seed edilir, ikisinde de deger 1=ACTIVE)
  ('CUST_STATUS',         1, 'ACTIVE',       'Aktif'),
  ('CUST_STATUS',         2, 'PASSIVE',      'Pasif'),
  ('CUST_STATUS',         3, 'DELETED',      'Silinmis'),

  -- ACCT_STATUS: customer-service SS3.2 kontrati - ACTIVE kod bazli resolve edilir, DEGISMEZ
  ('ACCT_STATUS',         1, 'ACTIVE',       'Aktif'),
  ('ACCT_STATUS',         2, 'PASSIVE',      'Pasif'),

  -- ACCT_TP: customer-service sabit ID kullanimi (DefaultLookupValues.DEFAULT_ACCOUNT_TYPE_ID=223L)
  -- SS9-B karari: resimdeki 9001=FATURA yerine calisan kod (223) esas alinir.
  ('ACCT_TP',            223, 'STANDARD',    'Standart Hesap'),

  -- CUST_TP: su an hicbir servis tarafindan cagrilmiyor, ileride kullanilmak uzere seed edilir
  ('CUST_TP',           6001, 'YOUNG',       'Genc Musteri'),
  ('CUST_TP',           6002, 'RETIRED',     'Emekli Musteri');
