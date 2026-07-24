-- infra/debezium/outbox-table.sql ile birebir ayni: Debezium EventRouter SMT bu
-- tabloyu okuyup "<aggregate_type>-events" topic'ine yayinlar.
-- Bu serviste aggregate_type = 'contact-medium' -> topic: contact-medium-events
--
-- baseline-version=1'in USTUNDE oldugu icin bu migration, halihazirda calisan
-- (Hibernate ddl-auto: update ile "outbox" tablosunu zaten kendiliginden olusturmus
-- olabilecek) ortamlarda da GERCEKTEN CALISIR - IF NOT EXISTS bu durumda no-op'tur,
-- fresh bir ortamda ise tabloyu doğru semayla kurar.

CREATE TABLE IF NOT EXISTS outbox (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_id   VARCHAR(255) NOT NULL,
    type           VARCHAR(255) NOT NULL,
    payload        JSONB        NOT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT now()
);
