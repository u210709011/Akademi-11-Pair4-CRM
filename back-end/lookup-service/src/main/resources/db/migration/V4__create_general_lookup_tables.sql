-- Kurumsal genel lookup semasi: GNL_TP/GNL_ST (grup tanimlari) + TYPE_VALUE (deger satirlari)
-- + GNL_CHAR/GNL_CHAR_VAL (karakteristik tanim/deger) + RSRC_SPEC/SRVC_SPEC (kaynak/servis spec).
-- Eski duz 'lookup' tablosunun yerini alir (bkz. V5/V6).

CREATE TABLE gnl_tp (
    gnl_tp_id     BIGSERIAL PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    descr         VARCHAR(100) NOT NULL,
    shrt_code     VARCHAR(64)  NOT NULL,
    ent_code_name VARCHAR(100) NOT NULL,
    ent_name      VARCHAR(100),
    is_actv       BOOLEAN      NOT NULL DEFAULT true,
    cdate         TIMESTAMP,
    cuser         VARCHAR(100),
    udate         TIMESTAMP,
    uuser         VARCHAR(100),
    CONSTRAINT uq_gnl_tp_shrt_code UNIQUE (shrt_code)
);

CREATE TABLE gnl_st (
    gnl_st_id     BIGSERIAL PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    descr         VARCHAR(100) NOT NULL,
    shrt_code     VARCHAR(15)  NOT NULL,
    is_actv       BOOLEAN      NOT NULL DEFAULT true,
    ent_code_name VARCHAR(100) NOT NULL,
    ent_name      VARCHAR(100),
    cdate         TIMESTAMP,
    cuser         VARCHAR(100),
    udate         TIMESTAMP,
    uuser         VARCHAR(100),
    CONSTRAINT uq_gnl_st_shrt_code UNIQUE (shrt_code)
);

-- Polimorfik referans: table_name ('GNL_TP'/'GNL_ST') + field_name (o tablodaki satirin id'si)
-- birlikte "bu deger hangi gruba ait" sorusunu cevaplar - iki farkli tabloya isaret edebildigi
-- icin gercek bir FK kurulamaz (ayni desen contact-info-service'teki ADDR.row_id/data_type_id).
CREATE TABLE type_value (
    type_value_id     BIGSERIAL PRIMARY KEY,
    table_name        VARCHAR(40)  NOT NULL,
    field_name        BIGINT       NOT NULL,
    description       VARCHAR(200) NOT NULL,
    value              VARCHAR(50),
    using_module_name VARCHAR(50),
    cdate             TIMESTAMP,
    cuser             VARCHAR(100),
    udate             TIMESTAMP,
    uuser             VARCHAR(100),
    CONSTRAINT ck_type_value_table_name CHECK (table_name IN ('GNL_TP', 'GNL_ST')),
    CONSTRAINT uq_type_value_group_value UNIQUE (table_name, field_name, value)
);

CREATE INDEX idx_type_value_group ON type_value (table_name, field_name);

CREATE TABLE gnl_char (
    char_id   BIGSERIAL PRIMARY KEY,
    name      VARCHAR(100) NOT NULL,
    descr     VARCHAR(100) NOT NULL,
    prvdr_cls VARCHAR(100),
    shrt_code VARCHAR(50)  NOT NULL,
    is_actv   BOOLEAN      NOT NULL DEFAULT true,
    cdate     TIMESTAMP,
    cuser     VARCHAR(100),
    udate     TIMESTAMP,
    uuser     VARCHAR(100)
);

CREATE TABLE gnl_char_val (
    char_val_id BIGSERIAL PRIMARY KEY,
    char_id     BIGINT       NOT NULL REFERENCES gnl_char (char_id),
    is_dflt     BOOLEAN      NOT NULL DEFAULT false,
    val         VARCHAR(100),
    shrt_code   VARCHAR(100) NOT NULL,
    sdate       DATE         NOT NULL,
    edate       DATE,
    is_actv     BOOLEAN      NOT NULL DEFAULT true,
    cdate       TIMESTAMP,
    cuser       VARCHAR(100),
    udate       TIMESTAMP,
    uuser       VARCHAR(100)
);

CREATE INDEX idx_gnl_char_val_char_id ON gnl_char_val (char_id);

CREATE TABLE rsrc_spec (
    rsrc_spec_id BIGSERIAL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    descr        VARCHAR(100) NOT NULL,
    st_id        BIGINT       NOT NULL,  -- mantiksal referans -> gnl_st.gnl_st_id, gercek FK yok
    rsrc_code    VARCHAR(30),
    cdate        TIMESTAMP,
    cuser        VARCHAR(100),
    udate        TIMESTAMP,
    uuser        VARCHAR(100)
);

CREATE INDEX idx_rsrc_spec_st_id ON rsrc_spec (st_id);

CREATE TABLE srvc_spec (
    srvc_spec_id BIGSERIAL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    descr        VARCHAR(100) NOT NULL,
    srvc_code    VARCHAR(100) NOT NULL,
    st_id        BIGINT       NOT NULL,  -- mantiksal referans -> gnl_st.gnl_st_id, gercek FK yok
    cdate        TIMESTAMP,
    cuser        VARCHAR(100),
    udate        TIMESTAMP,
    uuser        VARCHAR(100)
);

CREATE INDEX idx_srvc_spec_st_id ON srvc_spec (st_id);
