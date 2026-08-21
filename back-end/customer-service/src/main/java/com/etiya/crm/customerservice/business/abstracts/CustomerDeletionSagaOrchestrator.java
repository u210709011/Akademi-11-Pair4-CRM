package com.etiya.crm.customerservice.business.abstracts;

/** Musteri silme cascade'inin (party-service + contact-info-service) saga durum makinesi. */
public interface CustomerDeletionSagaOrchestrator {

	/** softDelete ile AYNI transaction'da cagrilir. */
	void start(Long custId);

	/** Bir adimin sonucunu isler; success=false ise telafi tetiklenir. */
	void handleStepResult(Long custId, String stepName, boolean success, String reason);
}
