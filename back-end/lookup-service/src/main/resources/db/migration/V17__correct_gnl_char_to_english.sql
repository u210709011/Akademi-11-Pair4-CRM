-- V11'de gnl_char tamamen Turkce seed edilmisti. Bu uygulamada her string'in taban degeri
-- Ingilizce olmalidir (bkz. V15 yorumu) - Turkce artik V19'da translation tablosunda 'tr'
-- locale'i icin ayri satirlarla saglanir, taban deger burada duzeltilir.
UPDATE gnl_char SET name = 'Connection Speed', descr = 'Internet connection download speed' WHERE shrt_code = 'CONN_SPEED';
UPDATE gnl_char SET name = 'Connection Type', descr = 'Internet connection infrastructure type' WHERE shrt_code = 'CONN_TYPE';
UPDATE gnl_char SET name = 'Commitment Period', descr = 'Subscription commitment period' WHERE shrt_code = 'COMMITMENT_PERIOD';
UPDATE gnl_char SET name = 'Static IP', descr = 'Static IP address allocation status' WHERE shrt_code = 'STATIC_IP';
UPDATE gnl_char SET name = 'Mobile Data Package', descr = 'Monthly data quota for the mobile line' WHERE shrt_code = 'MOBILE_DATA_PKG';
UPDATE gnl_char SET name = 'TV Channel Package', descr = 'Digital TV channel package tier' WHERE shrt_code = 'TV_CHANNEL_PKG';
