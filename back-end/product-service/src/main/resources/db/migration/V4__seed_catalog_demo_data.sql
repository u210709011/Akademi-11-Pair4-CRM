-- V4__seed_catalog_demo_data.sql
-- Frontend ekibinin gelistirme/test amacli kullanmasi icin genis, gercekci demo veri seti.
-- Mockap ekran goruntulerine dayanir: Internet/Mobile/TV kataloglari, zorunlu ek urun
-- iliskileri (router/modem), 10 farkli kampanya bundle'i.
--
-- ONEMLI: Bu script IDEMPOTENT'tir - her tablo icin "WHERE NOT EXISTS (... isimle ara)"
-- korumasi var, yani zaten manuel test sirasinda olusturulmus ayni isimli kayitlar
-- TEKRAR eklenmez, sadece eksik olanlar eklenir. Bu yuzden hem sifir bir ortamda hem de
-- daha once elle test verisi eklenmis bir ortamda (mevcut dev DB gibi) guvenle calisir.
--
-- Lookup-service'e ait id'ler (st_id, rel_tp_id) sabit yazilmistir - CUNKU bunlar
-- product-service'in kendi DB'sinde degil, ayri bir serviste yasiyor, subquery ile
-- bulunamaz. Anlamlari:
--   10  = GNL_ST / PROD_SPEC           / ACTV
--   70  = GNL_ST / PROD_OFR            / ACTV
--   34  = GNL_ST / PROD_CATAL          / ACTV
--   255 = GNL_ST / CMPG                / ACTV
--   177 = GNL_ST / PROD_CATAL_PROD_OFR / ACTV
--   592 = GNL_TP / PROD_OFR_REL        / MANDATORY
--   593 = GNL_TP / PROD_OFR_REL        / OPTIONAL

-- =========================================================
-- 1) PRODUCT SPEC'ler (2 tanesi zaten olabilir, 4 yenisi ekleniyor)
-- =========================================================

INSERT INTO prod_spec (name, descr, st_id, is_dev, cuser)
SELECT 'Fiber İnternet Ürün Tanımı', 'Ev tipi fiber internet hizmetleri için ürün spesifikasyonu', 10, false, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_spec WHERE name = 'Fiber İnternet Ürün Tanımı');

INSERT INTO prod_spec (name, descr, st_id, is_dev, cuser)
SELECT 'CPE Ekipmanı', 'Müşteri tarafında kurulan fiber modem/router donanımı', 10, false, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_spec WHERE name = 'CPE Ekipmanı');

INSERT INTO prod_spec (name, descr, st_id, is_dev, cuser)
SELECT 'Bakır Hat İnternet Ürün Tanımı', 'ADSL/VDSL bakır altyapı internet hizmetleri', 10, false, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_spec WHERE name = 'Bakır Hat İnternet Ürün Tanımı');

INSERT INTO prod_spec (name, descr, st_id, is_dev, cuser)
SELECT 'Kablosuz İnternet Ürün Tanımı', 'Kablosuz/uydu tabanlı internet hizmetleri', 10, false, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_spec WHERE name = 'Kablosuz İnternet Ürün Tanımı');

INSERT INTO prod_spec (name, descr, st_id, is_dev, cuser)
SELECT 'Mobil Hat Ürün Tanımı', 'Mobil hat ve paket ürün spesifikasyonu', 10, false, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_spec WHERE name = 'Mobil Hat Ürün Tanımı');

INSERT INTO prod_spec (name, descr, st_id, is_dev, cuser)
SELECT 'TV Ürün Tanımı', 'Dijital TV yayın hizmetleri ürün spesifikasyonu', 10, false, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_spec WHERE name = 'TV Ürün Tanımı');

-- =========================================================
-- 2) PRODUCT CATALOG'lar (Internet zaten olabilir, Mobile/TV yeni)
-- =========================================================

INSERT INTO prod_catal (name, descr, st_id, shrt_code, cuser)
SELECT 'Internet', 'Ev ve işyeri internet ürünlerinin listelendiği katalog', 34, 'INTERNET', 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_catal WHERE name = 'Internet');

INSERT INTO prod_catal (name, descr, st_id, shrt_code, cuser)
SELECT 'Mobile', 'Mobil hat ürünlerinin listelendiği katalog', 34, 'MOBILE', 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_catal WHERE name = 'Mobile');

