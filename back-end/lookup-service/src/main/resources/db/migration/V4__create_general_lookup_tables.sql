-- Kurumsal genel lookup semasi: GNL_TP/GNL_ST duz deger tablolaridir (her satir tek bir deger,
-- ent_code_name o degerin ait oldugu grubu belirtir - eski lookup.group_code'un yerini alir).
-- TYPE_VALUE ise GNL_TP/GNL_ST'den BAGIMSIZ, herhangi bir is tablosu icin polimorfik sahiplik
-- etiketi (row_id + type_id) uretmekte kullanilan genel bir tablo->numeric-tip kayit defteridir
-- (eski DATA_TYPE grubunun genellestirilmis hali - bkz. contact-info-service ADDR.data_type_id).
-- + GNL_CHAR/GNL_CHAR_VAL (karakteristik tanim/deger) + RSRC_SPEC/SRVC_SPEC (kaynak/servis spec).
-- Eski duz 'lookup' tablosunun yerini alir (bkz. V5/V6). Lookup-service'teki hicbir tablo
-- digerine gercek FK ile baglanmaz - butun capraz referanslar mantiksaldir.

CREATE TABLE gnl_tp (
    gnl_tp_id     BIGSERIAL PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    descr         VARCHAR(100) NOT NULL,
    shrt_code     VARCHAR(64)  NOT NULL,  -- bu degerin kendi kisa kodu (orn. PSTN, GSM, CUST_ACCT)
    ent_code_name VARCHAR(100) NOT NULL,  -- grup anahtari (orn. CNTC_MEDIUM, ACCOUNT_TYPE)
    ent_name      VARCHAR(100),           -- pratikte ent_code_name ile ayni deger seed edilir
    is_actv       BOOLEAN      NOT NULL DEFAULT true,
    cdate         TIMESTAMP,
    cuser         VARCHAR(100),
    udate         TIMESTAMP,
    uuser         VARCHAR(100),
    CONSTRAINT uq_gnl_tp_group_code UNIQUE (ent_code_name, shrt_code)
);

CREATE TABLE gnl_st (
    gnl_st_id     BIGSERIAL PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    descr         VARCHAR(100) NOT NULL,
    shrt_code     VARCHAR(15)  NOT NULL,  -- bu degerin kendi kisa kodu (orn. ACTIVE, PASSIVE)
    is_actv       BOOLEAN      NOT NULL DEFAULT true,
    ent_code_name VARCHAR(100) NOT NULL,  -- grup anahtari (orn. CUST, CUST_ACCT)
    ent_name      VARCHAR(100),           -- pratikte ent_code_name ile ayni deger seed edilir
    cdate         TIMESTAMP,
    cuser         VARCHAR(100),
    udate         TIMESTAMP,
    uuser         VARCHAR(100),
    CONSTRAINT uq_gnl_st_group_code UNIQUE (ent_code_name, shrt_code)
);

-- GNL_TP/GNL_ST'den bagimsiz: table_name gercek bir is tablosunun adi (orn. PROD, PARTY, CUST,
-- CUST_ACCT), field_name o tabloya atanmis polimorfik tip etiketi (orn. PARTY->9, CUST->12).
-- Baska bir servis, kendi tablosunda row_id + bu numarayi tasiyarak "bu satir hangi tabloya ait"
-- sorusunu cevaplar (ayni desen contact-info-service ADDR.row_id/data_type_id).
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
    CONSTRAINT uq_type_value_table_name UNIQUE (table_name)
);

CREATE INDEX idx_type_value_field_name ON type_value (field_name);

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
    char_id     BIGINT       NOT NULL,  -- mantiksal referans -> gnl_char.char_id, gercek FK yok
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
