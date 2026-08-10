-- FR-014 ACC-012/BR-04: PROD_OFR_REL grubuna EXCL (sepette es zamanli olamaz) tipi eklenir.
-- V9'daki MANDATORY/OPTIONAL satirlarindan sonra eklendigi icin (ve o migrationdan bu yana
-- gnl_tp'ye baska satir eklenmedigi icin) bu satirin gnl_tp_id'si 594 olacaktir - product-service
-- V9__seed_prod_ofr_rel_excl.sql bu ID'yi sabit olarak referans verir (ayni varsayim V9/V7'de de var).
INSERT INTO gnl_tp (name, descr, shrt_code, ent_code_name, ent_name, is_actv, cuser)
VALUES ('Çakışan', 'Çakışan', 'EXCL', 'PROD_OFR_REL', 'PROD_OFR_REL', true, 'system');
