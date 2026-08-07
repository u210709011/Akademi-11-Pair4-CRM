-- V5__seed_product_characteristic_demo_data.sql
-- 7 farkli offering tipinde ornek urun + karakteristik degerleri.
-- BILEREK "name" alani bos birakiliyor (gercek musteri/parti baglantisi
-- product-service'te henuz yok - gercek Product'lar ileride gercek akisla,
-- kendi isim/iliskileriyle olusturulacak). Script icinde satirlari birbirinden
-- ayirt etmek icin "descr" alani (kisi ismi degil, islevsel bir etiket)
-- benzersiz anahtar olarak kullaniliyor.
--
-- char_id / char_val_id degerleri V10__seed_char_and_char_val_demo_data.sql
-- calistirildiktan sonra DBeaver'dan dogrulanan GERCEK degerlerdir:
--   char_id: 1=CONN_SPEED, 2=CONN_TYPE, 3=COMMITMENT_PERIOD, 4=STATIC_IP,
--            5=MOBILE_DATA_PKG, 6=TV_CHANNEL_PKG
--   char_val_id: 1=200Mbps, 3=16Mbps, 5=50Mbps, 6=100Mbps, 7=300Mbps, 8=500Mbps,
--                11=Fiber, 12=ADSL, 14=Kablosuz, 17=24Ay, 16=12Ay, 20=Yok, 22=50GB, 25=Temel
-- st_id: 116 = GNL_ST/PROD/ACTV, 124 = GNL_ST/PROD_CHAR_VAL/ACTV
-- rel_tp_id: 591 = GNL_TP/PROD_REL/PRNTPROD

-- =========================================================
-- 1) Yeni PRODUCT kayitlari (7 abonelik + 1 modem) - name BOS
-- =========================================================

INSERT INTO prod (prod_ofr_id, prod_spec_id, descr, st_id, cuser)
SELECT o.prod_ofr_id, o.prod_spec_id, 'Home Fiber 100Mbps - örnek abonelik', 116, 'system'
FROM prod_ofr o WHERE o.name = 'Home Fiber 100Mbps'
                  AND NOT EXISTS (SELECT 1 FROM prod WHERE descr = 'Home Fiber 100Mbps - örnek abonelik');

INSERT INTO prod (prod_ofr_id, prod_spec_id, descr, st_id, cuser)
SELECT o.prod_ofr_id, o.prod_spec_id, 'Home Fiber 100Mbps aboneliğine bağlı örnek modem', 116, 'system'
FROM prod_ofr o WHERE o.name = 'Broadband Modem'
                  AND NOT EXISTS (SELECT 1 FROM prod WHERE descr = 'Home Fiber 100Mbps aboneliğine bağlı örnek modem');

INSERT INTO prod (prod_ofr_id, prod_spec_id, descr, st_id, cuser)
SELECT o.prod_ofr_id, o.prod_spec_id, 'Business Fiber 300Mbps - örnek abonelik', 116, 'system'
FROM prod_ofr o WHERE o.name = 'Business Fiber 300Mbps'
                  AND NOT EXISTS (SELECT 1 FROM prod WHERE descr = 'Business Fiber 300Mbps - örnek abonelik');

INSERT INTO prod (prod_ofr_id, prod_spec_id, descr, st_id, cuser)
SELECT o.prod_ofr_id, o.prod_spec_id, 'Home ADSL 16Mbps - örnek abonelik', 116, 'system'
FROM prod_ofr o WHERE o.name = 'Home ADSL 16Mbps'
                  AND NOT EXISTS (SELECT 1 FROM prod WHERE descr = 'Home ADSL 16Mbps - örnek abonelik');

INSERT INTO prod (prod_ofr_id, prod_spec_id, descr, st_id, cuser)
SELECT o.prod_ofr_id, o.prod_spec_id, 'Home Wireless Internet 50Mbps - örnek abonelik', 116, 'system'
FROM prod_ofr o WHERE o.name = 'Home Wireless Internet 50Mbps'
                  AND NOT EXISTS (SELECT 1 FROM prod WHERE descr = 'Home Wireless Internet 50Mbps - örnek abonelik');

