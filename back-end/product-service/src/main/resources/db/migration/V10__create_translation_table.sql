-- lookup-service'teki translation tablosuyla (V14) birebir ayni sekil - servisler arasi DB
-- paylasimi olmadigi icin (her servis kendi Postgres DB'sine sahip) burada tekrarlanir. Genel,
-- genisleyebilir ceviri tablosu: hicbir tabloya dil basina kolon eklenmez. Varsayilan dil
-- Ingilizce'dir - name/descr gibi kolonlar dogrudan Ingilizce tutulur, bu tablo sadece
-- varsayilan-disi diller icin bir overlay saglar.
CREATE TABLE translation (
    translation_id BIGSERIAL PRIMARY KEY,
    entity_name     VARCHAR(60)  NOT NULL,
    entity_id       BIGINT       NOT NULL,
    field_name      VARCHAR(40)  NOT NULL,
    locale          VARCHAR(10)  NOT NULL,
    value           VARCHAR(200) NOT NULL,
    cdate TIMESTAMP, cuser VARCHAR(100), udate TIMESTAMP, uuser VARCHAR(100),
    CONSTRAINT uq_translation UNIQUE (entity_name, entity_id, field_name, locale)
);

CREATE INDEX idx_translation_lookup ON translation (entity_name, entity_id, locale);
