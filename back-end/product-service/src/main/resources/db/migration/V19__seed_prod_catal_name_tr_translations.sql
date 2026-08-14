-- prod_catal.name icin Turkce ceviriler. name zaten Ingilizce'ydi (Internet/Mobile/TV) ve
-- baslangicta kasitli olarak cevrilmemisti (var olmayan Turkce metin uydurulmasin diye) - ama
-- kullanici deneyiminde katalog adlarinin da Turkce'de gorunmesi istendigi icin eklendi.
INSERT INTO translation (entity_name, entity_id, field_name, locale, value, cuser)
SELECT 'PROD_CATAL', prod_catal_id, 'NAME', 'tr', CASE shrt_code
        WHEN 'INTERNET' THEN 'İnternet'
        WHEN 'MOBILE'   THEN 'Mobil'
        WHEN 'TV'       THEN 'TV'
    END, 'system'
FROM prod_catal
WHERE shrt_code IN ('INTERNET', 'MOBILE', 'TV');
