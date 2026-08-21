package com.etiya.crm.customerservice.constants;

/** DltAlertNotifier.alert() cagrilarinda kullanilan listener/step adlari. */
public final class AlertTags {

	public static final String PARTY_EVENT_LISTENER = "PartyEventListener";
	public static final String CONTACT_MEDIUM_EVENT_LISTENER = "ContactMediumEventListener";
	public static final String SAGA_STEP_RESULT_EVENT_LISTENER = "SagaStepResultEventListener";
	public static final String SAGA_ORCHESTRATOR = "CustomerDeletionSagaOrchestrator";
	public static final String SAGA_STEP_FAILED = "SagaStepFailed";

	private AlertTags() {
	}
}
