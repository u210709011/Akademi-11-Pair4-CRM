-- V5'te CUSTOMER (PARTY_ROLE_TYPE) satiri Turkce ("Musteri") olarak seed edilmisti. Bu uygulamada
-- her string'in varsayilan/temel degeri Ingilizce olmalidir (bkz. V14 yorumu) - Turkce artik
-- translation tablosunda 'tr' locale'i icin ayri bir satirla saglanir (bkz. V16), taban deger
-- burada duzeltilir.
UPDATE gnl_tp
SET name = 'Customer', descr = 'Customer'
WHERE ent_code_name = 'PARTY_ROLE_TYPE' AND shrt_code = 'CUSTOMER';
