-- CUST_ORD_CHAR_VAL artik CUST_ORD'a degil CUST_ORD_ITEM'a bagli: Configuration ekraninda her
-- basket item'inin kendi karakteristik seti var, sepette birden fazla item oldugunda eski
-- (order-seviyesi) semayla hangi karakteristigin hangi item'a ait oldugu ayirt edilemiyordu.
-- Var olan satirlar hangi item'a ait oldugu bilinemedigi icin temizlenir - henuz gercek veri
-- yok (feature branch, prod'a hic cikmadi).
DELETE FROM cust_ord_char_val;
ALTER TABLE cust_ord_char_val DROP COLUMN cust_ord_id;
ALTER TABLE cust_ord_char_val ADD COLUMN cust_ord_item_id BIGINT NOT NULL REFERENCES cust_ord_item (cust_ord_item_id);

CREATE INDEX idx_cust_ord_char_val_cust_ord_item ON cust_ord_char_val (cust_ord_item_id);
