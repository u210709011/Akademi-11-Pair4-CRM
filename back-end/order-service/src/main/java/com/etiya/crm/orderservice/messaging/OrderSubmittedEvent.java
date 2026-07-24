package com.etiya.crm.orderservice.messaging;

import java.util.UUID;

/**
 * order-events topic'ine yayinlanacak payload. Su an bu topic'i dinleyen kimse
 * yok (shared-events'te tanimli degil), o yuzden bu sinif sadece order-service'in
 * kendi lokal payload sekli - ileride bir tuketici cikarsa shared-events'e tasinir.
 */
public record OrderSubmittedEvent(
		UUID eventId,
		String eventType,
		Long custOrdId,
		Long custId,
		Long custAcctId) {
}
