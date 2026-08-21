-- gnl_char_val icin Turkce ceviriler - V11'de seed edilmis orijinal Turkce metin, V18'de
-- Ingilizceye cevrilen taban degerin 'tr' locale'indeki karsiligi. id hardcode edilmez.
INSERT INTO translation (entity_name, entity_id, field_name, locale, value, cuser)
SELECT 'GNL_CHAR_VAL', char_val_id, 'VAL', 'tr', CASE shrt_code
        WHEN 'WIRELESS'        THEN 'Kablosuz'
        WHEN 'SATELLITE'       THEN 'Uydu'
        WHEN '12AY'            THEN '12 Ay'
        WHEN '24AY'            THEN '24 Ay'
        WHEN 'NO_COMMIT'       THEN 'Taahhütsüz'
        WHEN 'STATIC_IP_YES'   THEN 'Var'
        WHEN 'STATIC_IP_NO'    THEN 'Yok'
        WHEN 'DATA_UNLIMITED'  THEN 'Sınırsız'
        WHEN 'TV_BASIC'        THEN 'Temel'
        WHEN 'TV_STANDARD'     THEN 'Standart'
    END, 'system'
FROM gnl_char_val
WHERE shrt_code IN ('WIRELESS', 'SATELLITE', '12AY', '24AY', 'NO_COMMIT', 'STATIC_IP_YES', 'STATIC_IP_NO',
                     'DATA_UNLIMITED', 'TV_BASIC', 'TV_STANDARD');
