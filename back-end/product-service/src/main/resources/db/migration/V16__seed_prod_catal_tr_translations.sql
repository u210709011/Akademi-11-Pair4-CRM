-- prod_catal.descr icin Turkce ceviriler - V7'deki orijinal Turkce metin, V12'de Ingilizceye
-- cevrilen taban degerin 'tr' locale'indeki karsiligi. id hardcode edilmez, shrt_code'dan
-- INSERT...SELECT ile bulunur. name zaten Ingilizce'ydi, ceviri gerekmiyor.
INSERT INTO translation (entity_name, entity_id, field_name, locale, value, cuser)
SELECT 'PROD_CATAL', prod_catal_id, 'DESCR', 'tr', CASE shrt_code
        WHEN 'INTERNET' THEN 'Ev ve işyeri internet ürünlerinin listelendiği katalog'
        WHEN 'MOBILE'   THEN 'Mobil hat ürünlerinin listelendiği katalog'
        WHEN 'TV'       THEN 'Dijital TV ürünlerinin listelendiği katalog'
    END, 'system'
FROM prod_catal
WHERE shrt_code IN ('INTERNET', 'MOBILE', 'TV');
