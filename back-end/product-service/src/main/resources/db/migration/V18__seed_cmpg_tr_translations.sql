-- cmpg.descr icin Turkce ceviriler - V7'deki orijinal Turkce metin, V14'te Ingilizceye cevrilen
-- taban degerin 'tr' locale'indeki karsiligi. id hardcode edilmez, cmpg_code'dan INSERT...SELECT
-- ile bulunur. name zaten Ingilizce'ydi, ceviri gerekmiyor.
INSERT INTO translation (entity_name, entity_id, field_name, locale, value, cuser)
SELECT 'CMPG', cmpg_id, 'DESCR', 'tr', CASE cmpg_code
        WHEN 'HOME_STARTER' THEN 'Yeni ev aboneleri için başlangıç paketi'
        WHEN 'FIBER_MOBILE_TV_COMBO' THEN 'Fiber, mobil ve TV''nin bir arada olduğu kombine paket'
        WHEN 'WFH_BUNDLE' THEN 'Evden çalışanlar için internet ve ekipman paketi'
        WHEN 'SMB_FIBER_BUNDLE' THEN 'Küçük işletmeler için fiber ve donanım paketi'
        WHEN 'SMART_HOME_BUNDLE' THEN 'Akıllı ev cihazlarıyla genişletilmiş fiber paketi'
        WHEN 'LOYALTY_REWARD_BUNDLE' THEN 'Sadık müşteriler için yüksek indirimli üst seviye paket'
        WHEN 'WINTER_WARMTH_BUNDLE' THEN 'Kış dönemine özel internet ve TV paketi'
        WHEN 'REMOTE_WORKER_BUNDLE' THEN 'Uzaktan çalışanlar için kurumsal fiber paketi'
        WHEN 'NEIGHBORHOOD_FIBER_BUNDLE' THEN 'Mahalle bazlı tanıtım kampanyası paketi'
        WHEN 'DEVICE_UPGRADE_BUNDLE' THEN 'Mevcut müşteriler için cihaz yükseltme kampanyası'
    END, 'system'
FROM cmpg
WHERE cmpg_code IN ('HOME_STARTER', 'FIBER_MOBILE_TV_COMBO', 'WFH_BUNDLE', 'SMB_FIBER_BUNDLE',
                     'SMART_HOME_BUNDLE', 'LOYALTY_REWARD_BUNDLE', 'WINTER_WARMTH_BUNDLE',
                     'REMOTE_WORKER_BUNDLE', 'NEIGHBORHOOD_FIBER_BUNDLE', 'DEVICE_UPGRADE_BUNDLE');
