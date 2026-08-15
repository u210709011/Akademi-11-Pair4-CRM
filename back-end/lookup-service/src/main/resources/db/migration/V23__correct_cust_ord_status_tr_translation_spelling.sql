-- V22'de CUST_ORD (siparis durumu) icin eklenen 'tr' ceviri satirlari, V5'teki orijinal seed
-- verisindeki yazim hatalarini (Turkce karakter eksikligi/eksik harf) aynen tasidi. V22 zaten
-- uygulanmis bir migration oldugu icin (Flyway checksum) burada duzeltilmez, ayri bir migration
-- olarak UPDATE edilir.
UPDATE translation SET value = 'Sipariş Alındı, İşleniyor'
WHERE entity_name = 'GNL_ST' AND field_name IN ('NAME', 'DESCR') AND locale = 'tr' AND value = 'Siparis Alindi Isleniyor';

UPDATE translation SET value = 'Tamamlandı'
WHERE entity_name = 'GNL_ST' AND field_name IN ('NAME', 'DESCR') AND locale = 'tr' AND value = 'Tamamlandi';

UPDATE translation SET value = 'Reddedildi'
WHERE entity_name = 'GNL_ST' AND field_name IN ('NAME', 'DESCR') AND locale = 'tr' AND value = 'Rededildi';
