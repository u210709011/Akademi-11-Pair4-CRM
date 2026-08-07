-- V2__create_product_offering_relation_table.sql
CREATE TABLE prod_ofr_rel
(
    prod_ofr_rel_id BIGSERIAL PRIMARY KEY,
    prod_ofr_id1    BIGINT  NOT NULL REFERENCES prod_ofr (prod_ofr_id),
    prod_ofr_id2    BIGINT  NOT NULL REFERENCES prod_ofr (prod_ofr_id),
    rel_tp_id       BIGINT  NOT NULL,
    qty             INTEGER NOT NULL DEFAULT 1,
    is_actv         BOOLEAN NOT NULL DEFAULT TRUE,
    cdate           TIMESTAMPTZ,
    cuser           VARCHAR(255),
    udate           TIMESTAMPTZ,
    uuser           VARCHAR(255)
);