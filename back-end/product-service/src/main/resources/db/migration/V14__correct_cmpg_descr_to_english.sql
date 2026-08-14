-- cmpg.name zaten Ingilizce'ydi (bkz. V7) - sadece descr Turkce'ydi, burada duzeltiliyor.
-- cmpg_code dogal anahtar olarak kullanilir (id hardcode edilmez).
UPDATE cmpg SET descr = 'Starter package for new home subscribers' WHERE cmpg_code = 'HOME_STARTER';
UPDATE cmpg SET descr = 'Combined package bundling fiber, mobile, and TV' WHERE cmpg_code = 'FIBER_MOBILE_TV_COMBO';
UPDATE cmpg SET descr = 'Internet and equipment package for remote workers' WHERE cmpg_code = 'WFH_BUNDLE';
UPDATE cmpg SET descr = 'Fiber and hardware package for small businesses' WHERE cmpg_code = 'SMB_FIBER_BUNDLE';
UPDATE cmpg SET descr = 'Fiber package extended with smart home devices' WHERE cmpg_code = 'SMART_HOME_BUNDLE';
UPDATE cmpg SET descr = 'High-discount premium package for loyal customers' WHERE cmpg_code = 'LOYALTY_REWARD_BUNDLE';
UPDATE cmpg SET descr = 'Winter-season internet and TV package' WHERE cmpg_code = 'WINTER_WARMTH_BUNDLE';
UPDATE cmpg SET descr = 'Corporate fiber package for remote workers' WHERE cmpg_code = 'REMOTE_WORKER_BUNDLE';
UPDATE cmpg SET descr = 'Neighborhood-based promotional campaign package' WHERE cmpg_code = 'NEIGHBORHOOD_FIBER_BUNDLE';
UPDATE cmpg SET descr = 'Device upgrade campaign for existing customers' WHERE cmpg_code = 'DEVICE_UPGRADE_BUNDLE';
