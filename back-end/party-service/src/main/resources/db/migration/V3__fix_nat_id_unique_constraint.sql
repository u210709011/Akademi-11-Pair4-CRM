-- ind_nat_id_key (V1) tum satirlar (aktif/pasif fark etmeksizin) icin tekillik dayatiyordu.
-- Uygulama tarafi ise sadece aktif kayitlar arasinda tekillik bekliyor (bkz.
-- IndividualBusinessRules.checkNationalIdNotDuplicate -> existsByNationalIdAndActiveTrue).
-- Bu uyumsuzluk, soft-delete edilmis (ör. compensation ile geri alinmis) bir nat_id'nin
-- tekrar kullanilmasini engelliyordu (duplicate key violates unique constraint "ind_nat_id_key").
-- Cozum: DB kisitini is kuraliyla hizala - sadece is_active=true satirlar arasinda tekil olsun.

ALTER TABLE ind DROP CONSTRAINT ind_nat_id_key;

CREATE UNIQUE INDEX ind_nat_id_active_key ON ind (nat_id) WHERE is_active = true;
