-- CUST_ORD.address_id: siparisin servis adresi, contact-info-service ADDR.addr_id'ye
-- mantiksal referanstir (FK degildir). Configuration adiminda (saveConfiguration) yazilir;
-- boylece sayfa yenilense de secilen/olusturulan adres kaybolmaz.
ALTER TABLE cust_ord ADD COLUMN address_id BIGINT;