INSERT INTO prod_catal (name, descr, st_id, shrt_code, cuser)
SELECT 'TV', 'Dijital TV ürünlerinin listelendiği katalog', 34, 'TV', 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_catal WHERE name = 'TV');

-- =========================================================
-- 3) PRODUCT OFFERING'ler (23 adet)
-- =========================================================

-- --- Fiber (prod_spec: 'Fiber İnternet Ürün Tanımı') ---
INSERT INTO prod_ofr (prod_spec_id, name, descr, st_id, prod_ofr_total_prc, cuser)
SELECT (SELECT prod_spec_id FROM prod_spec WHERE name = 'Fiber İnternet Ürün Tanımı'),
       'Home Fiber 200Mbps', 'Ücretsiz modemli, 24 ay taahhütlü fiber internet paketi', 70, 399.90, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_ofr WHERE name = 'Home Fiber 200Mbps');

INSERT INTO prod_ofr (prod_spec_id, name, descr, st_id, prod_ofr_total_prc, cuser)
SELECT (SELECT prod_spec_id FROM prod_spec WHERE name = 'Fiber İnternet Ürün Tanımı'),
       'Home Fiber 100Mbps', 'Ücretsiz modemli, 24 ay taahhütlü fiber internet paketi', 70, 299.90, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_ofr WHERE name = 'Home Fiber 100Mbps');

INSERT INTO prod_ofr (prod_spec_id, name, descr, st_id, prod_ofr_total_prc, cuser)
SELECT (SELECT prod_spec_id FROM prod_spec WHERE name = 'Fiber İnternet Ürün Tanımı'),
       'Home Fiber 500Mbps', 'Yüksek hızlı, ücretsiz modemli fiber internet paketi', 70, 549.90, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_ofr WHERE name = 'Home Fiber 500Mbps');

INSERT INTO prod_ofr (prod_spec_id, name, descr, st_id, prod_ofr_total_prc, cuser)
SELECT (SELECT prod_spec_id FROM prod_spec WHERE name = 'Fiber İnternet Ürün Tanımı'),
       'Home Fiber 1Gbps', 'Gigabit hızında fiber internet paketi, 24 ay taahhütlü', 70, 699.90, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_ofr WHERE name = 'Home Fiber 1Gbps');

INSERT INTO prod_ofr (prod_spec_id, name, descr, st_id, prod_ofr_total_prc, cuser)
SELECT (SELECT prod_spec_id FROM prod_spec WHERE name = 'Fiber İnternet Ürün Tanımı'),
       'Home Fiber 2Gbps', 'Çoklu cihaz kullanımı için ultra hızlı fiber internet', 70, 899.90, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_ofr WHERE name = 'Home Fiber 2Gbps');

INSERT INTO prod_ofr (prod_spec_id, name, descr, st_id, prod_ofr_total_prc, cuser)
SELECT (SELECT prod_spec_id FROM prod_spec WHERE name = 'Fiber İnternet Ürün Tanımı'),
       'Home Internet + TV Bundle', 'Fiber internet ve dijital TV paketi bir arada', 70, 449.90, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_ofr WHERE name = 'Home Internet + TV Bundle');

INSERT INTO prod_ofr (prod_spec_id, name, descr, st_id, prod_ofr_total_prc, cuser)
SELECT (SELECT prod_spec_id FROM prod_spec WHERE name = 'Fiber İnternet Ürün Tanımı'),
       'Home Internet + Phone Bundle', 'Fiber internet ve ev telefonu hattı bir arada', 70, 429.90, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_ofr WHERE name = 'Home Internet + Phone Bundle');

INSERT INTO prod_ofr (prod_spec_id, name, descr, st_id, prod_ofr_total_prc, cuser)
SELECT (SELECT prod_spec_id FROM prod_spec WHERE name = 'Fiber İnternet Ürün Tanımı'),
       'Business Fiber 300Mbps', 'Küçük ofisler için özel fiber internet paketi', 70, 599.90, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_ofr WHERE name = 'Business Fiber 300Mbps');

INSERT INTO prod_ofr (prod_spec_id, name, descr, st_id, prod_ofr_total_prc, cuser)
SELECT (SELECT prod_spec_id FROM prod_spec WHERE name = 'Fiber İnternet Ürün Tanımı'),
       'Business Fiber 1Gbps Dedicated', 'Kurumsal SLA''lı adanmış gigabit fiber hattı', 70, 1199.90, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_ofr WHERE name = 'Business Fiber 1Gbps Dedicated');

