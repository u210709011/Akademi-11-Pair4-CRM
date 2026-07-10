package com.etiya.crm.shared.events.customer;

import java.util.UUID;

/**
 * "customer-events" topic'inde tasinan CustomerDeleted event govdesi
 * (yayinlayan: customer-service, tuketen: party-service). eventId, inbox
 * idempotency anahtari olarak kullanilir - onceden bu alan yoktu ve
 * party-service partyRoleId'den deterministik bir UUID turetiyordu, artik
 * gerek kalmadi.
 */
public record CustomerDeletedEvent(
		UUID eventId,
		String type,
		Long custId,
		Long partyRoleId) {
}
