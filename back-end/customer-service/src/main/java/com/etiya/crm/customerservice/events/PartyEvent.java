package com.etiya.crm.customerservice.events;

/**
 * party-events topic'inden tuketilen event govdesi. custId party-service'te
 * bilinmedigi icin partyRoleId ile tasinir; dinleyici bu partyRoleId'den
 * kendi custId'sini bulur.
 */
public record PartyEvent(
		String eventId,
		String type,
		Long partyRoleId,
		String firstName,
		String lastName,
		String nationalId) {
}
