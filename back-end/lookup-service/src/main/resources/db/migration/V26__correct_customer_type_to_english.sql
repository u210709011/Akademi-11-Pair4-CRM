-- V24, CUSTOMER_TYPE icin YOUNG/RETIRED shrt_code'larini hedeflemisti - bu, V5'teki ilk seed
-- verisiydi. Ama V7 (V5'ten cok once) bu satirlari zaten CORPORATE/INDIVIDUAL (Kurumsal/Bireysel)
-- olarak yeniden tanimlamisti; V24 artik var olmayan bir shrt_code'u aradigi icin sessizce 0
-- satir etkiledi. Gercek/guncel veriyi (CORPORATE/INDIVIDUAL) hedefleyen duzeltme burada.
UPDATE gnl_tp SET name = 'Corporate', descr = 'Corporate'
WHERE ent_code_name = 'CUSTOMER_TYPE' AND shrt_code = 'CORPORATE';

UPDATE gnl_tp SET name = 'Individual', descr = 'Individual'
WHERE ent_code_name = 'CUSTOMER_TYPE' AND shrt_code = 'INDIVIDUAL';
