-- SS2: hesap durumu (Account Status) icin lookup-service'in yeni GNL_ST/ACCOUNT_STATUS
-- grubuna (ACTIVE/PASSIVE) mantiksal referans. FK DEGIL - servis siniri.
-- Var olan kayitlara varsayilan bir ID yazilamaz (lookup-service'teki gercek GNL_ST id'si
-- migration zamaninda bilinmiyor); kolon nullable birakilir, uygulama katmani acct_st_id
-- IS NULL durumunu "ACTIVE" olarak yorumlar (mevcut kayitlar zaten fiilen aktif hesaplardi).
ALTER TABLE cust_acct ADD COLUMN acct_st_id BIGINT;
