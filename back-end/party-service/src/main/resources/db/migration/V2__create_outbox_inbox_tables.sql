-- infra/debezium/outbox-table.sql ile birebir ayni sema.
-- Debezium EventRouter SMT bu tabloyu okuyup "party-events" topic'ine yayinlar.

CREATE TABLE IF NOT EXISTS outbox (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_id   VARCHAR(255) NOT NULL,
    type           VARCHAR(255) NOT NULL,
    payload        JSONB        NOT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS inbox (
    event_id     UUID PRIMARY KEY,
    event_type   VARCHAR(255) NOT NULL,
    processed_at TIMESTAMP    NOT NULL DEFAULT now()
);
