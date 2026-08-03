package com.etiya.crm.shared.events.customer;

import java.util.UUID;

/**
 * "customer-events" topic'inde tasinan CustomerDeleted event govdesi
 * (yayinlayan: customer-service, tuketen: party-service ve contact-info-service).
 * eventId, inbox idempotency anahtari olarak kullanilir - onceden bu alan
 * yoktu ve party-service partyRoleId'den deterministik bir UUID turetiyordu,
 * artik gerek kalmadi. dataTypeId sadece contact-info-service tarafindan
 * kullanilir (cascade adres/contact medium deactivate icin rowId+dataTypeId
 * polimorfik anahtari) - customer-service publish anında kendi
 * LookupCacheService'i ile dinamik cozer, contact-info-service tarafinda
 * hardcode edilmez (party-service bu alani yok sayar).
 */
public record CustomerDeletedEvent(
		UUID eventId,
		String type,
		Long custId,
		Long partyRoleId,
		Long dataTypeId) {
}
