package com.etiya.crm.shared.contracts.address;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;

import com.etiya.crm.shared.contracts.constants.SwaggerText;

/** contact-info-service GET/POST/PUT /api/v1/addresses yanit govdesi. */
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = SwaggerText.ADDRESS_RESPONSE_DESCRIPTION)
public record AddressResponse(

		@Schema(description = SwaggerText.ADDRESS_ID_DESCRIPTION, example = "42")
		Long id,

		@Schema(description = SwaggerText.OWNER_ROW_ID_DESCRIPTION, example = "1")
		Long rowId,

		@Schema(description = SwaggerText.DATA_TYPE_ID_LOOKUP_DESCRIPTION, example = "102")
		Long dataTypeId,

		@Schema(description = SwaggerText.CITY_ID_DESCRIPTION, example = "201")
		Long cityId,

		String streetName,

		String houseName,

		String addrDesc,

		@Schema(description = SwaggerText.ADDRESS_RESPONSE_PRIMARY_DESCRIPTION)
		boolean primary,

		Instant cdate,

		String cuser,

		Instant udate,

		String uuser) {
}
