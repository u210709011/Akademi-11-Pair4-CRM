-- infra/debezium/outbox-table.sql ile birebir ayni: Debezium EventRouter SMT bu
-- sema ile "public.outbox" tablosunu okuyup "<aggregate_type>-events" topic'ine yayinlar.
-- Bu serviste aggregate_type = 'customer' -> topic: customer-events

CREATE TABLE outbox (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_id   VARCHAR(255) NOT NULL,
    type           VARCHAR(255) NOT NULL,
    payload        JSONB        NOT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT now()
);

-- Inbox: idempotent consumer. party-events topic'inden tuketilen event id'leri
-- burada saklanir; ayni event tekrar teslim edilirse islenmez.
CREATE TABLE inbox (
    event_id     UUID PRIMARY KEY,
    event_type   VARCHAR(255) NOT NULL,
    processed_at TIMESTAMP    NOT NULL DEFAULT now()
);
