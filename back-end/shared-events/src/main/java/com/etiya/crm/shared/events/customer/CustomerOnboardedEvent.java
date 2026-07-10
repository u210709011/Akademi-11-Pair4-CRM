package com.etiya.crm.shared.events.customer;

import java.util.UUID;

/**
 * "customer-events" topic'inde tasinan CustomerOnboarded event govdesi
 * (yayinlayan: customer-service). Su an aktif bir tuketicisi YOK - bkz. kod
 * incelemesinde raporlanan "gereksiz/kullanilmayan kod" bulgulari. Fact zaten
 * dogru sekilde yayinlaniyor (event-driven mimaride yayinci, tuketici sayisini
 * bilmek zorunda degildir); silinmesi ONERILMEDI, sadece bilginize.
 */
public record CustomerOnboardedEvent(
		UUID eventId,
		String type,
		Long custId,
		Long partyRoleId) {
}
