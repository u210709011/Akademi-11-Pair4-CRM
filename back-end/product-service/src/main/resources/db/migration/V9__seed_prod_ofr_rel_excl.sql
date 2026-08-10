-- V9__seed_prod_ofr_rel_excl.sql (product-service)
-- FR-014 ACC-012/BR-04: Internet katalogundaki plan tekliflerinin (prod_spec_id 1=Fiber,
-- 3=Bakir/ADSL-VDSL, 4=Kablosuz/Uydu - prod_ofr_id 1-17) hicbiri bir digeriyle es zamanli
-- sepette/siparişte olamaz (ayni anda birden fazla ev/is interneti hattina sahip olunamaz).
-- CPE ekipmani (prod_spec_id=2: Broadband Modem/Wi-Fi Router/Smart Home Hub/Mesh Wi-Fi,
-- prod_ofr_id 18-21) bu kurala dahil DEGIL - onlar zaten MANDATORY iliskiyle plana bagli
-- (bkz. V7'deki prod_ofr_rel satirlari) ve birbirleriyle ya da planlarla cakismaz.
--
-- EXCL lookup id'si 594 - lookup-service V12__seed_prod_ofr_rel_excl_tp.sql'den (MANDATORY=592/
-- OPTIONAL=593'ten sonraki ilk serbest id, bkz. o migration'in yorumu).
--
-- Tum plan-plan ciftlerini (17 offering -> C(17,2)=136 satir) elle listelemek yerine ayni
-- prod_ofr tablosundan uretilir; prod_ofr_rel_id kolonu belirtilmedigi icin sequence'ten
-- normal sekilde atanir (V7'nin setval'iyla cakismaz, 8'den devam eder).
INSERT INTO prod_ofr_rel (prod_ofr_id1, prod_ofr_id2, rel_tp_id, qty, is_actv, cuser)
SELECT a.prod_ofr_id, b.prod_ofr_id, 594, 1, true, 'system'
FROM prod_ofr a
JOIN prod_ofr b ON a.prod_ofr_id < b.prod_ofr_id
WHERE a.prod_spec_id IN (1, 3, 4) AND b.prod_spec_id IN (1, 3, 4);
