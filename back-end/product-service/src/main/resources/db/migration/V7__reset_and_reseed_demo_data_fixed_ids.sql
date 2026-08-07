-- V7__reset_and_reseed_demo_data_fixed_ids.sql (product-service)
-- ONCEKI DURUM: V4/V5, prod_spec/prod_catal/prod_ofr/... verilerini "WHERE NOT EXISTS" ile
-- idempotent seed ediyordu, ama bu ortamda V4'ten ONCE elle olusturulmus 2 test satiri
-- (prod_ofr id=1 'Fiber 100Mbps', id=2 'Fiber 50Mbps') oldugu icin gercek ID'ler V4'un
-- script sirasindan sapmisti (offering'ler id=3..25 araliginda cikmisti, id=1..23 degil).
--
-- Bu migration, asagidaki tablolardaki TUM satirlari SILER (elle eklenmis olanlar dahil)
-- ve ayni demo veriyi SABIT, deterministik ID'lerle yeniden ekler - boylece bu migration
-- hangi bilgisayarda/ortamda calisirsa calissin HER ZAMAN AYNI ID'leri uretir ve baska
-- servislerdeki/dokumanlardaki referanslarla (bkz. lookup-service
-- V11__reset_and_reseed_char_demo_data_fixed_ids.sql) tutarli kalir.
--
-- UYARI: prod_spec, prod_catal, cmpg, prod_ofr, prod_catal_prod_ofr, prod_ofr_rel,
-- cmpg_prod_ofr, prod, prod_rel, prod_char_val, prod_ofr_char_use tablolarindaki
-- TUM mevcut kayitlar silinir (sadece V4/V5 demo verisi degil).
--
-- char_id / char_val_id degerleri lookup-service V11'den (bu migration'la birlikte yazildi):
--   char_id: 1=CONN_SPEED 2=CONN_TYPE 3=COMMITMENT_PERIOD 4=STATIC_IP 5=MOBILE_DATA_PKG 6=TV_CHANNEL_PKG
--   char_val_id: 1-10=CONN_SPEED(8/16/35/50/100/200/300/500Mbps,1/2Gbps sirayla)
--                11=Fiber 12=ADSL 13=VDSL 14=Kablosuz 15=Uydu
--                16=12Ay 17=24Ay 18=Taahhütsüz  19=StatikIP-Var 20=StatikIP-Yok
--                21=20GB 22=50GB 23=100GB 24=Sınırsız  25=TV-Temel 26=TV-Standart 27=TV-Premium
-- st_id/rel_tp_id degerleri (GNL_ST/GNL_TP genel lookup, V5__seed_general_lookup_data.sql -
-- bu migration'in kapsami DISINDA, degistirilmedi):
--   10=PROD_SPEC/ACTV 70=PROD_OFR/ACTV 34=PROD_CATAL/ACTV 255=CMPG/ACTV 177=PROD_CATAL_PROD_OFR/ACTV
--   592=PROD_OFR_REL/MANDATORY 593=PROD_OFR_REL/OPTIONAL 116=PROD/ACTV 124=PROD_CHAR_VAL/ACTV
--   591=PROD_REL/PRNTPROD

-- =========================================================
-- 0) Eski veriyi sil (cocuktan ataya dogru, FK sirasina uygun)
-- =========================================================
DELETE FROM prod_ofr_char_use;
DELETE FROM prod_char_val;
DELETE FROM prod_rel;
DELETE FROM prod;
DELETE FROM cmpg_prod_ofr;
DELETE FROM prod_catal_prod_ofr;
DELETE FROM prod_ofr_rel;
DELETE FROM cmpg;
DELETE FROM prod_spec_rsrc_spec;
DELETE FROM prod_spec_srvc_spec;
DELETE FROM prod_ofr;
DELETE FROM prod_catal;
DELETE FROM prod_spec;

-- =========================================================
-- 1) PROD_SPEC - sabit ID 1-6
-- =========================================================
INSERT INTO prod_spec (prod_spec_id, name, descr, st_id, is_dev, cuser) VALUES
    (1, 'Fiber İnternet Ürün Tanımı', 'Ev tipi fiber internet hizmetleri için ürün spesifikasyonu', 10, false, 'system'),
    (2, 'CPE Ekipmanı', 'Müşteri tarafında kurulan fiber modem/router donanımı', 10, false, 'system'),
    (3, 'Bakır Hat İnternet Ürün Tanımı', 'ADSL/VDSL bakır altyapı internet hizmetleri', 10, false, 'system'),
    (4, 'Kablosuz İnternet Ürün Tanımı', 'Kablosuz/uydu tabanlı internet hizmetleri', 10, false, 'system'),
    (5, 'Mobil Hat Ürün Tanımı', 'Mobil hat ve paket ürün spesifikasyonu', 10, false, 'system'),
    (6, 'TV Ürün Tanımı', 'Dijital TV yayın hizmetleri ürün spesifikasyonu', 10, false, 'system');

