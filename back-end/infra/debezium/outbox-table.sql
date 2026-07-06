-- Transactional Outbox tablosu.
-- Her servis kendi veritabaninda bu tabloyu olusturur; Debezium EventRouter SMT
-- bu tabloyu okuyarak "<aggregate_type>-events" topic'ine event yayinlar.
-- Ornek: aggregate_type = 'customer'  ->  topic: customer-events

CREATE TABLE IF NOT EXISTS outbox (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_type VARCHAR(255) NOT NULL,  -- ornn. 'customer'
    aggregate_id   VARCHAR(255) NOT NULL,  -- ornn. musteri id'si (Kafka key olur)
    type           VARCHAR(255) NOT NULL,  -- ornn. 'CustomerCreated'
    payload        JSONB        NOT NULL,  -- event icerigi
    created_at     TIMESTAMP    NOT NULL DEFAULT now()
);

-- Inbox tablosu (idempotent consumer).
-- Kafka ayni event'i birden fazla kez teslim edebilir; islenen event id'leri
-- burada saklanir ve tekrar islenmez.

CREATE TABLE IF NOT EXISTS inbox (
    event_id     UUID PRIMARY KEY,        -- outbox.id ile ayni deger
    event_type   VARCHAR(255) NOT NULL,
    processed_at TIMESTAMP    NOT NULL DEFAULT now()
);
