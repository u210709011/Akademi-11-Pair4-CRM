-- Arama sonucuna Ikinci Ad + Rol + GSM kolonlari eklenir.
-- middle_name/gsm: customer-service'in kendi bildigi degerler, onboarding'de
-- senkron yazilir (bkz. CustomerServiceImpl.createSearchView).
-- role: party-service'in karari (PartyRole.partyRoleTypeId) - sadece
-- PartyEventListener (async, party-events) tarafindan doldurulur.
-- gsm: contact-info-service'in karari (CNTC_MEDIUM) - sadece
-- ContactMediumEventListener (async, contact-medium-events) tarafindan doldurulur.
ALTER TABLE customer_search_view ADD COLUMN middle_name VARCHAR(100);
ALTER TABLE customer_search_view ADD COLUMN role VARCHAR(100);
ALTER TABLE customer_search_view ADD COLUMN gsm VARCHAR(15);

CREATE INDEX idx_csv_gsm ON customer_search_view (gsm);
