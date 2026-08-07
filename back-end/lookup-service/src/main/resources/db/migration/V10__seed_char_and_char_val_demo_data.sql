-- V10__seed_char_and_char_val_demo_data.sql
-- Dropdown'larda kullanilacak karakteristik tanimlari ve degerleri.
-- IDEMPOTENT: shrt_code'a gore NOT EXISTS korumasi var, tekrar calistirilabilir.
-- 'Baglanti Hizi' (CONN_SPEED) daha once Adim 0'da manuel olusturulmustu -
-- burada TEKRAR eklenmeye calisiliyor ama NOT EXISTS onu atlayacak, sorun yok.

-- =========================================================
-- 1) GNL_CHAR - karakteristik tanimlari (6 adet)
-- =========================================================

INSERT INTO gnl_char (name, descr, shrt_code, is_actv, cuser)
SELECT 'Bağlantı Hızı', 'İnternet bağlantısının indirme hızı', 'CONN_SPEED', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char WHERE shrt_code = 'CONN_SPEED');

INSERT INTO gnl_char (name, descr, shrt_code, is_actv, cuser)
SELECT 'Bağlantı Tipi', 'İnternet bağlantısının alt yapı tipi', 'CONN_TYPE', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char WHERE shrt_code = 'CONN_TYPE');

INSERT INTO gnl_char (name, descr, shrt_code, is_actv, cuser)
SELECT 'Taahhüt Süresi', 'Aboneliğin taahhüt süresi', 'COMMITMENT_PERIOD', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char WHERE shrt_code = 'COMMITMENT_PERIOD');

INSERT INTO gnl_char (name, descr, shrt_code, is_actv, cuser)
SELECT 'Statik IP', 'Sabit IP adresi tahsisi durumu', 'STATIC_IP', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char WHERE shrt_code = 'STATIC_IP');

INSERT INTO gnl_char (name, descr, shrt_code, is_actv, cuser)
SELECT 'Mobil Veri Paketi', 'Mobil hat aylık veri kotası', 'MOBILE_DATA_PKG', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char WHERE shrt_code = 'MOBILE_DATA_PKG');

INSERT INTO gnl_char (name, descr, shrt_code, is_actv, cuser)
SELECT 'TV Kanal Paketi', 'Dijital TV kanal paketi seviyesi', 'TV_CHANNEL_PKG', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char WHERE shrt_code = 'TV_CHANNEL_PKG');

-- =========================================================
-- 2) GNL_CHAR_VAL - dropdown degerleri
-- =========================================================

-- --- CONN_SPEED (dun'ki ProductOffering hizlariyla birebir eslesiyor) ---
INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'CONN_SPEED'), false, '8 Mbps', '8MBPS', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = '8MBPS');

INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'CONN_SPEED'), false, '16 Mbps', '16MBPS', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = '16MBPS');

INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'CONN_SPEED'), false, '35 Mbps', '35MBPS', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = '35MBPS');

INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'CONN_SPEED'), false, '50 Mbps', '50MBPS', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = '50MBPS');

INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'CONN_SPEED'), true, '100 Mbps', '100MBPS', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = '100MBPS');

INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'CONN_SPEED'), false, '200 Mbps', '200MBPS', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = '200MBPS');

INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'CONN_SPEED'), false, '300 Mbps', '300MBPS', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = '300MBPS');

INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'CONN_SPEED'), false, '500 Mbps', '500MBPS', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = '500MBPS');

INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'CONN_SPEED'), false, '1 Gbps', '1GBPS', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = '1GBPS');

INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'CONN_SPEED'), false, '2 Gbps', '2GBPS', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = '2GBPS');

-- --- CONN_TYPE ---
INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'CONN_TYPE'), true, 'Fiber', 'FIBER', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = 'FIBER');

INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'CONN_TYPE'), false, 'ADSL', 'ADSL', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = 'ADSL');

INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'CONN_TYPE'), false, 'VDSL', 'VDSL', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = 'VDSL');

INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'CONN_TYPE'), false, 'Kablosuz', 'WIRELESS', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = 'WIRELESS');

INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'CONN_TYPE'), false, 'Uydu', 'SATELLITE', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = 'SATELLITE');

-- --- COMMITMENT_PERIOD ---
INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'COMMITMENT_PERIOD'), false, '12 Ay', '12AY', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = '12AY');

INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'COMMITMENT_PERIOD'), true, '24 Ay', '24AY', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = '24AY');

INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'COMMITMENT_PERIOD'), false, 'Taahhütsüz', 'NO_COMMIT', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = 'NO_COMMIT');

-- --- STATIC_IP ---
INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'STATIC_IP'), false, 'Var', 'STATIC_IP_YES', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = 'STATIC_IP_YES');

INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'STATIC_IP'), true, 'Yok', 'STATIC_IP_NO', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = 'STATIC_IP_NO');

-- --- MOBILE_DATA_PKG ---
INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'MOBILE_DATA_PKG'), false, '20GB', 'DATA_20GB', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = 'DATA_20GB');

INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'MOBILE_DATA_PKG'), true, '50GB', 'DATA_50GB', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = 'DATA_50GB');

INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'MOBILE_DATA_PKG'), false, '100GB', 'DATA_100GB', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = 'DATA_100GB');

INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'MOBILE_DATA_PKG'), false, 'Sınırsız', 'DATA_UNLIMITED', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = 'DATA_UNLIMITED');

-- --- TV_CHANNEL_PKG ---
INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'TV_CHANNEL_PKG'), true, 'Temel', 'TV_BASIC', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = 'TV_BASIC');

INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'TV_CHANNEL_PKG'), false, 'Standart', 'TV_STANDARD', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = 'TV_STANDARD');

INSERT INTO gnl_char_val (char_id, is_dflt, val, shrt_code, sdate, is_actv, cuser)
SELECT (SELECT char_id FROM gnl_char WHERE shrt_code = 'TV_CHANNEL_PKG'), false, 'Premium', 'TV_PREMIUM', '2026-01-01', true, 'system'
    WHERE NOT EXISTS (SELECT 1 FROM gnl_char_val WHERE shrt_code = 'TV_PREMIUM');

-- =========================================================
-- 3) Dogrulama sorgusu - bunu calistirip SONUCU bana ilet
-- =========================================================
-- SELECT char_id, name, shrt_code FROM gnl_char ORDER BY char_id;
-- SELECT char_val_id, char_id, val, shrt_code FROM gnl_char_val ORDER BY char_id, char_val_id;