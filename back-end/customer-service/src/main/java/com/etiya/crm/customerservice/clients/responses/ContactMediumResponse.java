package com.etiya.crm.customerservice.clients.responses;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** contact-info-service GET/POST/PUT /api/v1/contact-mediums yanit govdesi. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ContactMediumResponse(
		Long id,
		Long rowId,
		Long dataTypeId,
		String cntcData,
		Long cntcMediumTypeId,
		Instant cdate,
		Instant udate) {
}
