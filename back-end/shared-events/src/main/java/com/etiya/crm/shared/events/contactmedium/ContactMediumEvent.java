package com.etiya.crm.shared.events.contactmedium;

import java.util.UUID;

/**
 * "contact-medium-events" topic'inde tasinan event govdesi (yayinlayan:
 * contact-info-service, tuketen: customer-service). contact-info-service
 * rowId/dataTypeId'nin kime ait oldugunu bilmez/bilmemelidir (polimorfik,
 * genericttir) - filtreleme (ornn. sadece DATA_TYPE=CUST + CNTC_MEDIUM_TYPE=
 * MOBILE_PHONE) tamamen tuketici tarafinda yapilir, bkz. PartyEvent'teki ayni
 * prensip.
 */
public record ContactMediumEvent(
		UUID eventId,
		String type,
		Long rowId,
		Long dataTypeId,
		Long cntcMediumTypeId,
		String cntcData) {
}
