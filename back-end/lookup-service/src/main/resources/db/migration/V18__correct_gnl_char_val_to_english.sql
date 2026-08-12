-- V11'de gnl_char_val icindeki bazi degerler Turkce kelimeydi (ör. 'Kablosuz', 'Sınırsız').
-- Zaten Ingilizce/teknik olanlar (Mbps/Gbps hizlari, Fiber/ADSL/VDSL, GB degerleri) degistirilmedi.
-- Turkce karsiliklari V20'de translation tablosuna eklenir.
UPDATE gnl_char_val SET val = 'Wireless' WHERE shrt_code = 'WIRELESS';
UPDATE gnl_char_val SET val = 'Satellite' WHERE shrt_code = 'SATELLITE';
UPDATE gnl_char_val SET val = '12 Months' WHERE shrt_code = '12AY';
UPDATE gnl_char_val SET val = '24 Months' WHERE shrt_code = '24AY';
UPDATE gnl_char_val SET val = 'No Commitment' WHERE shrt_code = 'NO_COMMIT';
UPDATE gnl_char_val SET val = 'Yes' WHERE shrt_code = 'STATIC_IP_YES';
UPDATE gnl_char_val SET val = 'No' WHERE shrt_code = 'STATIC_IP_NO';
UPDATE gnl_char_val SET val = 'Unlimited' WHERE shrt_code = 'DATA_UNLIMITED';
UPDATE gnl_char_val SET val = 'Basic' WHERE shrt_code = 'TV_BASIC';
UPDATE gnl_char_val SET val = 'Standard' WHERE shrt_code = 'TV_STANDARD';
