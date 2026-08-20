-- B-20: charValId opsiyonel (serbest metin karakteristikleri, bkz. ProdCharValRequest.val) ama
-- kolon NOT NULL kalmisti - free-text kaydi 500 ile patliyordu. product-service'teki es deger
-- (prod_char_val.char_val_id) zaten nullable, orada da hem charValId hem val ayni anda null
-- olabilecek sekilde kod yazilmamis (charId + charValId/val'den en az biri) - burada da ayni
-- kurala hizalaniyoruz.
ALTER TABLE cust_ord_char_val ALTER COLUMN char_val_id DROP NOT NULL;
ALTER TABLE cust_ord_char_val ADD CONSTRAINT chk_cust_ord_char_val_has_value
    CHECK (char_val_id IS NOT NULL OR val IS NOT NULL);