-- =========================================================
-- 2) PROD_CATAL - sabit ID 1-3
-- =========================================================
INSERT INTO prod_catal (prod_catal_id, name, descr, st_id, shrt_code, cuser) VALUES
    (1, 'Internet', 'Ev ve işyeri internet ürünlerinin listelendiği katalog', 34, 'INTERNET', 'system'),
    (2, 'Mobile', 'Mobil hat ürünlerinin listelendiği katalog', 34, 'MOBILE', 'system'),
    (3, 'TV', 'Dijital TV ürünlerinin listelendiği katalog', 34, 'TV', 'system');

-- =========================================================
-- 3) PROD_OFR - sabit ID 1-23
-- =========================================================
INSERT INTO prod_ofr (prod_ofr_id, prod_spec_id, name, descr, st_id, prod_ofr_total_prc, cuser) VALUES
    (1, 1, 'Home Fiber 200Mbps', 'Ücretsiz modemli, 24 ay taahhütlü fiber internet paketi', 70, 399.90, 'system'),
    (2, 1, 'Home Fiber 100Mbps', 'Ücretsiz modemli, 24 ay taahhütlü fiber internet paketi', 70, 299.90, 'system'),
    (3, 1, 'Home Fiber 500Mbps', 'Yüksek hızlı, ücretsiz modemli fiber internet paketi', 70, 549.90, 'system'),
    (4, 1, 'Home Fiber 1Gbps', 'Gigabit hızında fiber internet paketi, 24 ay taahhütlü', 70, 699.90, 'system'),
    (5, 1, 'Home Fiber 2Gbps', 'Çoklu cihaz kullanımı için ultra hızlı fiber internet', 70, 899.90, 'system'),
    (6, 1, 'Home Internet + TV Bundle', 'Fiber internet ve dijital TV paketi bir arada', 70, 449.90, 'system'),
    (7, 1, 'Home Internet + Phone Bundle', 'Fiber internet ve ev telefonu hattı bir arada', 70, 429.90, 'system'),
    (8, 1, 'Business Fiber 300Mbps', 'Küçük ofisler için özel fiber internet paketi', 70, 599.90, 'system'),
    (9, 1, 'Business Fiber 1Gbps Dedicated', 'Kurumsal SLA''lı adanmış gigabit fiber hattı', 70, 1199.90, 'system'),
    (10, 1, 'Internet + Static IP', 'Sabit IP adresli fiber internet paketi', 70, 479.90, 'system'),
    (11, 3, 'Home ADSL 8Mbps', 'Hafif ev kullanımı için bakır hat internet', 70, 179.90, 'system'),
    (12, 3, 'Home ADSL 16Mbps', 'Günlük ev kullanımı için bakır hat internet', 70, 209.90, 'system'),
    (13, 3, 'Home VDSL 35Mbps', 'Yüksek hızlı bakır hat internet paketi', 70, 259.90, 'system'),
    (14, 3, 'Business VDSL 50Mbps', 'Küçük ofisler için yüksek hızlı bakır hat internet', 70, 349.90, 'system'),
    (15, 4, 'Home Wireless Internet 50Mbps', 'Kablolama gerektirmeyen sabit kablosuz internet', 70, 319.90, 'system'),
    (16, 4, 'Rural Satellite Internet', 'Fiber altyapısı olmayan bölgeler için uydu internet', 70, 279.90, 'system'),
    (17, 4, 'Student Home Internet 50Mbps', 'Öğrenci evleri için indirimli internet paketi', 70, 229.90, 'system'),
    (18, 2, 'Broadband Modem', 'Fiber/DSL bağlantıları için gerekli modem', 70, 1199.90, 'system'),
    (19, 2, 'Wi-Fi Router Purchase', 'Çift bantlı Wi-Fi 6 router, 2 yıl garantili tek seferlik satın alma', 70, 2499.90, 'system'),
    (20, 2, 'Smart Home Hub', 'IoT SIM ile çalışan akıllı ev merkezi cihazı', 70, 899.90, 'system'),
    (21, 2, 'Home Mesh Wi-Fi Add-on', 'Ev geneli mesh Wi-Fi genişletme kiti, tek seferlik satın alma', 70, 799.90, 'system'),
    (22, 5, 'Mobile Postpaid 50GB', 'Aylık 50GB internet içeren faturalı mobil hat paketi', 70, 249.90, 'system'),
    (23, 6, 'Digital TV Basic Package', 'Temel dijital TV kanal paketi', 70, 179.90, 'system');

