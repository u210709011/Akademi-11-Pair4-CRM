-- prod_catal.name zaten Ingilizce'ydi (Internet/Mobile/TV) - sadece descr Turkce'ydi, burada
-- duzeltiliyor. shrt_code dogal anahtar olarak kullanilir (id hardcode edilmez).
UPDATE prod_catal SET descr = 'Catalog listing home and office internet products' WHERE shrt_code = 'INTERNET';
UPDATE prod_catal SET descr = 'Catalog listing mobile line products' WHERE shrt_code = 'MOBILE';
UPDATE prod_catal SET descr = 'Catalog listing digital TV products' WHERE shrt_code = 'TV';
