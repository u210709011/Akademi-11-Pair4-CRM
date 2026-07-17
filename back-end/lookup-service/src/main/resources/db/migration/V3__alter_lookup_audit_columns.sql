-- BaseEntity cuser/uuser artik JWT preferred_username (String) tasiyor; BIGINT -> VARCHAR(100).
ALTER TABLE lookup ALTER COLUMN cuser TYPE VARCHAR(100) USING cuser::VARCHAR(100);
ALTER TABLE lookup ALTER COLUMN uuser TYPE VARCHAR(100) USING uuser::VARCHAR(100);
