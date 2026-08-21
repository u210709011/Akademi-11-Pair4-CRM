-- CustomerDeletionSagaOrchestrator'in durum makinesi: musteri silme cascade'inin
-- (party-service + contact-info-service) her adiminin sonucunu izler. cust_id, bir
-- silme saga'sinin dogal/tek korelasyon anahtaridir (bkz. shared-events.saga paketi
-- - sagaId ayrica tasinmaz, CustomerDeletedEvent zaten custId iceriyor).
CREATE TABLE customer_deletion_saga (
    cust_id                    BIGINT PRIMARY KEY,
    status                     VARCHAR(20) NOT NULL,
    party_deactivation_success BOOLEAN,
    contact_info_success       BOOLEAN,
    cdate                      TIMESTAMP NOT NULL DEFAULT now(),
    udate                      TIMESTAMP
);