-- =========================================================
-- 4) PROD_CATAL_PROD_OFR - sabit ID 1-23
-- =========================================================
INSERT INTO prod_catal_prod_ofr (prod_catal_prod_ofr_id, prod_catal_id, prod_ofr_id, st_id, cuser) VALUES
    (1, 1, 1, 177, 'system'),   (2, 1, 2, 177, 'system'),   (3, 1, 3, 177, 'system'),
    (4, 1, 4, 177, 'system'),   (5, 1, 5, 177, 'system'),   (6, 1, 6, 177, 'system'),
    (7, 1, 7, 177, 'system'),   (8, 1, 8, 177, 'system'),   (9, 1, 9, 177, 'system'),
    (10, 1, 10, 177, 'system'), (11, 1, 11, 177, 'system'), (12, 1, 12, 177, 'system'),
    (13, 1, 13, 177, 'system'), (14, 1, 14, 177, 'system'), (15, 1, 15, 177, 'system'),
    (16, 1, 16, 177, 'system'), (17, 1, 17, 177, 'system'), (18, 1, 18, 177, 'system'),
    (19, 1, 19, 177, 'system'), (20, 1, 20, 177, 'system'), (21, 1, 21, 177, 'system'),
    (22, 2, 22, 177, 'system'), -- Mobile katalogu -> Mobile Postpaid 50GB
    (23, 3, 23, 177, 'system'); -- TV katalogu -> Digital TV Basic Package

-- =========================================================
-- 5) PROD_OFR_REL - sabit ID 1-8 (zorunlu/opsiyonel ek ürünler)
-- =========================================================
INSERT INTO prod_ofr_rel (prod_ofr_rel_id, prod_ofr_id1, prod_ofr_id2, rel_tp_id, qty, is_actv, cuser) VALUES
    (1, 1, 18, 592, 1, true, 'system'),  -- Home Fiber 200Mbps -> Broadband Modem (zorunlu)
    (2, 1, 19, 592, 1, true, 'system'),  -- Home Fiber 200Mbps -> Wi-Fi Router Purchase (zorunlu)
    (3, 2, 18, 592, 1, true, 'system'),  -- Home Fiber 100Mbps -> Broadband Modem (zorunlu)
    (4, 3, 18, 592, 1, true, 'system'),  -- Home Fiber 500Mbps -> Broadband Modem (zorunlu)
    (5, 4, 18, 592, 1, true, 'system'),  -- Home Fiber 1Gbps -> Broadband Modem (zorunlu)
    (6, 5, 18, 592, 1, true, 'system'),  -- Home Fiber 2Gbps -> Broadband Modem (zorunlu)
    (7, 15, 20, 593, 1, true, 'system'), -- Home Wireless Internet 50Mbps -> Smart Home Hub (opsiyonel)
    (8, 8, 19, 593, 1, true, 'system');  -- Business Fiber 300Mbps -> Wi-Fi Router Purchase (opsiyonel)

