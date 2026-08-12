-- V7'de prod_spec tamamen Turkce seed edilmisti. Bu uygulamada her string'in taban degeri
-- Ingilizce olmalidir - Turkce artik V15'te translation tablosunda 'tr' locale'i icin ayri
-- satirlarla saglanir. prod_spec_id degerleri V7'nin kendi yorumunda belirtildigi gibi SABIT/
-- deterministiktir (hangi ortamda calisirsa calissin ayni ID'ler uretilir) - bu yuzden burada
-- dogrudan referans verilmesi guvenlidir (prod_spec'te baska bir dogal anahtar/shrt_code yok).
UPDATE prod_spec SET name = 'Fiber Internet Product Specification',
    descr = 'Product specification for home-type fiber internet services' WHERE prod_spec_id = 1;
UPDATE prod_spec SET name = 'CPE Equipment',
    descr = 'Fiber modem/router hardware installed on the customer side' WHERE prod_spec_id = 2;
UPDATE prod_spec SET name = 'Copper Line Internet Product Specification',
    descr = 'ADSL/VDSL copper-infrastructure internet services' WHERE prod_spec_id = 3;
UPDATE prod_spec SET name = 'Wireless Internet Product Specification',
    descr = 'Wireless/satellite-based internet services' WHERE prod_spec_id = 4;
UPDATE prod_spec SET name = 'Mobile Line Product Specification',
    descr = 'Mobile line and package product specification' WHERE prod_spec_id = 5;
UPDATE prod_spec SET name = 'TV Product Specification',
    descr = 'Product specification for digital TV broadcast services' WHERE prod_spec_id = 6;
