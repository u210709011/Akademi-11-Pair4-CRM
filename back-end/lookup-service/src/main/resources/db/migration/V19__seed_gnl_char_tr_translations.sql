-- gnl_char icin Turkce ceviriler - V11'de seed edilmis orijinal Turkce metin, V17'de Ingilizceye
-- cevrilen taban degerin 'tr' locale'indeki karsiligi olarak burada saklanir. id hardcode edilmez.
INSERT INTO translation (entity_name, entity_id, field_name, locale, value, cuser)
SELECT 'GNL_CHAR', char_id, 'NAME', 'tr', CASE shrt_code
        WHEN 'CONN_SPEED'        THEN 'Bağlantı Hızı'
        WHEN 'CONN_TYPE'         THEN 'Bağlantı Tipi'
        WHEN 'COMMITMENT_PERIOD' THEN 'Taahhüt Süresi'
        WHEN 'STATIC_IP'         THEN 'Statik IP'
        WHEN 'MOBILE_DATA_PKG'   THEN 'Mobil Veri Paketi'
        WHEN 'TV_CHANNEL_PKG'    THEN 'TV Kanal Paketi'
    END, 'system'
FROM gnl_char
WHERE shrt_code IN ('CONN_SPEED', 'CONN_TYPE', 'COMMITMENT_PERIOD', 'STATIC_IP', 'MOBILE_DATA_PKG', 'TV_CHANNEL_PKG');

INSERT INTO translation (entity_name, entity_id, field_name, locale, value, cuser)
SELECT 'GNL_CHAR', char_id, 'DESCR', 'tr', CASE shrt_code
        WHEN 'CONN_SPEED'        THEN 'İnternet bağlantısının indirme hızı'
        WHEN 'CONN_TYPE'         THEN 'İnternet bağlantısının alt yapı tipi'
        WHEN 'COMMITMENT_PERIOD' THEN 'Aboneliğin taahhüt süresi'
        WHEN 'STATIC_IP'         THEN 'Sabit IP adresi tahsisi durumu'
        WHEN 'MOBILE_DATA_PKG'   THEN 'Mobil hat aylık veri kotası'
        WHEN 'TV_CHANNEL_PKG'    THEN 'Dijital TV kanal paketi seviyesi'
    END, 'system'
FROM gnl_char
WHERE shrt_code IN ('CONN_SPEED', 'CONN_TYPE', 'COMMITMENT_PERIOD', 'STATIC_IP', 'MOBILE_DATA_PKG', 'TV_CHANNEL_PKG');