INSERT INTO prod (prod_ofr_id, prod_spec_id, descr, st_id, cuser)
SELECT o.prod_ofr_id, o.prod_spec_id, 'Mobile Postpaid 50GB - örnek abonelik', 116, 'system'
FROM prod_ofr o WHERE o.name = 'Mobile Postpaid 50GB'
                  AND NOT EXISTS (SELECT 1 FROM prod WHERE descr = 'Mobile Postpaid 50GB - örnek abonelik');

INSERT INTO prod (prod_ofr_id, prod_spec_id, descr, st_id, cuser)
SELECT o.prod_ofr_id, o.prod_spec_id, 'Digital TV Basic Package - örnek abonelik', 116, 'system'
FROM prod_ofr o WHERE o.name = 'Digital TV Basic Package'
                  AND NOT EXISTS (SELECT 1 FROM prod WHERE descr = 'Digital TV Basic Package - örnek abonelik');

INSERT INTO prod (prod_ofr_id, prod_spec_id, descr, st_id, cuser)
SELECT o.prod_ofr_id, o.prod_spec_id, 'Home Fiber 500Mbps - örnek abonelik', 116, 'system'
FROM prod_ofr o WHERE o.name = 'Home Fiber 500Mbps'
                  AND NOT EXISTS (SELECT 1 FROM prod WHERE descr = 'Home Fiber 500Mbps - örnek abonelik');

-- =========================================================
-- 2) Fiber hizmeti <-> modem arasinda PRODUCT RELATION ornegi
-- =========================================================

INSERT INTO prod_rel (prod_id1, prod_id2, rel_tp_id, is_actv, cuser)
SELECT (SELECT prod_id FROM prod WHERE descr = 'Home Fiber 100Mbps - örnek abonelik'),
       (SELECT prod_id FROM prod WHERE descr = 'Home Fiber 100Mbps aboneliğine bağlı örnek modem'), 591, true, 'system'
    WHERE NOT EXISTS (
    SELECT 1 FROM prod_rel
    WHERE prod_id1 = (SELECT prod_id FROM prod WHERE descr = 'Home Fiber 100Mbps - örnek abonelik')
      AND prod_id2 = (SELECT prod_id FROM prod WHERE descr = 'Home Fiber 100Mbps aboneliğine bağlı örnek modem')
);

-- =========================================================
-- 3) PRODUCT CHARACTERISTIC VALUE - her aboneliğe uygun karakteristikler
-- =========================================================

-- --- Home Fiber 100Mbps: Bağlantı Hızı + Tipi + Taahhüt ---
INSERT INTO prod_char_val (prod_id, char_id, char_val_id, st_id, cuser)
SELECT (SELECT prod_id FROM prod WHERE descr = 'Home Fiber 100Mbps - örnek abonelik'), 1, 6, 124, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_char_val WHERE prod_id = (SELECT prod_id FROM prod WHERE descr = 'Home Fiber 100Mbps - örnek abonelik') AND char_id = 1);

INSERT INTO prod_char_val (prod_id, char_id, char_val_id, st_id, cuser)
SELECT (SELECT prod_id FROM prod WHERE descr = 'Home Fiber 100Mbps - örnek abonelik'), 2, 11, 124, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_char_val WHERE prod_id = (SELECT prod_id FROM prod WHERE descr = 'Home Fiber 100Mbps - örnek abonelik') AND char_id = 2);

INSERT INTO prod_char_val (prod_id, char_id, char_val_id, st_id, cuser)
SELECT (SELECT prod_id FROM prod WHERE descr = 'Home Fiber 100Mbps - örnek abonelik'), 3, 17, 124, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_char_val WHERE prod_id = (SELECT prod_id FROM prod WHERE descr = 'Home Fiber 100Mbps - örnek abonelik') AND char_id = 3);