INSERT INTO prod_ofr (prod_spec_id, name, descr, st_id, prod_ofr_total_prc, cuser)
SELECT (SELECT prod_spec_id FROM prod_spec WHERE name = 'Fiber İnternet Ürün Tanımı'),
       'Internet + Static IP', 'Sabit IP adresli fiber internet paketi', 70, 479.90, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_ofr WHERE name = 'Internet + Static IP');

-- --- Bakır Hat (ADSL/VDSL) ---
INSERT INTO prod_ofr (prod_spec_id, name, descr, st_id, prod_ofr_total_prc, cuser)
SELECT (SELECT prod_spec_id FROM prod_spec WHERE name = 'Bakır Hat İnternet Ürün Tanımı'),
       'Home ADSL 8Mbps', 'Hafif ev kullanımı için bakır hat internet', 70, 179.90, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_ofr WHERE name = 'Home ADSL 8Mbps');

INSERT INTO prod_ofr (prod_spec_id, name, descr, st_id, prod_ofr_total_prc, cuser)
SELECT (SELECT prod_spec_id FROM prod_spec WHERE name = 'Bakır Hat İnternet Ürün Tanımı'),
       'Home ADSL 16Mbps', 'Günlük ev kullanımı için bakır hat internet', 70, 209.90, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_ofr WHERE name = 'Home ADSL 16Mbps');

INSERT INTO prod_ofr (prod_spec_id, name, descr, st_id, prod_ofr_total_prc, cuser)
SELECT (SELECT prod_spec_id FROM prod_spec WHERE name = 'Bakır Hat İnternet Ürün Tanımı'),
       'Home VDSL 35Mbps', 'Yüksek hızlı bakır hat internet paketi', 70, 259.90, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_ofr WHERE name = 'Home VDSL 35Mbps');

INSERT INTO prod_ofr (prod_spec_id, name, descr, st_id, prod_ofr_total_prc, cuser)
SELECT (SELECT prod_spec_id FROM prod_spec WHERE name = 'Bakır Hat İnternet Ürün Tanımı'),
       'Business VDSL 50Mbps', 'Küçük ofisler için yüksek hızlı bakır hat internet', 70, 349.90, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_ofr WHERE name = 'Business VDSL 50Mbps');

-- --- Kablosuz / Uydu ---
INSERT INTO prod_ofr (prod_spec_id, name, descr, st_id, prod_ofr_total_prc, cuser)
SELECT (SELECT prod_spec_id FROM prod_spec WHERE name = 'Kablosuz İnternet Ürün Tanımı'),
       'Home Wireless Internet 50Mbps', 'Kablolama gerektirmeyen sabit kablosuz internet', 70, 319.90, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_ofr WHERE name = 'Home Wireless Internet 50Mbps');

INSERT INTO prod_ofr (prod_spec_id, name, descr, st_id, prod_ofr_total_prc, cuser)
SELECT (SELECT prod_spec_id FROM prod_spec WHERE name = 'Kablosuz İnternet Ürün Tanımı'),
       'Rural Satellite Internet', 'Fiber altyapısı olmayan bölgeler için uydu internet', 70, 279.90, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_ofr WHERE name = 'Rural Satellite Internet');

INSERT INTO prod_ofr (prod_spec_id, name, descr, st_id, prod_ofr_total_prc, cuser)
SELECT (SELECT prod_spec_id FROM prod_spec WHERE name = 'Kablosuz İnternet Ürün Tanımı'),
       'Student Home Internet 50Mbps', 'Öğrenci evleri için indirimli internet paketi', 70, 229.90, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_ofr WHERE name = 'Student Home Internet 50Mbps');

-- --- CPE / Donanım ---
INSERT INTO prod_ofr (prod_spec_id, name, descr, st_id, prod_ofr_total_prc, cuser)
SELECT (SELECT prod_spec_id FROM prod_spec WHERE name = 'CPE Ekipmanı'),
       'Broadband Modem', 'Fiber/DSL bağlantıları için gerekli modem', 70, 1199.90, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_ofr WHERE name = 'Broadband Modem');

-- Daha once elle test icin 'Broadband Modem' 1.19 gibi hatali bir fiyatla olusturulmus
-- olabilir - gercekci degere duzeltiyoruz (yukaridaki INSERT calismasa bile bu satir calisir).
UPDATE prod_ofr SET prod_ofr_total_prc = 1199.90 WHERE name = 'Broadband Modem';

