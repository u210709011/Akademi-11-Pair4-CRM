package com.etiya.crm.customerservice.clients.commands;

/** contact-info-service POST /api/v1/contact-mediums/single istek govdesi. */
public record CreateContactMediumCommand(
		Long rowId,
		Long dataTypeId,
		String cntcData,
		Long cntcMediumTypeId) {
}
