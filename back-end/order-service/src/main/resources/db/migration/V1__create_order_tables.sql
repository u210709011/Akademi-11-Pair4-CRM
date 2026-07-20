-- BSN_INTER_SPEC, BSN_INTER_TP_ID/PRNT_BSN_INTER_SPEC_ID gibi lookup id'leri duz
-- BIGINT kolonlardir; lookup-service ayri serviste oldugu icin FK kurulmaz.
-- Soft delete: is_active. Ayrica bir lifecycle (st_id) kullanilmiyor.
CREATE TABLE bsn_inter_spec (
    bsn_inter_spec_id      BIGSERIAL PRIMARY KEY,
    bsn_inter_tp_id        BIGINT,
    prnt_bsn_inter_spec_id BIGINT REFERENCES bsn_inter_spec (bsn_inter_spec_id),
    name                   VARCHAR(255) NOT NULL,
    descr                  VARCHAR(255),
    shrt_code              VARCHAR(64),
    is_active              BOOLEAN      NOT NULL DEFAULT true,
    cdate                  TIMESTAMPTZ,
    cuser                  VARCHAR(255),
    udate                  TIMESTAMPTZ,
    uuser                  VARCHAR(255)
);

CREATE INDEX idx_bsn_inter_spec_prnt ON bsn_inter_spec (prnt_bsn_inter_spec_id);