INSERT INTO prod_ofr (prod_spec_id, name, descr, st_id, prod_ofr_total_prc, cuser)
SELECT (SELECT prod_spec_id FROM prod_spec WHERE name = 'CPE Ekipmanı'),
       'Wi-Fi Router Purchase', 'Çift bantlı Wi-Fi 6 router, 2 yıl garantili tek seferlik satın alma', 70, 2499.90, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_ofr WHERE name = 'Wi-Fi Router Purchase');

INSERT INTO prod_ofr (prod_spec_id, name, descr, st_id, prod_ofr_total_prc, cuser)
SELECT (SELECT prod_spec_id FROM prod_spec WHERE name = 'CPE Ekipmanı'),
       'Smart Home Hub', 'IoT SIM ile çalışan akıllı ev merkezi cihazı', 70, 899.90, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_ofr WHERE name = 'Smart Home Hub');

INSERT INTO prod_ofr (prod_spec_id, name, descr, st_id, prod_ofr_total_prc, cuser)
SELECT (SELECT prod_spec_id FROM prod_spec WHERE name = 'CPE Ekipmanı'),
       'Home Mesh Wi-Fi Add-on', 'Ev geneli mesh Wi-Fi genişletme kiti, tek seferlik satın alma', 70, 799.90, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_ofr WHERE name = 'Home Mesh Wi-Fi Add-on');

-- --- Mobil ---
INSERT INTO prod_ofr (prod_spec_id, name, descr, st_id, prod_ofr_total_prc, cuser)
SELECT (SELECT prod_spec_id FROM prod_spec WHERE name = 'Mobil Hat Ürün Tanımı'),
       'Mobile Postpaid 50GB', 'Aylık 50GB internet içeren faturalı mobil hat paketi', 70, 249.90, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_ofr WHERE name = 'Mobile Postpaid 50GB');

-- --- TV ---
INSERT INTO prod_ofr (prod_spec_id, name, descr, st_id, prod_ofr_total_prc, cuser)
SELECT (SELECT prod_spec_id FROM prod_spec WHERE name = 'TV Ürün Tanımı'),
       'Digital TV Basic Package', 'Temel dijital TV kanal paketi', 70, 179.90, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_ofr WHERE name = 'Digital TV Basic Package');

-- =========================================================
-- 4) PRODUCT CATALOG OFFERING (katalog <-> teklif eşleştirmeleri)
-- =========================================================

-- Internet kataloğuna bağlı tüm internet + CPE teklifleri (21 adet)
INSERT INTO prod_catal_prod_ofr (prod_catal_id, prod_ofr_id, st_id, cuser)
SELECT (SELECT prod_catal_id FROM prod_catal WHERE name = 'Internet'), o.prod_ofr_id, 177, 'system'
FROM prod_ofr o
WHERE o.name IN (
                 'Home Fiber 200Mbps','Home Fiber 100Mbps','Home Fiber 500Mbps','Home Fiber 1Gbps','Home Fiber 2Gbps',
                 'Home Internet + TV Bundle','Home Internet + Phone Bundle','Business Fiber 300Mbps',
                 'Business Fiber 1Gbps Dedicated','Internet + Static IP',
                 'Home ADSL 8Mbps','Home ADSL 16Mbps','Home VDSL 35Mbps','Business VDSL 50Mbps',
                 'Home Wireless Internet 50Mbps','Rural Satellite Internet','Student Home Internet 50Mbps',
                 'Broadband Modem','Wi-Fi Router Purchase','Smart Home Hub','Home Mesh Wi-Fi Add-on'
    )
  AND NOT EXISTS (
    SELECT 1 FROM prod_catal_prod_ofr pcpo
    WHERE pcpo.prod_catal_id = (SELECT prod_catal_id FROM prod_catal WHERE name = 'Internet')
      AND pcpo.prod_ofr_id = o.prod_ofr_id
);

