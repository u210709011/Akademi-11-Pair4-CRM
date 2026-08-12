-- V5'te CITY grubuna sadece Ankara eklenmisti (TAHMINI veri, bkz. V5 yorumu). Gercekci veri
-- seed'i (musteri/adres) birden fazla sehir gerektirdigi icin en kalabalik illerden bir kismi
-- eklendi. Diger CITY satirlari gibi id auto-increment; shrt_code resolve/CITY/{shrtCode} ile
-- kullanilir.
INSERT INTO gnl_tp (name, descr, shrt_code, ent_code_name, ent_name, is_actv, cuser) VALUES
    ('Istanbul',   'Istanbul',   'ISTANBUL',   'CITY', 'CITY', true, 'system'),
    ('Izmir',      'Izmir',      'IZMIR',      'CITY', 'CITY', true, 'system'),
    ('Bursa',      'Bursa',      'BURSA',      'CITY', 'CITY', true, 'system'),
    ('Antalya',    'Antalya',    'ANTALYA',    'CITY', 'CITY', true, 'system'),
    ('Adana',      'Adana',      'ADANA',      'CITY', 'CITY', true, 'system'),
    ('Konya',      'Konya',      'KONYA',      'CITY', 'CITY', true, 'system'),
    ('Gaziantep',  'Gaziantep',  'GAZIANTEP',  'CITY', 'CITY', true, 'system'),
    ('Mersin',     'Mersin',     'MERSIN',     'CITY', 'CITY', true, 'system'),
    ('Kayseri',    'Kayseri',    'KAYSERI',    'CITY', 'CITY', true, 'system'),
    ('Eskisehir',  'Eskisehir',  'ESKISEHIR',  'CITY', 'CITY', true, 'system');
