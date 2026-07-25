-- Product-service: PROD_SPEC, PROD_CATAL, CMPG, PROD_OFR, PROD_CATAL_PROD_OFR,
-- CMPG_PROD_OFR, PROD_SPEC_RSRC_SPEC, PROD_SPEC_SRVC_SPEC, PROD, PROD_CHAR_VAL, PROD_REL.
-- Lookup id'leri (rsrc_spec_id, srvc_spec_id, char_id, char_val_id, rel_tp_id, st_id)
-- duz BIGINT kolonlardir; lookup-service ayri serviste oldugu icin FK kurulmaz.
-- TRNSC_ID kolonu bilerek acilmadi: sprint plani geregi kullanilmiyor, hep null kaliyordu.

-- 1) Bagimsiz tablolar

CREATE TABLE prod_spec (
                           prod_spec_id BIGSERIAL PRIMARY KEY,
                           name         VARCHAR(100) NOT NULL,
                           descr        VARCHAR(100) NOT NULL,
                           st_id        BIGINT NOT NULL,
                           is_dev       BOOLEAN NOT NULL DEFAULT FALSE,
                           cdate        TIMESTAMPTZ,
                           cuser        VARCHAR(255),
                           udate        TIMESTAMPTZ,
                           uuser        VARCHAR(255)
);

CREATE TABLE prod_catal (
                            prod_catal_id BIGSERIAL PRIMARY KEY,
                            name          VARCHAR(100) NOT NULL,
                            descr         VARCHAR(100) NOT NULL,
                            st_id         BIGINT NOT NULL,
                            shrt_code     VARCHAR(64),
                            cdate         TIMESTAMPTZ,
                            cuser         VARCHAR(255),
                            udate         TIMESTAMPTZ,
                            uuser         VARCHAR(255)
);

CREATE TABLE cmpg (
                      cmpg_id      BIGSERIAL PRIMARY KEY,
                      name         VARCHAR(100) NOT NULL,
                      descr        VARCHAR(100) NOT NULL,
                      cmpg_code    VARCHAR(50) NOT NULL,
                      actvt_edate  DATE,
                      st_id        BIGINT NOT NULL,
                      is_penalty   BOOLEAN NOT NULL DEFAULT FALSE,
                      cdate        TIMESTAMPTZ,
                      cuser        VARCHAR(255),
                      udate        TIMESTAMPTZ,
                      uuser        VARCHAR(255)
);

-- 2) prod_spec'e bagimli tablo (+ kendine referans)

CREATE TABLE prod_ofr (
                          prod_ofr_id       BIGSERIAL PRIMARY KEY,
                          prod_spec_id      BIGINT NOT NULL REFERENCES prod_spec (prod_spec_id),
                          name              VARCHAR(100) NOT NULL,
                          descr             VARCHAR(100) NOT NULL,
                          prnt_ofr_id       BIGINT REFERENCES prod_ofr (prod_ofr_id),
                          st_id             BIGINT NOT NULL,
                          prod_ofr_total_prc NUMERIC(16,2) NOT NULL,
                          cdate             TIMESTAMPTZ,
                          cuser             VARCHAR(255),
                          udate             TIMESTAMPTZ,
                          uuser             VARCHAR(255)
);

-- 3) Junction tablolar (prod_catal/cmpg <-> prod_ofr)

CREATE TABLE prod_catal_prod_ofr (
                                     prod_catal_prod_ofr_id BIGSERIAL PRIMARY KEY,
                                     prod_catal_id          BIGINT NOT NULL REFERENCES prod_catal (prod_catal_id),
                                     prod_ofr_id            BIGINT NOT NULL REFERENCES prod_ofr (prod_ofr_id),
                                     st_id                  BIGINT NOT NULL,
                                     cdate                  TIMESTAMPTZ,
                                     cuser                  VARCHAR(255),
                                     udate                  TIMESTAMPTZ,
                                     uuser                  VARCHAR(255)
);

