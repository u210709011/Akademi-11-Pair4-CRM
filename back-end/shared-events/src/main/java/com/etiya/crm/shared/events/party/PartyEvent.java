package com.etiya.crm.shared.events.party;

import java.util.UUID;

/**
 * "party-events" topic'inde tasinan event govdesi (yayinlayan: party-service,
 * tuketen: customer-service). "type" alani payload'un icine kasitli olarak
 * gomulur (Debezium'un eventType header'indan bagimsiz, self-describing) -
 * bkz. PartyEventTypes.
 *
 * partyRoleTypeId lookup-service PARTY_ROLE_TYPE grubundaki deger id'sidir
 * (bugun icin hep CUSTOMER/2001 - party-service'in tek rol atama akisi bu);
 * customer-service kendi LookupCacheService'iyle gosterim metnine cevirir.
 */
public record PartyEvent(
		UUID eventId,
		String type,
		Long partyRoleId,
		String firstName,
		String middleName,
		String lastName,
		String nationalId,
		Long partyRoleTypeId) {
}
