package com.etiya.crm.shared.events.party;

import java.util.UUID;

/**
 * "party-events" topic'inde tasinan event govdesi (yayinlayan: party-service,
 * tuketen: customer-service). "type" alani payload'un icine kasitli olarak
 * gomulur (Debezium'un eventType header'indan bagimsiz, self-describing) -
 * bkz. PartyEventTypes.
 */
public record PartyEvent(
		UUID eventId,
		String type,
		Long partyRoleId,
		String firstName,
		String lastName,
		String nationalId) {
}
