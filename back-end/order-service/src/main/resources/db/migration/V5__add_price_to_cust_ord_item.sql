-- CUST_ORD_ITEM.price: item eklenirken (createOrder/addItem) product-service'ten cekilen
-- teklif fiyatinin snapshot'idir. Ileride offer fiyati degisse bile bu siparisin fiyati
-- sabit kalsin diye canli sorgulanmaz, kayit anindaki deger burada saklanir.
ALTER TABLE cust_ord_item ADD COLUMN price NUMERIC(16, 2);
