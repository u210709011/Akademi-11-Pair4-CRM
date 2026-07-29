UPDATE gnl_tp
SET
    name = 'Bireysel',
    descr = 'Bireysel',
    shrt_code = 'INDIVIDUAL',
    ent_code_name = 'CUSTOMER_TYPE',
    ent_name = 'CUSTOMER_TYPE',
    is_actv = true,
    cuser = 'system'
WHERE ent_name = 'CUSTOMER_TYPE'
  AND shrt_code = 'YOUNG';

UPDATE gnl_tp
SET
    name = 'Kurumsal',
    descr = 'Kurumsal',
    shrt_code = 'CORPORATE',
    ent_code_name = 'CUSTOMER_TYPE',
    ent_name = 'CUSTOMER_TYPE',
    is_actv = true,
    cuser = 'system'
WHERE ent_name = 'CUSTOMER_TYPE'
  AND shrt_code = 'RETIRED';