-- =========================================================
-- 6) CMPG - sabit ID 1-10
-- =========================================================
INSERT INTO cmpg (cmpg_id, name, descr, cmpg_code, actvt_edate, st_id, is_penalty, cuser) VALUES
    (1, 'Home Starter Bundle', 'Yeni ev aboneleri için başlangıç paketi', 'HOME_STARTER', '2026-12-31', 255, false, 'system'),
    (2, 'Fiber + Mobile + TV Combo', 'Fiber, mobil ve TV''nin bir arada olduğu kombine paket', 'FIBER_MOBILE_TV_COMBO', '2026-12-31', 255, true, 'system'),
    (3, 'Work From Home Bundle', 'Evden çalışanlar için internet ve ekipman paketi', 'WFH_BUNDLE', '2026-12-31', 255, false, 'system'),
    (4, 'Small Business Fiber Bundle', 'Küçük işletmeler için fiber ve donanım paketi', 'SMB_FIBER_BUNDLE', '2026-12-31', 255, true, 'system'),
    (5, 'Smart Home Bundle', 'Akıllı ev cihazlarıyla genişletilmiş fiber paketi', 'SMART_HOME_BUNDLE', '2026-12-31', 255, false, 'system'),
    (6, 'Loyalty Reward Bundle', 'Sadık müşteriler için yüksek indirimli üst seviye paket', 'LOYALTY_REWARD_BUNDLE', '2026-12-31', 255, false, 'system'),
    (7, 'Winter Warmth Bundle', 'Kış dönemine özel internet ve TV paketi', 'WINTER_WARMTH_BUNDLE', '2026-12-31', 255, false, 'system'),
    (8, 'Remote Worker Bundle', 'Uzaktan çalışanlar için kurumsal fiber paketi', 'REMOTE_WORKER_BUNDLE', '2026-12-31', 255, true, 'system'),
    (9, 'Neighborhood Fiber Bundle', 'Mahalle bazlı tanıtım kampanyası paketi', 'NEIGHBORHOOD_FIBER_BUNDLE', '2026-12-31', 255, false, 'system'),
    (10, 'Device Upgrade Bundle', 'Mevcut müşteriler için cihaz yükseltme kampanyası', 'DEVICE_UPGRADE_BUNDLE', '2026-12-31', 255, false, 'system');

-- =========================================================
-- 7) CMPG_PROD_OFR - sabit ID 1-24
-- =========================================================
INSERT INTO cmpg_prod_ofr (cmpg_prod_ofr_id, cmpg_id, prod_ofr_id, prod_ofr_name, prio, sdate, edate, is_actv, discount_pct, cuser) VALUES
    (1, 1, 2, 'Home Fiber 100Mbps', 1, '2026-06-01', '2026-12-31', true, 10, 'system'),
    (2, 1, 18, 'Broadband Modem', 1, '2026-06-01', '2026-12-31', true, 10, 'system'),
    (3, 2, 22, 'Mobile Postpaid 50GB', 1, '2026-06-01', '2026-12-31', true, 15, 'system'),
    (4, 2, 2, 'Home Fiber 100Mbps', 1, '2026-06-01', '2026-12-31', true, 15, 'system'),
    (5, 2, 23, 'Digital TV Basic Package', 1, '2026-06-01', '2026-12-31', true, 15, 'system'),
    (6, 3, 3, 'Home Fiber 500Mbps', 1, '2026-06-01', '2026-12-31', true, 10, 'system'),
    (7, 3, 19, 'Wi-Fi Router Purchase', 1, '2026-06-01', '2026-12-31', true, 10, 'system'),
    (8, 3, 20, 'Smart Home Hub', 1, '2026-06-01', '2026-12-31', true, 10, 'system'),
    (9, 4, 8, 'Business Fiber 300Mbps', 1, '2026-06-01', '2026-12-31', true, 12, 'system'),
    (10, 4, 18, 'Broadband Modem', 1, '2026-06-01', '2026-12-31', true, 12, 'system'),
    (11, 5, 1, 'Home Fiber 200Mbps', 1, '2026-06-01', '2026-12-31', true, 8, 'system'),
    (12, 5, 20, 'Smart Home Hub', 1, '2026-06-01', '2026-12-31', true, 8, 'system'),
    (13, 5, 21, 'Home Mesh Wi-Fi Add-on', 1, '2026-06-01', '2026-12-31', true, 8, 'system'),
    (14, 5, 19, 'Wi-Fi Router Purchase', 1, '2026-06-01', '2026-12-31', true, 8, 'system'),
    (15, 6, 4, 'Home Fiber 1Gbps', 1, '2026-06-01', '2026-12-31', true, 20, 'system'),
    (16, 6, 18, 'Broadband Modem', 1, '2026-06-01', '2026-12-31', true, 20, 'system'),
    (17, 7, 6, 'Home Internet + TV Bundle', 1, '2026-11-01', '2027-02-28', true, 10, 'system'),
    (18, 7, 23, 'Digital TV Basic Package', 1, '2026-11-01', '2027-02-28', true, 10, 'system'),
    (19, 8, 9, 'Business Fiber 1Gbps Dedicated', 1, '2026-06-01', '2026-12-31', true, 5, 'system'),
    (20, 8, 19, 'Wi-Fi Router Purchase', 1, '2026-06-01', '2026-12-31', true, 5, 'system'),
    (21, 9, 2, 'Home Fiber 100Mbps', 1, '2026-06-01', '2026-12-31', true, 10, 'system'),
    (22, 9, 21, 'Home Mesh Wi-Fi Add-on', 1, '2026-06-01', '2026-12-31', true, 10, 'system'),
    (23, 10, 19, 'Wi-Fi Router Purchase', 1, '2026-06-01', '2026-12-31', true, 90, 'system'),
    (24, 10, 20, 'Smart Home Hub', 1, '2026-06-01', '2026-12-31', true, 90, 'system');

