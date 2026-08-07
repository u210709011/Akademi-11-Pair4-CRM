-- V11__reset_and_reseed_char_demo_data_fixed_ids.sql (lookup-service)
-- ONCEKI DURUM: V10, gnl_char/gnl_char_val'i "WHERE NOT EXISTS" ile idempotent seed ediyordu,
-- ama bu ortamda V10'dan ONCE elle olusturulmus satirlar oldugu icin (V10'un basindaki
-- "Adim 0'da manuel olusturulmustu" notu) gercek ID'ler script sirasindan sapmisti.
-- Bu migration, gnl_char/gnl_char_val icindeki TUM satirlari SILER ve ayni veriyi SABIT,
-- deterministik ID'lerle yeniden ekler - boylece bu migration hangi bilgisayarda/ortamda
-- calisirsa calissin (temiz DB dahil) HER ZAMAN AYNI char_id / char_val_id degerlerini uretir.
--
-- UYARI: Bu tablolardaki mevcut TUM kayitlar (elle eklenmis olanlar dahil) silinir.
-- gnl_char.char_id / gnl_char_val.char_val_id kolonlarina baska hicbir serviste gercek FK yok
-- (product-service bu ID'lere duz BIGINT olarak referans verir), bu yuzden silme islemi
-- Postgres seviyesinde bir FK ihlaline yol acmaz - ama product-service'teki referanslarin
-- (prod_char_val, prod_ofr_char_use) bu YENI ID semasiyla uyumlu olmasi gerekir (bkz.
-- product-service V7__reset_and_reseed_demo_data_fixed_ids.sql, ayni gunde birlikte yazildi).

DELETE FROM gnl_char_val;
DELETE FROM gnl_char;

-- =========================================================
-- 1) GNL_CHAR - sabit ID 1-6
-- =========================================================
INSERT INTO gnl_char (char_id, name, descr, shrt_code, is_actv, cuser) VALUES
    (1, 'Bağlantı Hızı', 'İnternet bağlantısının indirme hızı', 'CONN_SPEED', true, 'system'),
    (2, 'Bağlantı Tipi', 'İnternet bağlantısının alt yapı tipi', 'CONN_TYPE', true, 'system'),
    (3, 'Taahhüt Süresi', 'Aboneliğin taahhüt süresi', 'COMMITMENT_PERIOD', true, 'system'),
    (4, 'Statik IP', 'Sabit IP adresi tahsisi durumu', 'STATIC_IP', true, 'system'),
    (5, 'Mobil Veri Paketi', 'Mobil hat aylık veri kotası', 'MOBILE_DATA_PKG', true, 'system'),
    (6, 'TV Kanal Paketi', 'Dijital TV kanal paketi seviyesi', 'TV_CHANNEL_PKG', true, 'system');

-- =========================================================
-- 2) GNL_CHAR_VAL - sabit ID 1-27
-- =========================================================
INSERT INTO gnl_char_val (char_val_id, char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser) VALUES
    -- CONN_SPEED (char_id=1): id 1-10
    (1, 1, false, '8 Mbps', '8MBPS', '2026-01-01', true, 'system'),
    (2, 1, false, '16 Mbps', '16MBPS', '2026-01-01', true, 'system'),
    (3, 1, false, '35 Mbps', '35MBPS', '2026-01-01', true, 'system'),
    (4, 1, false, '50 Mbps', '50MBPS', '2026-01-01', true, 'system'),
    (5, 1, true,  '100 Mbps', '100MBPS', '2026-01-01', true, 'system'),
    (6, 1, false, '200 Mbps', '200MBPS', '2026-01-01', true, 'system'),
    (7, 1, false, '300 Mbps', '300MBPS', '2026-01-01', true, 'system'),
    (8, 1, false, '500 Mbps', '500MBPS', '2026-01-01', true, 'system'),
    (9, 1, false, '1 Gbps', '1GBPS', '2026-01-01', true, 'system'),
    (10, 1, false, '2 Gbps', '2GBPS', '2026-01-01', true, 'system'),
    -- CONN_TYPE (char_id=2): id 11-15
    (11, 2, true,  'Fiber', 'FIBER', '2026-01-01', true, 'system'),
    (12, 2, false, 'ADSL', 'ADSL', '2026-01-01', true, 'system'),
    (13, 2, false, 'VDSL', 'VDSL', '2026-01-01', true, 'system'),
    (14, 2, false, 'Kablosuz', 'WIRELESS', '2026-01-01', true, 'system'),
    (15, 2, false, 'Uydu', 'SATELLITE', '2026-01-01', true, 'system'),
    -- COMMITMENT_PERIOD (char_id=3): id 16-18
    (16, 3, false, '12 Ay', '12AY', '2026-01-01', true, 'system'),
    (17, 3, true,  '24 Ay', '24AY', '2026-01-01', true, 'system'),
    (18, 3, false, 'Taahhütsüz', 'NO_COMMIT', '2026-01-01', true, 'system'),
    -- STATIC_IP (char_id=4): id 19-20
    (19, 4, false, 'Var', 'STATIC_IP_YES', '2026-01-01', true, 'system'),
    (20, 4, true,  'Yok', 'STATIC_IP_NO', '2026-01-01', true, 'system'),
    -- MOBILE_DATA_PKG (char_id=5): id 21-24
    (21, 5, false, '20GB', 'DATA_20GB', '2026-01-01', true, 'system'),
    (22, 5, true,  '50GB', 'DATA_50GB', '2026-01-01', true, 'system'),
    (23, 5, false, '100GB', 'DATA_100GB', '2026-01-01', true, 'system'),
    (24, 5, false, 'Sınırsız', 'DATA_UNLIMITED', '2026-01-01', true, 'system'),
    -- TV_CHANNEL_PKG (char_id=6): id 25-27
    (25, 6, true,  'Temel', 'TV_BASIC', '2026-01-01', true, 'system'),
    (26, 6, false, 'Standart', 'TV_STANDARD', '2026-01-01', true, 'system'),
    (27, 6, false, 'Premium', 'TV_PREMIUM', '2026-01-01', true, 'system');

-- =========================================================
-- 3) Sequence'leri son hardcoded ID'nin sonrasina tasi -
--    boylece uygulama uzerinden yeni bir kayit eklenirse ID cakismasi olmaz.
-- =========================================================
SELECT setval(pg_get_serial_sequence('gnl_char', 'char_id'), 6, true);
SELECT setval(pg_get_serial_sequence('gnl_char_val', 'char_val_id'), 27, true);
