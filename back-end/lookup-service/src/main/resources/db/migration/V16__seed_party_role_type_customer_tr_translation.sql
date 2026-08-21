-- CUSTOMER (PARTY_ROLE_TYPE) icin Turkce ceviri - V15'te Ingilizceye cevrilen taban degerin
-- 'tr' locale'indeki karsiligi. id hardcode edilmez, shrt_code'dan INSERT...SELECT ile bulunur.
INSERT INTO translation (entity_name, entity_id, field_name, locale, value, cuser)
SELECT 'GNL_TP', gnl_tp_id, field_name, 'tr', 'Musteri', 'system'
FROM gnl_tp, (VALUES ('NAME'), ('DESCR')) AS fields(field_name)
WHERE ent_code_name = 'PARTY_ROLE_TYPE' AND shrt_code = 'CUSTOMER';