-- =========================================================
-- 8) PROD - sabit ID 1-8 (örnek ürün örnekleri, name bilerek bos - bkz. eski V5 notu)
-- =========================================================
INSERT INTO prod (prod_id, prod_ofr_id, prod_spec_id, descr, st_id, cuser) VALUES
    (1, 2, 1, 'Home Fiber 100Mbps - örnek abonelik', 116, 'system'),
    (2, 18, 2, 'Home Fiber 100Mbps aboneliğine bağlı örnek modem', 116, 'system'),
    (3, 8, 1, 'Business Fiber 300Mbps - örnek abonelik', 116, 'system'),
    (4, 12, 3, 'Home ADSL 16Mbps - örnek abonelik', 116, 'system'),
    (5, 15, 4, 'Home Wireless Internet 50Mbps - örnek abonelik', 116, 'system'),
    (6, 22, 5, 'Mobile Postpaid 50GB - örnek abonelik', 116, 'system'),
    (7, 23, 6, 'Digital TV Basic Package - örnek abonelik', 116, 'system'),
    (8, 3, 1, 'Home Fiber 500Mbps - örnek abonelik', 116, 'system');

-- =========================================================
-- 9) PROD_REL - sabit ID 1 (Fiber hizmeti <-> modem)
-- =========================================================
INSERT INTO prod_rel (prod_rel_id, prod_id1, prod_id2, rel_tp_id, is_actv, cuser) VALUES
    (1, 1, 2, 591, true, 'system');

-- =========================================================
-- 10) PROD_CHAR_VAL - sabit ID 1-17 (her örnek aboneliğe uygun karakteristikler)
-- =========================================================
INSERT INTO prod_char_val (prod_char_val_id, prod_id, char_id, char_val_id, st_id, cuser) VALUES
    (1, 1, 1, 5, 124, 'system'),   (2, 1, 2, 11, 124, 'system'),  (3, 1, 3, 17, 124, 'system'),   -- Home Fiber 100Mbps
    (4, 3, 1, 7, 124, 'system'),   (5, 3, 2, 11, 124, 'system'),  (6, 3, 3, 17, 124, 'system'),    -- Business Fiber 300Mbps
    (7, 4, 1, 2, 124, 'system'),   (8, 4, 2, 12, 124, 'system'),  (9, 4, 3, 16, 124, 'system'),    -- Home ADSL 16Mbps
    (10, 5, 1, 4, 124, 'system'),  (11, 5, 2, 14, 124, 'system'), (12, 5, 4, 20, 124, 'system'),   -- Home Wireless Internet 50Mbps
    (13, 6, 5, 22, 124, 'system'),                                                                  -- Mobile Postpaid 50GB
    (14, 7, 6, 25, 124, 'system'),                                                                  -- Digital TV Basic Package
    (15, 8, 1, 8, 124, 'system'),  (16, 8, 2, 11, 124, 'system'), (17, 8, 3, 17, 124, 'system');   -- Home Fiber 500Mbps

