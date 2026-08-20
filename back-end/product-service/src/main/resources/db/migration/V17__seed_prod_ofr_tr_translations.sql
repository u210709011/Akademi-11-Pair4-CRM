-- prod_ofr.descr icin Turkce ceviriler - V7'deki orijinal Turkce metin, V13'te Ingilizceye
-- cevrilen taban degerin 'tr' locale'indeki karsiligi. id hardcode edilmez, name (bu tabloda
-- tekil) uzerinden INSERT...SELECT ile bulunur. name zaten Ingilizce'ydi, ceviri gerekmiyor.
INSERT INTO translation (entity_name, entity_id, field_name, locale, value, cuser)
SELECT 'PROD_OFR', prod_ofr_id, 'DESCR', 'tr', CASE name
        WHEN 'Home Fiber 200Mbps' THEN 'Ücretsiz modemli, 24 ay taahhütlü fiber internet paketi'
        WHEN 'Home Fiber 100Mbps' THEN 'Ücretsiz modemli, 24 ay taahhütlü fiber internet paketi'
        WHEN 'Home Fiber 500Mbps' THEN 'Yüksek hızlı, ücretsiz modemli fiber internet paketi'
        WHEN 'Home Fiber 1Gbps' THEN 'Gigabit hızında fiber internet paketi, 24 ay taahhütlü'
        WHEN 'Home Fiber 2Gbps' THEN 'Çoklu cihaz kullanımı için ultra hızlı fiber internet'
        WHEN 'Home Internet + TV Bundle' THEN 'Fiber internet ve dijital TV paketi bir arada'
        WHEN 'Home Internet + Phone Bundle' THEN 'Fiber internet ve ev telefonu hattı bir arada'
        WHEN 'Business Fiber 300Mbps' THEN 'Küçük ofisler için özel fiber internet paketi'
        WHEN 'Business Fiber 1Gbps Dedicated' THEN 'Kurumsal SLA''lı adanmış gigabit fiber hattı'
        WHEN 'Internet + Static IP' THEN 'Sabit IP adresli fiber internet paketi'
        WHEN 'Home ADSL 8Mbps' THEN 'Hafif ev kullanımı için bakır hat internet'
        WHEN 'Home ADSL 16Mbps' THEN 'Günlük ev kullanımı için bakır hat internet'
        WHEN 'Home VDSL 35Mbps' THEN 'Yüksek hızlı bakır hat internet paketi'
        WHEN 'Business VDSL 50Mbps' THEN 'Küçük ofisler için yüksek hızlı bakır hat internet'
        WHEN 'Home Wireless Internet 50Mbps' THEN 'Kablolama gerektirmeyen sabit kablosuz internet'
        WHEN 'Rural Satellite Internet' THEN 'Fiber altyapısı olmayan bölgeler için uydu internet'
        WHEN 'Student Home Internet 50Mbps' THEN 'Öğrenci evleri için indirimli internet paketi'
        WHEN 'Broadband Modem' THEN 'Fiber/DSL bağlantıları için gerekli modem'
        WHEN 'Wi-Fi Router Purchase' THEN 'Çift bantlı Wi-Fi 6 router, 2 yıl garantili tek seferlik satın alma'
        WHEN 'Smart Home Hub' THEN 'IoT SIM ile çalışan akıllı ev merkezi cihazı'
        WHEN 'Home Mesh Wi-Fi Add-on' THEN 'Ev geneli mesh Wi-Fi genişletme kiti, tek seferlik satın alma'
        WHEN 'Mobile Postpaid 50GB' THEN 'Aylık 50GB internet içeren faturalı mobil hat paketi'
        WHEN 'Digital TV Basic Package' THEN 'Temel dijital TV kanal paketi'
    END, 'system'
FROM prod_ofr
WHERE name IN ('Home Fiber 200Mbps', 'Home Fiber 100Mbps', 'Home Fiber 500Mbps', 'Home Fiber 1Gbps',
               'Home Fiber 2Gbps', 'Home Internet + TV Bundle', 'Home Internet + Phone Bundle',
               'Business Fiber 300Mbps', 'Business Fiber 1Gbps Dedicated', 'Internet + Static IP',
               'Home ADSL 8Mbps', 'Home ADSL 16Mbps', 'Home VDSL 35Mbps', 'Business VDSL 50Mbps',
               'Home Wireless Internet 50Mbps', 'Rural Satellite Internet', 'Student Home Internet 50Mbps',
               'Broadband Modem', 'Wi-Fi Router Purchase', 'Smart Home Hub', 'Home Mesh Wi-Fi Add-on',
               'Mobile Postpaid 50GB', 'Digital TV Basic Package');
