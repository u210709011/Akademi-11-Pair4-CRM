package com.etiya.crm.shared.contracts.contactmedium;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.etiya.crm.shared.contracts.constants.SwaggerText;

/** contact-info-service POST /api/v1/contact-mediums/single istek govdesi. */
@Schema(description = SwaggerText.CREATE_CONTACT_MEDIUM_REQUEST_DESCRIPTION)
public record CreateContactMediumRequest(

		@Schema(description = SwaggerText.CREATE_CONTACT_MEDIUM_ROW_ID_DESCRIPTION, example = "1")
		@NotNull
		Long rowId,

		@Schema(description = SwaggerText.CREATE_CONTACT_MEDIUM_DATA_TYPE_ID_DESCRIPTION, example = "102")
		@NotNull
		Long dataTypeId,

		@Schema(description = SwaggerText.CONTACT_DATA_DESCRIPTION, example = "ahmet.yilmaz@example.com")
		@NotBlank
		@Size(max = 100)
		String cntcData,

		@Schema(description = SwaggerText.CNTC_MEDIUM_TYPE_ID_DESCRIPTION,
				example = "4001")
		@NotNull
		Long cntcMediumTypeId) {
}
