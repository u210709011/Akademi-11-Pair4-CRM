package com.etiya.crm.customerservice.clients;

/** contact-info-service PUT /api/v1/addresses/{id} istek govdesi. */
public record UpdateAddressCommand(
		Long cityId,
		String streetName,
		String houseName,
		String addrDesc,
		boolean primary) {
}
