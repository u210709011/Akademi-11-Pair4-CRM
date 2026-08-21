package com.etiya.crm.shared.events.saga;

import java.util.UUID;

/** "customer-deletion-saga-events" topic govdesi - bir saga adiminin basari/hata sonucu. */
public record SagaStepResultEvent(
		UUID eventId,
		String type,
		Long custId,
		String stepName,
		boolean success,
		String reason) {
}