-- --- Business Fiber 300Mbps ---
INSERT INTO prod_char_val (prod_id, char_id, char_val_id, st_id, cuser)
SELECT (SELECT prod_id FROM prod WHERE descr = 'Business Fiber 300Mbps - örnek abonelik'), 1, 7, 124, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_char_val WHERE prod_id = (SELECT prod_id FROM prod WHERE descr = 'Business Fiber 300Mbps - örnek abonelik') AND char_id = 1);

INSERT INTO prod_char_val (prod_id, char_id, char_val_id, st_id, cuser)
SELECT (SELECT prod_id FROM prod WHERE descr = 'Business Fiber 300Mbps - örnek abonelik'), 2, 11, 124, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_char_val WHERE prod_id = (SELECT prod_id FROM prod WHERE descr = 'Business Fiber 300Mbps - örnek abonelik') AND char_id = 2);

INSERT INTO prod_char_val (prod_id, char_id, char_val_id, st_id, cuser)
SELECT (SELECT prod_id FROM prod WHERE descr = 'Business Fiber 300Mbps - örnek abonelik'), 3, 17, 124, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_char_val WHERE prod_id = (SELECT prod_id FROM prod WHERE descr = 'Business Fiber 300Mbps - örnek abonelik') AND char_id = 3);

-- --- Home ADSL 16Mbps ---
INSERT INTO prod_char_val (prod_id, char_id, char_val_id, st_id, cuser)
SELECT (SELECT prod_id FROM prod WHERE descr = 'Home ADSL 16Mbps - örnek abonelik'), 1, 3, 124, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_char_val WHERE prod_id = (SELECT prod_id FROM prod WHERE descr = 'Home ADSL 16Mbps - örnek abonelik') AND char_id = 1);

INSERT INTO prod_char_val (prod_id, char_id, char_val_id, st_id, cuser)
SELECT (SELECT prod_id FROM prod WHERE descr = 'Home ADSL 16Mbps - örnek abonelik'), 2, 12, 124, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_char_val WHERE prod_id = (SELECT prod_id FROM prod WHERE descr = 'Home ADSL 16Mbps - örnek abonelik') AND char_id = 2);

INSERT INTO prod_char_val (prod_id, char_id, char_val_id, st_id, cuser)
SELECT (SELECT prod_id FROM prod WHERE descr = 'Home ADSL 16Mbps - örnek abonelik'), 3, 16, 124, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_char_val WHERE prod_id = (SELECT prod_id FROM prod WHERE descr = 'Home ADSL 16Mbps - örnek abonelik') AND char_id = 3);

-- --- Home Wireless Internet 50Mbps ---
INSERT INTO prod_char_val (prod_id, char_id, char_val_id, st_id, cuser)
SELECT (SELECT prod_id FROM prod WHERE descr = 'Home Wireless Internet 50Mbps - örnek abonelik'), 1, 5, 124, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_char_val WHERE prod_id = (SELECT prod_id FROM prod WHERE descr = 'Home Wireless Internet 50Mbps - örnek abonelik') AND char_id = 1);

INSERT INTO prod_char_val (prod_id, char_id, char_val_id, st_id, cuser)
SELECT (SELECT prod_id FROM prod WHERE descr = 'Home Wireless Internet 50Mbps - örnek abonelik'), 2, 14, 124, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_char_val WHERE prod_id = (SELECT prod_id FROM prod WHERE descr = 'Home Wireless Internet 50Mbps - örnek abonelik') AND char_id = 2);

INSERT INTO prod_char_val (prod_id, char_id, char_val_id, st_id, cuser)
SELECT (SELECT prod_id FROM prod WHERE descr = 'Home Wireless Internet 50Mbps - örnek abonelik'), 4, 20, 124, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_char_val WHERE prod_id = (SELECT prod_id FROM prod WHERE descr = 'Home Wireless Internet 50Mbps - örnek abonelik') AND char_id = 4);

