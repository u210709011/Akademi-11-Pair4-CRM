-- UI-07: eski sema (lookup-service V2__seed_lookup.sql) Ankara'yi sabit id=201 ile
-- seed'liyordu. Yeni sema migration'i (lookup-service V5__seed_general_lookup_data.sql)
-- ayni kaydi id vermeden ekledi ve auto-increment'ten 5 aldi (dogrulama:
-- GET /api/v1/general-types/5 -> {"name":"Ankara","entCodeName":"CITY"}, id=201 artik
-- 404). Frontend commit 24bcfdb ile dinamik cozume gectigi icin dropdown'da yalnizca
-- id 5 var; bu migration onceden 201 ile yazilmis mevcut kayitlari duzeltir.
UPDATE addr SET city_id = 5 WHERE city_id = 201;