-- BSN_INTER: is etkilesimi/surec kaydi. CUST_ID musteri-service'e, BSN_INTER_ST_ID
-- lookup-service'teki GNL_ST'ye mantiksal referanstir (FK degildir).
-- ROW_ID + DATA_TP_ID polimorfik sahiplik ciftidir (ör. CUST_ORD'u isaret eder).
CREATE TABLE bsn_inter (
    bsn_inter_id      BIGSERIAL PRIMARY KEY,
    bsn_inter_spec_id BIGINT REFERENCES bsn_inter_spec (bsn_inter_spec_id),
    cust_id           BIGINT,
    descr             VARCHAR(255),
    bsn_inter_st_id   BIGINT,
    prnt_bsn_inter_id BIGINT REFERENCES bsn_inter (bsn_inter_id),
    row_id            BIGINT,
    data_tp_id        BIGINT,
    is_active         BOOLEAN      NOT NULL DEFAULT true,
    cdate             TIMESTAMPTZ,
    cuser             VARCHAR(255),
    udate             TIMESTAMPTZ,
    uuser             VARCHAR(255)
);

CREATE INDEX idx_bsn_inter_cust_id ON bsn_inter (cust_id);
CREATE INDEX idx_bsn_inter_prnt ON bsn_inter (prnt_bsn_inter_id);
CREATE INDEX idx_bsn_inter_row_data_tp ON bsn_inter (row_id, data_tp_id);

-- BSN_INTER_ITEM: BSN_INTER'a servis-ici gercek FK ile baglidir. ACTN_TP_ID/ST_ID
-- lookup-service'teki GNL_TP/GNL_ST'ye mantiksal referanstir (FK degildir).
CREATE TABLE bsn_inter_item (
    bsn_inter_item_id BIGSERIAL PRIMARY KEY,
    bsn_inter_id      BIGINT       NOT NULL REFERENCES bsn_inter (bsn_inter_id),
    bsn_inter_spec_id BIGINT REFERENCES bsn_inter_spec (bsn_inter_spec_id),
    descr             VARCHAR(255),
    row_id            BIGINT,
    data_tp_id        BIGINT,
    actn_tp_id        BIGINT,
    st_id             BIGINT,
    is_active         BOOLEAN      NOT NULL DEFAULT true,
    cdate             TIMESTAMPTZ,
    cuser             VARCHAR(255),
    udate             TIMESTAMPTZ,
    uuser             VARCHAR(255)
);

CREATE INDEX idx_bsn_inter_item_bsn_inter ON bsn_inter_item (bsn_inter_id);
CREATE INDEX idx_bsn_inter_item_row_data_tp ON bsn_inter_item (row_id, data_tp_id);

-- CUST_ORD: siparis basligi. CUST_ID musteri-service'e mantiksal referanstir.
-- ORD_ST_ID lookup-service'teki GNL_ST'ye mantiksal referanstir (FK degildir).
CREATE TABLE cust_ord (
    cust_ord_id       BIGSERIAL PRIMARY KEY,
    ord_st_id         BIGINT,
    cust_id           BIGINT       NOT NULL,
    bsn_inter_id      BIGINT REFERENCES bsn_inter (bsn_inter_id),
    bsn_inter_spec_id BIGINT REFERENCES bsn_inter_spec (bsn_inter_spec_id),
    is_active         BOOLEAN      NOT NULL DEFAULT true,
    cdate             TIMESTAMPTZ,
    cuser             VARCHAR(255),
    udate             TIMESTAMPTZ,
    uuser             VARCHAR(255)
);

CREATE INDEX idx_cust_ord_cust_id ON cust_ord (cust_id);
CREATE INDEX idx_cust_ord_bsn_inter ON cust_ord (bsn_inter_id);

-- CUST_ORD_ITEM: CUST_ORD'a servis-ici gercek FK. CUST_ACCT_ID/PROD_ID/PROD_OFR_ID/
-- PROD_SPEC_ID/CMPG_ID/CUST_ID diger servislere mantiksal referanstir (FK degildir).
-- NEW_CUST_ACCT_ID/NEW_CUST_ID hesap/musteri devri senaryosu icindir, su an bos kalir.
CREATE TABLE cust_ord_item (
    cust_ord_item_id BIGSERIAL PRIMARY KEY,
    cust_ord_id      BIGINT       NOT NULL REFERENCES cust_ord (cust_ord_id),
    cust_acct_id     BIGINT,
    new_cust_acct_id BIGINT,
    prod_id          BIGINT,
    prod_ofr_id      BIGINT,
    bsn_inter_id     BIGINT REFERENCES bsn_inter (bsn_inter_id),
    cmpg_id          BIGINT,
    is_need_shpmt    BOOLEAN,
    ofr_name         VARCHAR(255),
    prod_name        VARCHAR(255),
    prod_spec_id     BIGINT,
    cust_id          BIGINT,
    new_cust_id      BIGINT,
    cmpg_name        VARCHAR(255),
    is_active        BOOLEAN      NOT NULL DEFAULT true,
    cdate            TIMESTAMPTZ,
    cuser            VARCHAR(255),
    udate            TIMESTAMPTZ,
    uuser            VARCHAR(255)
);

CREATE INDEX idx_cust_ord_item_cust_ord ON cust_ord_item (cust_ord_id);
CREATE INDEX idx_cust_ord_item_cust_acct ON cust_ord_item (cust_acct_id);

-- CUST_ORD_CHAR_VAL: CUST_ORD'a servis-ici gercek FK. CHAR_ID/CHAR_VAL_ID
-- lookup-service'teki GNL_CHAR/GNL_CHAR_VAL'e mantiksal referanstir (FK degildir).
CREATE TABLE cust_ord_char_val (
    cust_ord_char_val_id BIGSERIAL PRIMARY KEY,
    cust_ord_id          BIGINT       NOT NULL REFERENCES cust_ord (cust_ord_id),
    char_id              BIGINT       NOT NULL,
    char_val_id          BIGINT       NOT NULL,
    val                  VARCHAR(255),
    is_active            BOOLEAN      NOT NULL DEFAULT true,
    cdate                TIMESTAMPTZ,
    cuser                VARCHAR(255),
    udate                TIMESTAMPTZ,
    uuser                VARCHAR(255)
);

CREATE INDEX idx_cust_ord_char_val_cust_ord ON cust_ord_char_val (cust_ord_id);
