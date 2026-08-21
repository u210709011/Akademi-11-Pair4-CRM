-- CUST_ORD (siparis durumu, GNL_ST) icin Turkce ceviri - V21'de Ingilizceye cevrilen taban
-- degerlerin 'tr' locale'indeki karsiligi. id hardcode edilmez, shrt_code'dan INSERT...SELECT
-- ile bulunur (bkz. V16 ile ayni desen).
INSERT INTO translation (entity_name, entity_id, field_name, locale, value, cuser)
SELECT 'GNL_ST', gnl_st.gnl_st_id, fields.field_name, 'tr', tr_names.tr_value, 'system'
FROM gnl_st,
    (VALUES
        ('WAIT',     'Beklemede'),
        ('MIDLWARE', 'Siparis Alindi Isleniyor'),
        ('FINISHED', 'Tamamlandi'),
        ('REJECTED', 'Rededildi')
    ) AS tr_names(shrt_code, tr_value),
    (VALUES ('NAME'), ('DESCR')) AS fields(field_name)
WHERE gnl_st.ent_code_name = 'CUST_ORD' AND gnl_st.shrt_code = tr_names.shrt_code;
