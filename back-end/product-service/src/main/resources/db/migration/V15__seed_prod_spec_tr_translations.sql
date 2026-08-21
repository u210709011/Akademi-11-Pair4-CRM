-- prod_spec icin Turkce ceviriler - V7'de seed edilmis orijinal Turkce metin, V11'de Ingilizceye
-- cevrilen taban degerin 'tr' locale'indeki karsiligi. prod_spec_id'ler V7'nin kendi tasarimi
-- geregi sabit/deterministiktir (bkz. V11 yorumu).
INSERT INTO translation (entity_name, entity_id, field_name, locale, value, cuser) VALUES
    ('PROD_SPEC', 1, 'NAME', 'tr', 'Fiber İnternet Ürün Tanımı', 'system'),
    ('PROD_SPEC', 1, 'DESCR', 'tr', 'Ev tipi fiber internet hizmetleri için ürün spesifikasyonu', 'system'),
    ('PROD_SPEC', 2, 'NAME', 'tr', 'CPE Ekipmanı', 'system'),
    ('PROD_SPEC', 2, 'DESCR', 'tr', 'Müşteri tarafında kurulan fiber modem/router donanımı', 'system'),
    ('PROD_SPEC', 3, 'NAME', 'tr', 'Bakır Hat İnternet Ürün Tanımı', 'system'),
    ('PROD_SPEC', 3, 'DESCR', 'tr', 'ADSL/VDSL bakır altyapı internet hizmetleri', 'system'),
    ('PROD_SPEC', 4, 'NAME', 'tr', 'Kablosuz İnternet Ürün Tanımı', 'system'),
    ('PROD_SPEC', 4, 'DESCR', 'tr', 'Kablosuz/uydu tabanlı internet hizmetleri', 'system'),
    ('PROD_SPEC', 5, 'NAME', 'tr', 'Mobil Hat Ürün Tanımı', 'system'),
    ('PROD_SPEC', 5, 'DESCR', 'tr', 'Mobil hat ve paket ürün spesifikasyonu', 'system'),
    ('PROD_SPEC', 6, 'NAME', 'tr', 'TV Ürün Tanımı', 'system'),
    ('PROD_SPEC', 6, 'DESCR', 'tr', 'Dijital TV yayın hizmetleri ürün spesifikasyonu', 'system');
