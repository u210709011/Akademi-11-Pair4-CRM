-- prod_ofr.name icin Turkce ceviriler - bkz. V19 yorumu (ayni geri donusun prod_ofr icin
-- uygulanmasi). name bu tabloda tekil oldugu icin dogal anahtar olarak kullanilir.
INSERT INTO translation (entity_name, entity_id, field_name, locale, value, cuser)
SELECT 'PROD_OFR', prod_ofr_id, 'NAME', 'tr', CASE name
        WHEN 'Home Fiber 200Mbps' THEN 'Ev Fiber 200Mbps'
        WHEN 'Home Fiber 100Mbps' THEN 'Ev Fiber 100Mbps'
        WHEN 'Home Fiber 500Mbps' THEN 'Ev Fiber 500Mbps'
        WHEN 'Home Fiber 1Gbps' THEN 'Ev Fiber 1Gbps'
        WHEN 'Home Fiber 2Gbps' THEN 'Ev Fiber 2Gbps'
        WHEN 'Home Internet + TV Bundle' THEN 'Ev İnternet + TV Paketi'
        WHEN 'Home Internet + Phone Bundle' THEN 'Ev İnternet + Telefon Paketi'
        WHEN 'Business Fiber 300Mbps' THEN 'İş Yeri Fiber 300Mbps'
        WHEN 'Business Fiber 1Gbps Dedicated' THEN 'İş Yeri Fiber 1Gbps Adanmış Hat'
        WHEN 'Internet + Static IP' THEN 'İnternet + Statik IP'
        WHEN 'Home ADSL 8Mbps' THEN 'Ev ADSL 8Mbps'
        WHEN 'Home ADSL 16Mbps' THEN 'Ev ADSL 16Mbps'
        WHEN 'Home VDSL 35Mbps' THEN 'Ev VDSL 35Mbps'
        WHEN 'Business VDSL 50Mbps' THEN 'İş Yeri VDSL 50Mbps'
        WHEN 'Home Wireless Internet 50Mbps' THEN 'Ev Kablosuz İnternet 50Mbps'
        WHEN 'Rural Satellite Internet' THEN 'Kırsal Uydu İnternet'
        WHEN 'Student Home Internet 50Mbps' THEN 'Öğrenci Evi İnternet 50Mbps'
        WHEN 'Broadband Modem' THEN 'Geniş Bant Modem'
        WHEN 'Wi-Fi Router Purchase' THEN 'Wi-Fi Router Satın Alma'
        WHEN 'Smart Home Hub' THEN 'Akıllı Ev Merkezi'
        WHEN 'Home Mesh Wi-Fi Add-on' THEN 'Ev Mesh Wi-Fi Eklentisi'
        WHEN 'Mobile Postpaid 50GB' THEN 'Faturalı Mobil 50GB'
        WHEN 'Digital TV Basic Package' THEN 'Dijital TV Temel Paket'
    END, 'system'
FROM prod_ofr
WHERE name IN ('Home Fiber 200Mbps', 'Home Fiber 100Mbps', 'Home Fiber 500Mbps', 'Home Fiber 1Gbps',
               'Home Fiber 2Gbps', 'Home Internet + TV Bundle', 'Home Internet + Phone Bundle',
               'Business Fiber 300Mbps', 'Business Fiber 1Gbps Dedicated', 'Internet + Static IP',
               'Home ADSL 8Mbps', 'Home ADSL 16Mbps', 'Home VDSL 35Mbps', 'Business VDSL 50Mbps',
               'Home Wireless Internet 50Mbps', 'Rural Satellite Internet', 'Student Home Internet 50Mbps',
               'Broadband Modem', 'Wi-Fi Router Purchase', 'Smart Home Hub', 'Home Mesh Wi-Fi Add-on',
               'Mobile Postpaid 50GB', 'Digital TV Basic Package');