-- --- Mobile Postpaid 50GB ---
INSERT INTO prod_char_val (prod_id, char_id, char_val_id, st_id, cuser)
SELECT (SELECT prod_id FROM prod WHERE descr = 'Mobile Postpaid 50GB - örnek abonelik'), 5, 22, 124, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_char_val WHERE prod_id = (SELECT prod_id FROM prod WHERE descr = 'Mobile Postpaid 50GB - örnek abonelik') AND char_id = 5);

-- --- Digital TV Basic Package ---
INSERT INTO prod_char_val (prod_id, char_id, char_val_id, st_id, cuser)
SELECT (SELECT prod_id FROM prod WHERE descr = 'Digital TV Basic Package - örnek abonelik'), 6, 25, 124, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_char_val WHERE prod_id = (SELECT prod_id FROM prod WHERE descr = 'Digital TV Basic Package - örnek abonelik') AND char_id = 6);

-- --- Home Fiber 500Mbps ---
INSERT INTO prod_char_val (prod_id, char_id, char_val_id, st_id, cuser)
SELECT (SELECT prod_id FROM prod WHERE descr = 'Home Fiber 500Mbps - örnek abonelik'), 1, 8, 124, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_char_val WHERE prod_id = (SELECT prod_id FROM prod WHERE descr = 'Home Fiber 500Mbps - örnek abonelik') AND char_id = 1);

INSERT INTO prod_char_val (prod_id, char_id, char_val_id, st_id, cuser)
SELECT (SELECT prod_id FROM prod WHERE descr = 'Home Fiber 500Mbps - örnek abonelik'), 2, 11, 124, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_char_val WHERE prod_id = (SELECT prod_id FROM prod WHERE descr = 'Home Fiber 500Mbps - örnek abonelik') AND char_id = 2);

INSERT INTO prod_char_val (prod_id, char_id, char_val_id, st_id, cuser)
SELECT (SELECT prod_id FROM prod WHERE descr = 'Home Fiber 500Mbps - örnek abonelik'), 3, 17, 124, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM prod_char_val WHERE prod_id = (SELECT prod_id FROM prod WHERE descr = 'Home Fiber 500Mbps - örnek abonelik') AND char_id = 3);

-- =========================================================
-- 4) Dünkü mevcut kayıt (adı zaten var, dokunulmuyor) - sadece eksik
--    iki karakteristiği tamamlıyoruz. Bu satır SENİN dünkü gerçek "name"
--    değerine göre çalışır - eşleşmezse sessizce atlanır, hata vermez.
-- =========================================================

INSERT INTO prod_char_val (prod_id, char_id, char_val_id, st_id, cuser)
SELECT (SELECT prod_id FROM prod WHERE name = 'Ahmet Yılmaz - Ev Fiber İnterneti'), 2, 11, 124, 'system'
    WHERE EXISTS (SELECT 1 FROM prod WHERE name = 'Ahmet Yılmaz - Ev Fiber İnterneti')
AND NOT EXISTS (SELECT 1 FROM prod_char_val WHERE prod_id = (SELECT prod_id FROM prod WHERE name = 'Ahmet Yılmaz - Ev Fiber İnterneti') AND char_id = 2);

INSERT INTO prod_char_val (prod_id, char_id, char_val_id, st_id, cuser)
SELECT (SELECT prod_id FROM prod WHERE name = 'Ahmet Yılmaz - Ev Fiber İnterneti'), 3, 17, 124, 'system'
    WHERE EXISTS (SELECT 1 FROM prod WHERE name = 'Ahmet Yılmaz - Ev Fiber İnterneti')
AND NOT EXISTS (SELECT 1 FROM prod_char_val WHERE prod_id = (SELECT prod_id FROM prod WHERE name = 'Ahmet Yılmaz - Ev Fiber İnterneti') AND char_id = 3);