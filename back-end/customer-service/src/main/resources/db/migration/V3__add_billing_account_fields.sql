-- ACC-001..014 (Create Billing Account) icin CUST_ACCT'e eklenen alanlar.
-- Ikisi de nullable: mevcut varsayilan (223) hesapta doldurulmuyor, sadece
-- fatura (224) hesabi olusturulurken uygulama katmaninda zorunlu tutulur.
-- ADDRESS_ID contact-info-service'teki Address'e cross-service logical
-- referanstir (FK DEGIL).
ALTER TABLE cust_acct ADD COLUMN acct_desc VARCHAR(500);
ALTER TABLE cust_acct ADD COLUMN address_id BIGINT;
