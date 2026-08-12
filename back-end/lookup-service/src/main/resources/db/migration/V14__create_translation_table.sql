-- Genel, genisleyebilir ceviri tablosu: hicbir tabloya dil basina kolon eklenmez. Ayni
-- polimorfik referans deseni type_value'de (table_name/field_name) zaten kullaniliyor - burada
-- entity_name/entity_id/field_name olarak tekrarlanir, gercek FK yoktur.
--
-- Varsayilan dil INGILIZCE'dir: her tablonun name/descr gibi kolonlari dogrudan Ingilizce
-- tutar (bkz. V15). Bu tablo SADECE varsayilan-disi diller (once Turkce) icin bir "overlay"
-- satiri saglar - Ingilizce istekte hic sorgulanmaz.
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
