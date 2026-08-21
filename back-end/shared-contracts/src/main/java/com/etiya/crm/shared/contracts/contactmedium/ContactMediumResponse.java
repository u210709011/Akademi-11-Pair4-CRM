package com.etiya.crm.shared.contracts.contactmedium;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;

import com.etiya.crm.shared.contracts.constants.SwaggerText;

/** contact-info-service GET/POST/PUT /api/v1/contact-mediums yanit govdesi. */
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = SwaggerText.CONTACT_MEDIUM_RESPONSE_DESCRIPTION)
public record ContactMediumResponse(

		@Schema(description = SwaggerText.CONTACT_MEDIUM_RESPONSE_ID_DESCRIPTION, example = "17")
		Long id,

		@Schema(description = SwaggerText.CONTACT_MEDIUM_RESPONSE_ROW_ID_DESCRIPTION, example = "1")
		Long rowId,

		@Schema(description = SwaggerText.DATA_TYPE_ID_LOOKUP_DESCRIPTION, example = "102")
		Long dataTypeId,

		String cntcData,

		@Schema(description = SwaggerText.CNTC_MEDIUM_TYPE_ID_DESCRIPTION,
				example = "4001")
		Long cntcMediumTypeId,

		Instant cdate,

		String cuser,

		Instant udate,

		String uuser) {
}
