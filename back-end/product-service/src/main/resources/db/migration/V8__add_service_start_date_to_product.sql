-- V8__add_service_start_date_to_product.sql
-- prod.sdate: bu urun/servis orneginin fiilen ne zaman aktif oldugunu tutar (cdate'ten
-- farkli - cdate kaydin DB'ye yazildigi an, sdate is anlaminda servisin basladigi tarih).
-- order-service'teki CustOrdManager.finishOrder() -> provisionProducts() akisinda
-- ProductManager.create() cagrildiginda otomatik LocalDate.now() olarak set edilir.
-- Nullable: mevcut satirlar (V7'deki demo urunler haric) icin bilinmiyor.
ALTER TABLE prod ADD COLUMN sdate DATE;

-- V7'de seed edilen 8 demo urune, ekranda bos gorunmemesi icin gecmis bir tarih veriyoruz.
UPDATE prod SET sdate = '2026-06-15' WHERE prod_id BETWEEN 1 AND 8 AND sdate IS NULL;
