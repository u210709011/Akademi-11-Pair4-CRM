CREATE TABLE prod_ofr_char_use (
                                   prod_ofr_char_use_id BIGSERIAL PRIMARY KEY,
                                   prod_ofr_id      BIGINT NOT NULL REFERENCES prod_ofr (prod_ofr_id),
                                   char_id          BIGINT NOT NULL,
                                   is_mandatory     BOOLEAN NOT NULL DEFAULT FALSE,
                                   is_actv          BOOLEAN NOT NULL DEFAULT TRUE,
                                   cdate            TIMESTAMPTZ,
                                   cuser            VARCHAR(255),
                                   udate            TIMESTAMPTZ,
                                   uuser            VARCHAR(255)
);