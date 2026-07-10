package com.etiya.crm.customerservice.clients;

/** contact-info-service PUT /api/v1/contact-mediums/{id} istek govdesi. */
public record UpdateContactMediumCommand(
		String cntcData,
		Long cntcMediumTypeId) {
}
