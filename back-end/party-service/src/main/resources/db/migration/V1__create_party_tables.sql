-- PARTY + IND + PARTY_ROLE (BRAIN.md SS3 - Pair4 uyarlamasi).
-- Lookup id'leri (party_tp_id, gndr_id, party_role_tp_id) duz Long
-- kolonlardir; lookup-service ayri serviste oldugu icin FK kurulmaz.
-- Soft delete: is_active. Party domaininde lifecycle (st_id) kullanilmiyor.
-- kolonlardir; lookup-service ayri serviste oldugu icin FK kurulmaz.

CREATE TABLE party (
     party_id     BIGSERIAL PRIMARY KEY,
    party_tp_id  BIGINT NOT NULL,
    is_active    BOOLEAN NOT NULL DEFAULT TRUE,
    cdate        TIMESTAMPTZ,
    cuser        VARCHAR(255),
    udate        TIMESTAMPTZ,
    uuser        VARCHAR(255)
);

CREATE TABLE ind (
    ind_id     BIGSERIAL PRIMARY KEY,
    party_id   BIGINT NOT NULL REFERENCES party (party_id),
    frst_name  VARCHAR(255) NOT NULL,
    mname      VARCHAR(255),
    lst_name   VARCHAR(255) NOT NULL,
    brth_date  DATE NOT NULL,
    gndr_id    BIGINT NOT NULL,
    mthr_name  VARCHAR(255),
    fthr_name  VARCHAR(255),
    nat_id     VARCHAR(11) NOT NULL UNIQUE,
    is_active  BOOLEAN NOT NULL DEFAULT TRUE,
    cdate      TIMESTAMPTZ,
    cuser      VARCHAR(255),
    udate      TIMESTAMPTZ,
    uuser      VARCHAR(255)
);

CREATE TABLE party_role (
    party_role_id    BIGSERIAL PRIMARY KEY,
    party_id         BIGINT NOT NULL REFERENCES party (party_id),
    party_role_tp_id BIGINT NOT NULL,
    is_active        BOOLEAN NOT NULL DEFAULT TRUE,
    cdate            TIMESTAMPTZ,
    cuser            VARCHAR(255),
    udate            TIMESTAMPTZ,
    uuser            VARCHAR(255)
);
