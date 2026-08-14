-- role (VARCHAR) yazildigi anda donduruldugu icin dile gore yeniden cevrilemiyor (bkz.
-- PartyEventHandler/CustomerOnboardingServiceImpl). partyRoleTypeId, arama sirasinda
-- lookup-service'ten istekteki dile gore YENIDEN cozulebilmesi icin eklenir - role kolonu
-- geriye donuk uyumluluk icin degismeden kalir.
ALTER TABLE customer_search_view ADD COLUMN party_role_type_id BIGINT;
