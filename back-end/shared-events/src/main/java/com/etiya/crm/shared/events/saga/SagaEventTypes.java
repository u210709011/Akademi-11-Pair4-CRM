package com.etiya.crm.shared.events.saga;

/** "customer-deletion-saga-events" topic'inde tasinan event'lerin "type" degerleri. */
public final class SagaEventTypes {

	public static final String STEP_SUCCEEDED = "SagaStepSucceeded";
	public static final String STEP_FAILED = "SagaStepFailed";

	private SagaEventTypes() {
	}
}
