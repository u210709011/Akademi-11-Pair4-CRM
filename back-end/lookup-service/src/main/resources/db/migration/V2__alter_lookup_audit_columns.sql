-- cuser/uuser: JWT'den gelen preferred_username (string) ile hizalamak icin
-- BIGINT yerine VARCHAR. Diger servislerdeki (party/customer/contact-info)
-- JWT tabanli auditing ile ayni tip.
ALTER TABLE lookup ALTER COLUMN cuser TYPE VARCHAR(255) USING cuser::varchar;
ALTER TABLE lookup ALTER COLUMN uuser TYPE VARCHAR(255) USING uuser::varchar;