CREATE TABLE cmpg_prod_ofr (
                               cmpg_prod_ofr_id BIGSERIAL PRIMARY KEY,
                               cmpg_id          BIGINT NOT NULL REFERENCES cmpg (cmpg_id),
                               prod_ofr_id      BIGINT NOT NULL REFERENCES prod_ofr (prod_ofr_id),
                               prod_ofr_name    VARCHAR(100) NOT NULL,
                               prio             INTEGER,
                               sdate            DATE NOT NULL,
                               edate            DATE,
                               is_actv          BOOLEAN NOT NULL DEFAULT TRUE,
                               cdate            TIMESTAMPTZ,
                               cuser            VARCHAR(255),
                               udate            TIMESTAMPTZ,
                               uuser            VARCHAR(255)
);

-- 4) prod_spec <-> lookup-service junction'lari (RSRC_SPEC/SRVC_SPEC orada, FK yok)

CREATE TABLE prod_spec_rsrc_spec (
                                     prod_spec_rsrc_spec_id BIGSERIAL PRIMARY KEY,
                                     prod_spec_id           BIGINT NOT NULL REFERENCES prod_spec (prod_spec_id),
                                     rsrc_spec_id           BIGINT NOT NULL,
                                     rel_tp_id               BIGINT NOT NULL,
                                     sdate                   DATE NOT NULL,
                                     edate                   DATE,
                                     st_id                   BIGINT NOT NULL,
                                     cdate                   TIMESTAMPTZ,
                                     cuser                   VARCHAR(255),
                                     udate                   TIMESTAMPTZ,
                                     uuser                   VARCHAR(255)
);

CREATE TABLE prod_spec_srvc_spec (
                                     prod_spec_srvc_spec_id BIGSERIAL PRIMARY KEY,
                                     prod_spec_id           BIGINT NOT NULL REFERENCES prod_spec (prod_spec_id),
                                     srvc_spec_id           BIGINT NOT NULL,
                                     rel_tp_id               BIGINT NOT NULL,
                                     sdate                   DATE NOT NULL,
                                     edate                   DATE,
                                     st_id                   BIGINT NOT NULL,
                                     cdate                   TIMESTAMPTZ,
                                     cuser                   VARCHAR(255),
                                     udate                   TIMESTAMPTZ,
                                     uuser                   VARCHAR(255)
);

-- 5) prod - zincirin somutlastigi tablo (+ kendine referans)

CREATE TABLE prod (
                      prod_id      BIGSERIAL PRIMARY KEY,
                      prnt_prod_id BIGINT REFERENCES prod (prod_id),
                      prod_ofr_id  BIGINT NOT NULL REFERENCES prod_ofr (prod_ofr_id),
                      prod_spec_id BIGINT NOT NULL REFERENCES prod_spec (prod_spec_id),
                      name         VARCHAR(100),
                      descr        VARCHAR(100),
                      cmpg_id      BIGINT REFERENCES cmpg (cmpg_id),
                      st_id        BIGINT NOT NULL,
                      cdate        TIMESTAMPTZ,
                      cuser        VARCHAR(255),
                      udate        TIMESTAMPTZ,
                      uuser        VARCHAR(255)
);

-- 6) prod'a bagimli tablolar

CREATE TABLE prod_char_val (
                               prod_char_val_id BIGSERIAL PRIMARY KEY,
                               prod_id          BIGINT NOT NULL REFERENCES prod (prod_id),
                               char_id          BIGINT NOT NULL,
                               char_val_id      BIGINT,
                               val              VARCHAR(300),
                               st_id            BIGINT,
                               cdate            TIMESTAMPTZ,
                               cuser            VARCHAR(255),
                               udate            TIMESTAMPTZ,
                               uuser            VARCHAR(255)
);

CREATE TABLE prod_rel (
                          prod_rel_id BIGSERIAL PRIMARY KEY,
                          prod_id1    BIGINT NOT NULL REFERENCES prod (prod_id),
                          prod_id2    BIGINT NOT NULL REFERENCES prod (prod_id),
                          rel_tp_id   BIGINT NOT NULL,
                          is_actv     BOOLEAN NOT NULL DEFAULT TRUE,
                          cdate       TIMESTAMPTZ,
                          cuser       VARCHAR(255),
                          udate       TIMESTAMPTZ,
                          uuser       VARCHAR(255)
);