package com.etiya.crm.shared.contracts.contactmedium;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.etiya.crm.shared.contracts.constants.SwaggerText;

/** contact-info-service PUT /api/v1/contact-mediums/{id} istek govdesi. */
@Schema(description = SwaggerText.UPDATE_CONTACT_MEDIUM_REQUEST_DESCRIPTION)
public record UpdateContactMediumRequest(

		@Schema(description = SwaggerText.CONTACT_DATA_DESCRIPTION, example = "ahmet.yilmazoglu@example.com")
		@NotBlank
		@Size(max = 100)
		String cntcData,

		@Schema(description = SwaggerText.CNTC_MEDIUM_TYPE_ID_DESCRIPTION,
				example = "4001")
		@NotNull
		Long cntcMediumTypeId) {
}
