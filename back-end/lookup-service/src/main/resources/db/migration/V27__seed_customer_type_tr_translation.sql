-- V26'da Ingilizceye cevrilen CUSTOMER_TYPE (CORPORATE/INDIVIDUAL) taban degerlerinin 'tr'
-- locale'indeki karsiligi (bkz. V16/V25 ile ayni desen).
INSERT INTO translation (entity_name, entity_id, field_name, locale, value, cuser)
SELECT 'GNL_TP', gnl_tp.gnl_tp_id, fields.field_name, 'tr', tr_names.tr_value, 'system'
FROM gnl_tp,
    (VALUES
        ('CORPORATE',  'Kurumsal'),
        ('INDIVIDUAL', 'Bireysel')
    ) AS tr_names(shrt_code, tr_value),
    (VALUES ('NAME'), ('DESCR')) AS fields(field_name)
WHERE gnl_tp.ent_code_name = 'CUSTOMER_TYPE' AND gnl_tp.shrt_code = tr_names.shrt_code;