-- Mobile kataloğu
INSERT INTO prod_catal_prod_ofr (prod_catal_id, prod_ofr_id, st_id, cuser)
SELECT (SELECT prod_catal_id FROM prod_catal WHERE name = 'Mobile'),
       (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Mobile Postpaid 50GB'), 177, 'system'
    WHERE NOT EXISTS (
    SELECT 1 FROM prod_catal_prod_ofr
    WHERE prod_catal_id = (SELECT prod_catal_id FROM prod_catal WHERE name = 'Mobile')
      AND prod_ofr_id = (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Mobile Postpaid 50GB')
);

-- TV kataloğu
INSERT INTO prod_catal_prod_ofr (prod_catal_id, prod_ofr_id, st_id, cuser)
SELECT (SELECT prod_catal_id FROM prod_catal WHERE name = 'TV'),
       (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Digital TV Basic Package'), 177, 'system'
    WHERE NOT EXISTS (
    SELECT 1 FROM prod_catal_prod_ofr
    WHERE prod_catal_id = (SELECT prod_catal_id FROM prod_catal WHERE name = 'TV')
      AND prod_ofr_id = (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Digital TV Basic Package')
);

-- =========================================================
-- 5) PRODUCT OFFERING RELATION (zorunlu/opsiyonel ek ürünler)
-- =========================================================

INSERT INTO prod_ofr_rel (prod_ofr_id1, prod_ofr_id2, rel_tp_id, qty, is_actv, cuser)
SELECT (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Home Fiber 200Mbps'),
       (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Broadband Modem'), 592, 1, true, 'system'
    WHERE NOT EXISTS (
    SELECT 1 FROM prod_ofr_rel
    WHERE prod_ofr_id1 = (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Home Fiber 200Mbps')
      AND prod_ofr_id2 = (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Broadband Modem')
);

INSERT INTO prod_ofr_rel (prod_ofr_id1, prod_ofr_id2, rel_tp_id, qty, is_actv, cuser)
SELECT (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Home Fiber 200Mbps'),
       (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Wi-Fi Router Purchase'), 592, 1, true, 'system'
    WHERE NOT EXISTS (
    SELECT 1 FROM prod_ofr_rel
    WHERE prod_ofr_id1 = (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Home Fiber 200Mbps')
      AND prod_ofr_id2 = (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Wi-Fi Router Purchase')
);

INSERT INTO prod_ofr_rel (prod_ofr_id1, prod_ofr_id2, rel_tp_id, qty, is_actv, cuser)
SELECT (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Home Fiber 100Mbps'),
       (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Broadband Modem'), 592, 1, true, 'system'
    WHERE NOT EXISTS (
    SELECT 1 FROM prod_ofr_rel
    WHERE prod_ofr_id1 = (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Home Fiber 100Mbps')
      AND prod_ofr_id2 = (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Broadband Modem')
);

INSERT INTO prod_ofr_rel (prod_ofr_id1, prod_ofr_id2, rel_tp_id, qty, is_actv, cuser)
SELECT (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Home Fiber 500Mbps'),
       (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Broadband Modem'), 592, 1, true, 'system'
    WHERE NOT EXISTS (
    SELECT 1 FROM prod_ofr_rel
    WHERE prod_ofr_id1 = (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Home Fiber 500Mbps')
      AND prod_ofr_id2 = (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Broadband Modem')
);

INSERT INTO prod_ofr_rel (prod_ofr_id1, prod_ofr_id2, rel_tp_id, qty, is_actv, cuser)
SELECT (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Home Fiber 1Gbps'),
       (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Broadband Modem'), 592, 1, true, 'system'
    WHERE NOT EXISTS (
    SELECT 1 FROM prod_ofr_rel
    WHERE prod_ofr_id1 = (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Home Fiber 1Gbps')
      AND prod_ofr_id2 = (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Broadband Modem')
);

INSERT INTO prod_ofr_rel (prod_ofr_id1, prod_ofr_id2, rel_tp_id, qty, is_actv, cuser)
SELECT (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Home Fiber 2Gbps'),
       (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Broadband Modem'), 592, 1, true, 'system'
    WHERE NOT EXISTS (
    SELECT 1 FROM prod_ofr_rel
    WHERE prod_ofr_id1 = (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Home Fiber 2Gbps')
      AND prod_ofr_id2 = (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Broadband Modem')
);

-- Opsiyonel ilişkiye de birkaç örnek (593 = OPTIONAL) - "isteğe bağlı ek ürün" senaryosu için
INSERT INTO prod_ofr_rel (prod_ofr_id1, prod_ofr_id2, rel_tp_id, qty, is_actv, cuser)
SELECT (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Home Wireless Internet 50Mbps'),
       (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Smart Home Hub'), 593, 1, true, 'system'
    WHERE NOT EXISTS (
    SELECT 1 FROM prod_ofr_rel
    WHERE prod_ofr_id1 = (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Home Wireless Internet 50Mbps')
      AND prod_ofr_id2 = (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Smart Home Hub')
);

INSERT INTO prod_ofr_rel (prod_ofr_id1, prod_ofr_id2, rel_tp_id, qty, is_actv, cuser)
SELECT (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Business Fiber 300Mbps'),
       (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Wi-Fi Router Purchase'), 593, 1, true, 'system'
    WHERE NOT EXISTS (
    SELECT 1 FROM prod_ofr_rel
    WHERE prod_ofr_id1 = (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Business Fiber 300Mbps')
      AND prod_ofr_id2 = (SELECT prod_ofr_id FROM prod_ofr WHERE name = 'Wi-Fi Router Purchase')
);

-- =========================================================
-- 6) CAMPAIGN'ler (10 adet, mockap'taki isimlerle birebir)
-- =========================================================

INSERT INTO cmpg (name, descr, cmpg_code, actvt_edate, st_id, is_penalty, cuser)
SELECT 'Home Starter Bundle', 'Yeni ev aboneleri için başlangıç paketi', 'HOME_STARTER', '2026-12-31', 255, false, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM cmpg WHERE name = 'Home Starter Bundle');

INSERT INTO cmpg (name, descr, cmpg_code, actvt_edate, st_id, is_penalty, cuser)
SELECT 'Fiber + Mobile + TV Combo', 'Fiber, mobil ve TV''nin bir arada olduğu kombine paket', 'FIBER_MOBILE_TV_COMBO', '2026-12-31', 255, true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM cmpg WHERE name = 'Fiber + Mobile + TV Combo');

INSERT INTO cmpg (name, descr, cmpg_code, actvt_edate, st_id, is_penalty, cuser)
SELECT 'Work From Home Bundle', 'Evden çalışanlar için internet ve ekipman paketi', 'WFH_BUNDLE', '2026-12-31', 255, false, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM cmpg WHERE name = 'Work From Home Bundle');

INSERT INTO cmpg (name, descr, cmpg_code, actvt_edate, st_id, is_penalty, cuser)
SELECT 'Small Business Fiber Bundle', 'Küçük işletmeler için fiber ve donanım paketi', 'SMB_FIBER_BUNDLE', '2026-12-31', 255, true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM cmpg WHERE name = 'Small Business Fiber Bundle');

INSERT INTO cmpg (name, descr, cmpg_code, actvt_edate, st_id, is_penalty, cuser)
SELECT 'Smart Home Bundle', 'Akıllı ev cihazlarıyla genişletilmiş fiber paketi', 'SMART_HOME_BUNDLE', '2026-12-31', 255, false, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM cmpg WHERE name = 'Smart Home Bundle');

INSERT INTO cmpg (name, descr, cmpg_code, actvt_edate, st_id, is_penalty, cuser)
SELECT 'Loyalty Reward Bundle', 'Sadık müşteriler için yüksek indirimli üst seviye paket', 'LOYALTY_REWARD_BUNDLE', '2026-12-31', 255, false, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM cmpg WHERE name = 'Loyalty Reward Bundle');

INSERT INTO cmpg (name, descr, cmpg_code, actvt_edate, st_id, is_penalty, cuser)
SELECT 'Winter Warmth Bundle', 'Kış dönemine özel internet ve TV paketi', 'WINTER_WARMTH_BUNDLE', '2026-12-31', 255, false, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM cmpg WHERE name = 'Winter Warmth Bundle');

INSERT INTO cmpg (name, descr, cmpg_code, actvt_edate, st_id, is_penalty, cuser)
SELECT 'Remote Worker Bundle', 'Uzaktan çalışanlar için kurumsal fiber paketi', 'REMOTE_WORKER_BUNDLE', '2026-12-31', 255, true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM cmpg WHERE name = 'Remote Worker Bundle');

INSERT INTO cmpg (name, descr, cmpg_code, actvt_edate, st_id, is_penalty, cuser)
SELECT 'Neighborhood Fiber Bundle', 'Mahalle bazlı tanıtım kampanyası paketi', 'NEIGHBORHOOD_FIBER_BUNDLE', '2026-12-31', 255, false, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM cmpg WHERE name = 'Neighborhood Fiber Bundle');

INSERT INTO cmpg (name, descr, cmpg_code, actvt_edate, st_id, is_penalty, cuser)
SELECT 'Device Upgrade Bundle', 'Mevcut müşteriler için cihaz yükseltme kampanyası', 'DEVICE_UPGRADE_BUNDLE', '2026-12-31', 255, false, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM cmpg WHERE name = 'Device Upgrade Bundle');

-- =========================================================
-- 7) CAMPAIGN OFFERING (bundle içerikleri, discount_pct ile)
-- =========================================================

-- Home Starter Bundle (2 teklif, %10)
INSERT INTO cmpg_prod_ofr (cmpg_id, prod_ofr_id, prod_ofr_name, prio, sdate, edate, is_actv, discount_pct, cuser)
SELECT (SELECT cmpg_id FROM cmpg WHERE name = 'Home Starter Bundle'), o.prod_ofr_id, o.name, 1, '2026-06-01', '2026-12-31', true, 10, 'system'
FROM prod_ofr o WHERE o.name IN ('Home Fiber 100Mbps','Broadband Modem')
                  AND NOT EXISTS (SELECT 1 FROM cmpg_prod_ofr WHERE cmpg_id = (SELECT cmpg_id FROM cmpg WHERE name = 'Home Starter Bundle') AND prod_ofr_id = o.prod_ofr_id);

-- Fiber + Mobile + TV Combo (3 teklif, %15) - mockap'ta birebir gösterilen kombinasyon
INSERT INTO cmpg_prod_ofr (cmpg_id, prod_ofr_id, prod_ofr_name, prio, sdate, edate, is_actv, discount_pct, cuser)
SELECT (SELECT cmpg_id FROM cmpg WHERE name = 'Fiber + Mobile + TV Combo'), o.prod_ofr_id, o.name, 1, '2026-06-01', '2026-12-31', true, 15, 'system'
FROM prod_ofr o WHERE o.name IN ('Mobile Postpaid 50GB','Home Fiber 100Mbps','Digital TV Basic Package')
                  AND NOT EXISTS (SELECT 1 FROM cmpg_prod_ofr WHERE cmpg_id = (SELECT cmpg_id FROM cmpg WHERE name = 'Fiber + Mobile + TV Combo') AND prod_ofr_id = o.prod_ofr_id);

-- Work From Home Bundle (3 teklif, %10)
INSERT INTO cmpg_prod_ofr (cmpg_id, prod_ofr_id, prod_ofr_name, prio, sdate, edate, is_actv, discount_pct, cuser)
SELECT (SELECT cmpg_id FROM cmpg WHERE name = 'Work From Home Bundle'), o.prod_ofr_id, o.name, 1, '2026-06-01', '2026-12-31', true, 10, 'system'
FROM prod_ofr o WHERE o.name IN ('Home Fiber 500Mbps','Wi-Fi Router Purchase','Smart Home Hub')
                  AND NOT EXISTS (SELECT 1 FROM cmpg_prod_ofr WHERE cmpg_id = (SELECT cmpg_id FROM cmpg WHERE name = 'Work From Home Bundle') AND prod_ofr_id = o.prod_ofr_id);

-- Small Business Fiber Bundle (2 teklif, %12)
INSERT INTO cmpg_prod_ofr (cmpg_id, prod_ofr_id, prod_ofr_name, prio, sdate, edate, is_actv, discount_pct, cuser)
SELECT (SELECT cmpg_id FROM cmpg WHERE name = 'Small Business Fiber Bundle'), o.prod_ofr_id, o.name, 1, '2026-06-01', '2026-12-31', true, 12, 'system'
FROM prod_ofr o WHERE o.name IN ('Business Fiber 300Mbps','Broadband Modem')
                  AND NOT EXISTS (SELECT 1 FROM cmpg_prod_ofr WHERE cmpg_id = (SELECT cmpg_id FROM cmpg WHERE name = 'Small Business Fiber Bundle') AND prod_ofr_id = o.prod_ofr_id);

-- Smart Home Bundle (4 teklif, %8)
INSERT INTO cmpg_prod_ofr (cmpg_id, prod_ofr_id, prod_ofr_name, prio, sdate, edate, is_actv, discount_pct, cuser)
SELECT (SELECT cmpg_id FROM cmpg WHERE name = 'Smart Home Bundle'), o.prod_ofr_id, o.name, 1, '2026-06-01', '2026-12-31', true, 8, 'system'
FROM prod_ofr o WHERE o.name IN ('Home Fiber 200Mbps','Smart Home Hub','Home Mesh Wi-Fi Add-on','Wi-Fi Router Purchase')
                  AND NOT EXISTS (SELECT 1 FROM cmpg_prod_ofr WHERE cmpg_id = (SELECT cmpg_id FROM cmpg WHERE name = 'Smart Home Bundle') AND prod_ofr_id = o.prod_ofr_id);

-- Loyalty Reward Bundle (2 teklif, %20)
INSERT INTO cmpg_prod_ofr (cmpg_id, prod_ofr_id, prod_ofr_name, prio, sdate, edate, is_actv, discount_pct, cuser)
SELECT (SELECT cmpg_id FROM cmpg WHERE name = 'Loyalty Reward Bundle'), o.prod_ofr_id, o.name, 1, '2026-06-01', '2026-12-31', true, 20, 'system'
FROM prod_ofr o WHERE o.name IN ('Home Fiber 1Gbps','Broadband Modem')
                  AND NOT EXISTS (SELECT 1 FROM cmpg_prod_ofr WHERE cmpg_id = (SELECT cmpg_id FROM cmpg WHERE name = 'Loyalty Reward Bundle') AND prod_ofr_id = o.prod_ofr_id);

-- Winter Warmth Bundle (2 teklif, %10)
INSERT INTO cmpg_prod_ofr (cmpg_id, prod_ofr_id, prod_ofr_name, prio, sdate, edate, is_actv, discount_pct, cuser)
SELECT (SELECT cmpg_id FROM cmpg WHERE name = 'Winter Warmth Bundle'), o.prod_ofr_id, o.name, 1, '2026-11-01', '2027-02-28', true, 10, 'system'
FROM prod_ofr o WHERE o.name IN ('Home Internet + TV Bundle','Digital TV Basic Package')
                  AND NOT EXISTS (SELECT 1 FROM cmpg_prod_ofr WHERE cmpg_id = (SELECT cmpg_id FROM cmpg WHERE name = 'Winter Warmth Bundle') AND prod_ofr_id = o.prod_ofr_id);

-- Remote Worker Bundle (2 teklif, %5)
INSERT INTO cmpg_prod_ofr (cmpg_id, prod_ofr_id, prod_ofr_name, prio, sdate, edate, is_actv, discount_pct, cuser)
SELECT (SELECT cmpg_id FROM cmpg WHERE name = 'Remote Worker Bundle'), o.prod_ofr_id, o.name, 1, '2026-06-01', '2026-12-31', true, 5, 'system'
FROM prod_ofr o WHERE o.name IN ('Business Fiber 1Gbps Dedicated','Wi-Fi Router Purchase')
                  AND NOT EXISTS (SELECT 1 FROM cmpg_prod_ofr WHERE cmpg_id = (SELECT cmpg_id FROM cmpg WHERE name = 'Remote Worker Bundle') AND prod_ofr_id = o.prod_ofr_id);

-- Neighborhood Fiber Bundle (2 teklif, %10)
INSERT INTO cmpg_prod_ofr (cmpg_id, prod_ofr_id, prod_ofr_name, prio, sdate, edate, is_actv, discount_pct, cuser)
SELECT (SELECT cmpg_id FROM cmpg WHERE name = 'Neighborhood Fiber Bundle'), o.prod_ofr_id, o.name, 1, '2026-06-01', '2026-12-31', true, 10, 'system'
FROM prod_ofr o WHERE o.name IN ('Home Fiber 100Mbps','Home Mesh Wi-Fi Add-on')
                  AND NOT EXISTS (SELECT 1 FROM cmpg_prod_ofr WHERE cmpg_id = (SELECT cmpg_id FROM cmpg WHERE name = 'Neighborhood Fiber Bundle') AND prod_ofr_id = o.prod_ofr_id);

-- Device Upgrade Bundle (2 teklif, %90 - agresif cihaz kampanyasi, yuksek indirim senaryosunu test etmek icin)
INSERT INTO cmpg_prod_ofr (cmpg_id, prod_ofr_id, prod_ofr_name, prio, sdate, edate, is_actv, discount_pct, cuser)
SELECT (SELECT cmpg_id FROM cmpg WHERE name = 'Device Upgrade Bundle'), o.prod_ofr_id, o.name, 1, '2026-06-01', '2026-12-31', true, 90, 'system'
FROM prod_ofr o WHERE o.name IN ('Wi-Fi Router Purchase','Smart Home Hub')
                  AND NOT EXISTS (SELECT 1 FROM cmpg_prod_ofr WHERE cmpg_id = (SELECT cmpg_id FROM cmpg WHERE name = 'Device Upgrade Bundle') AND prod_ofr_id = o.prod_ofr_id);