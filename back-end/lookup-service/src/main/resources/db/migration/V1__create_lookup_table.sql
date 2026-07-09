-- LOOKUP: tum referans/enum verisinin tek kaynagi.
-- (group_code, value_id) ve (group_code, code) benzersizdir.
-- Diger servisler value_id'yi duz Long olarak saklar; FK kurulmaz.
CREATE TABLE lookup (
    lookup_id     BIGSERIAL PRIMARY KEY,   -- surrogate (ic kullanim)
    group_code    VARCHAR(50)  NOT NULL,   -- ör. CUST_STATUS, GENDER
    value_id      BIGINT       NOT NULL,   -- grup-ici is kimligi -> API "id"
    code          VARCHAR(50),             -- makine kodu -> API "code" (bazi grupta null)
    look_up_value VARCHAR(200) NOT NULL,   -- gosterim -> API "value"
    cdate         TIMESTAMP,
    cuser         BIGINT,
    udate         TIMESTAMP,
    uuser         BIGINT,
    CONSTRAINT uq_lookup_group_value UNIQUE (group_code, value_id),
    CONSTRAINT uq_lookup_group_code  UNIQUE (group_code, code)
);

CREATE INDEX idx_lookup_group ON lookup (group_code);
