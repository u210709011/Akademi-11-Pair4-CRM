package com.etiya.crm.shared.contracts.contactmedium;

import io.swagger.v3.oas.annotations.media.Schema;

import com.etiya.crm.shared.contracts.constants.SwaggerText;

/** CreateContactCommand icindeki tek bir CNTC_MEDIUM satiri. */
@Schema(description = SwaggerText.CONTACT_MEDIUM_COMMAND_DESCRIPTION)
public record ContactMediumCommand(

		@Schema(description = SwaggerText.CNTC_MEDIUM_TYPE_ID_DESCRIPTION,
				example = "4001")
		Long contactMediumTpId,

		@Schema(description = SwaggerText.CONTACT_DATA_DESCRIPTION, example = "ahmet.yilmaz@example.com")
		String contactData) {
}
