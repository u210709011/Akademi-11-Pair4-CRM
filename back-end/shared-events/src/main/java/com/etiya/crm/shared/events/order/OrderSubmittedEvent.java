package com.etiya.crm.shared.events.order;

import java.util.UUID;

/**
 * "order-events" topic'inde tasinan OrderSubmitted event 
 * Henuz bir tuketici yok - customer-service'in "order number"
 * ile musteri aramasi bu event'i dinlemeye basladiginda tuketen taraf olacak.
 */
public record OrderSubmittedEvent(
		UUID eventId,
		String type,
		Long custOrdId,
		Long custId,
		Long custAcctId) {
}
