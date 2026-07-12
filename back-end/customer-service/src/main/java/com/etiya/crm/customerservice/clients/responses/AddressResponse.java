package com.etiya.crm.customerservice.clients.responses;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** contact-info-service GET/POST/PUT /api/v1/addresses yanit govdesi. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AddressResponse(
		Long id,
		Long rowId,
		Long dataTypeId,
		Long cityId,
		String streetName,
		String houseName,
		String addrDesc,
		boolean primary,
		Instant cdate,
		Instant udate) {
}
