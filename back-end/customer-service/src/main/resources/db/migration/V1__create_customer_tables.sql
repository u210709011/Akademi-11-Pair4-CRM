-- CUST: musteri kaydi. PARTY_ROLE_ID, party-service'e mantiksal referanstir (FK degildir).
-- is_active: soft-delete bayragi. st_id: lookup-service'teki lifecycle durumu (PENDING/ACTIVE/SUSPENDED/...).
-- Ikisi birbirinin yerine gecmez.
CREATE TABLE cust (
    cust_id       BIGSERIAL PRIMARY KEY,
    party_role_id BIGINT       NOT NULL,
    cust_tp_id    BIGINT,
    st_id         BIGINT       NOT NULL,
    is_active     BOOLEAN      NOT NULL DEFAULT true,
    cdate         TIMESTAMP    NOT NULL DEFAULT now(),
    cuser         VARCHAR(100) NOT NULL,
    udate         TIMESTAMP,
    uuser         VARCHAR(100)
);

CREATE INDEX idx_cust_party_role_id ON cust (party_role_id);

-- CUST_ACCT: musteri hesabi, CUST'a servis-ici gercek FK ile baglidir.
CREATE TABLE cust_acct (
    cust_acct_id BIGSERIAL PRIMARY KEY,
    cust_id      BIGINT       NOT NULL REFERENCES cust (cust_id),
    acct_no      VARCHAR(50)  NOT NULL,
    acct_name    VARCHAR(200),
    acct_tp_id   BIGINT,
    st_id        BIGINT       NOT NULL,
    is_active    BOOLEAN      NOT NULL DEFAULT true,
    cdate        TIMESTAMP    NOT NULL DEFAULT now(),
    cuser        VARCHAR(100) NOT NULL,
    udate        TIMESTAMP,
    uuser        VARCHAR(100),
    CONSTRAINT uq_cust_acct_no UNIQUE (acct_no)
);

CREATE INDEX idx_cust_acct_cust_id ON cust_acct (cust_id);

-- CUSTOMER_SEARCH_VIEW: parti-service verisiyle denormalize edilmis, arama icin read-model.
-- party-service event'leri (IndividualPartyCreated / IndividualUpdated) ile guncel tutulur.
CREATE TABLE customer_search_view (
    cust_id       BIGINT PRIMARY KEY,
    party_role_id BIGINT       NOT NULL,
    first_name    VARCHAR(100),
    last_name     VARCHAR(100),
    tc_no         VARCHAR(11),
    acct_no       VARCHAR(50),
    status        VARCHAR(30),
    deleted       BOOLEAN      NOT NULL DEFAULT false
);

CREATE INDEX idx_csv_lastname ON customer_search_view (last_name);
CREATE INDEX idx_csv_tc ON customer_search_view (tc_no);
