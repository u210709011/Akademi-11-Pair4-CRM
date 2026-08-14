-- cmpg.name icin Turkce ceviriler - bkz. V19 yorumu (ayni geri donusun cmpg icin uygulanmasi).
INSERT INTO translation (entity_name, entity_id, field_name, locale, value, cuser)
SELECT 'CMPG', cmpg_id, 'NAME', 'tr', CASE cmpg_code
        WHEN 'HOME_STARTER'             THEN 'Ev Başlangıç Paketi'
        WHEN 'FIBER_MOBILE_TV_COMBO'    THEN 'Fiber + Mobil + TV Kombo'
        WHEN 'WFH_BUNDLE'               THEN 'Evden Çalışma Paketi'
        WHEN 'SMB_FIBER_BUNDLE'         THEN 'Küçük İşletme Fiber Paketi'
        WHEN 'SMART_HOME_BUNDLE'        THEN 'Akıllı Ev Paketi'
        WHEN 'LOYALTY_REWARD_BUNDLE'    THEN 'Sadakat Ödül Paketi'
        WHEN 'WINTER_WARMTH_BUNDLE'     THEN 'Kış Sıcaklığı Paketi'
        WHEN 'REMOTE_WORKER_BUNDLE'     THEN 'Uzaktan Çalışan Paketi'
        WHEN 'NEIGHBORHOOD_FIBER_BUNDLE' THEN 'Mahalle Fiber Paketi'
        WHEN 'DEVICE_UPGRADE_BUNDLE'    THEN 'Cihaz Yükseltme Paketi'
    END, 'system'
FROM cmpg
WHERE cmpg_code IN ('HOME_STARTER', 'FIBER_MOBILE_TV_COMBO', 'WFH_BUNDLE', 'SMB_FIBER_BUNDLE',
                     'SMART_HOME_BUNDLE', 'LOYALTY_REWARD_BUNDLE', 'WINTER_WARMTH_BUNDLE',
                     'REMOTE_WORKER_BUNDLE', 'NEIGHBORHOOD_FIBER_BUNDLE', 'DEVICE_UPGRADE_BUNDLE');