-- =========================================================
-- 11) PROD_OFR_CHAR_USE - sabit ID 1-62 (offering'in hangi karakteristikleri zorunlu/opsiyonel
--     kullanabilecegini tanimlar; prod_char_val'in "somut deger" karsiligi olan "sablon" seviyesi)
--     CPE/donanim offering'leri (4,18,19,20,21) kasitli disarida - cihaz, baglanti
--     karakteristigi tasimaz.
-- =========================================================
INSERT INTO prod_ofr_char_use (prod_ofr_char_use_id, prod_ofr_id, char_id, is_mandatory, is_actv, cuser) VALUES
    -- Saf Fiber internet (1,2,3,4,5,8,9): hiz+tip+taahhut zorunlu, statik IP opsiyonel
    (1, 1, 1, true, true, 'system'),  (2, 1, 2, true, true, 'system'),  (3, 1, 3, true, true, 'system'),  (4, 1, 4, false, true, 'system'),
    (5, 2, 1, true, true, 'system'),  (6, 2, 2, true, true, 'system'),  (7, 2, 3, true, true, 'system'),  (8, 2, 4, false, true, 'system'),
    (9, 3, 1, true, true, 'system'),  (10, 3, 2, true, true, 'system'), (11, 3, 3, true, true, 'system'), (12, 3, 4, false, true, 'system'),
    (13, 4, 1, true, true, 'system'), (14, 4, 2, true, true, 'system'), (15, 4, 3, true, true, 'system'), (16, 4, 4, false, true, 'system'),
    (17, 5, 1, true, true, 'system'), (18, 5, 2, true, true, 'system'), (19, 5, 3, true, true, 'system'), (20, 5, 4, false, true, 'system'),
    (21, 8, 1, true, true, 'system'), (22, 8, 2, true, true, 'system'), (23, 8, 3, true, true, 'system'), (24, 8, 4, false, true, 'system'),
    (25, 9, 1, true, true, 'system'), (26, 9, 2, true, true, 'system'), (27, 9, 3, true, true, 'system'), (28, 9, 4, false, true, 'system'),
    -- Internet + Static IP (10): statik IP burada ZORUNLU
    (29, 10, 1, true, true, 'system'), (30, 10, 2, true, true, 'system'), (31, 10, 3, true, true, 'system'), (32, 10, 4, true, true, 'system'),
    -- Home Internet + TV Bundle (6): internet 3'lusu + TV kanal paketi, hepsi zorunlu
    (33, 6, 1, true, true, 'system'), (34, 6, 2, true, true, 'system'), (35, 6, 3, true, true, 'system'), (36, 6, 6, true, true, 'system'),
    -- Home Internet + Phone Bundle (7): sadece internet 3'lusu
    (37, 7, 1, true, true, 'system'), (38, 7, 2, true, true, 'system'), (39, 7, 3, true, true, 'system'),
    -- ADSL/VDSL (11,12,13,14): hiz+tip+taahhut zorunlu
    (40, 11, 1, true, true, 'system'), (41, 11, 2, true, true, 'system'), (42, 11, 3, true, true, 'system'),
    (43, 12, 1, true, true, 'system'), (44, 12, 2, true, true, 'system'), (45, 12, 3, true, true, 'system'),
    (46, 13, 1, true, true, 'system'), (47, 13, 2, true, true, 'system'), (48, 13, 3, true, true, 'system'),
    (49, 14, 1, true, true, 'system'), (50, 14, 2, true, true, 'system'), (51, 14, 3, true, true, 'system'),
    -- Kablosuz / Uydu (15,16,17): hiz+tip zorunlu, taahhut opsiyonel
    (52, 15, 1, true, true, 'system'), (53, 15, 2, true, true, 'system'), (54, 15, 3, false, true, 'system'),
    (55, 16, 1, true, true, 'system'), (56, 16, 2, true, true, 'system'), (57, 16, 3, false, true, 'system'),
    (58, 17, 1, true, true, 'system'), (59, 17, 2, true, true, 'system'), (60, 17, 3, false, true, 'system'),
    -- Mobil ve TV: kendi tekil karakteristikleri zorunlu
    (61, 22, 5, true, true, 'system'), -- Mobile Postpaid 50GB -> MOBILE_DATA_PKG
    (62, 23, 6, true, true, 'system'); -- Digital TV Basic Package -> TV_CHANNEL_PKG

-- =========================================================
-- 12) Sequence'leri son hardcoded ID'nin sonrasina tasi
-- =========================================================
SELECT setval(pg_get_serial_sequence('prod_spec', 'prod_spec_id'), 6, true);
SELECT setval(pg_get_serial_sequence('prod_catal', 'prod_catal_id'), 3, true);
SELECT setval(pg_get_serial_sequence('prod_ofr', 'prod_ofr_id'), 23, true);
SELECT setval(pg_get_serial_sequence('prod_catal_prod_ofr', 'prod_catal_prod_ofr_id'), 23, true);
SELECT setval(pg_get_serial_sequence('prod_ofr_rel', 'prod_ofr_rel_id'), 8, true);
SELECT setval(pg_get_serial_sequence('cmpg', 'cmpg_id'), 10, true);
SELECT setval(pg_get_serial_sequence('cmpg_prod_ofr', 'cmpg_prod_ofr_id'), 24, true);
SELECT setval(pg_get_serial_sequence('prod', 'prod_id'), 8, true);
SELECT setval(pg_get_serial_sequence('prod_rel', 'prod_rel_id'), 1, true);
SELECT setval(pg_get_serial_sequence('prod_char_val', 'prod_char_val_id'), 17, true);
SELECT setval(pg_get_serial_sequence('prod_ofr_char_use', 'prod_ofr_char_use_id'), 62, true);
