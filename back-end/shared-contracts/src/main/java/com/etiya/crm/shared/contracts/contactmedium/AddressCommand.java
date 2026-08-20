package com.etiya.crm.shared.contracts.contactmedium;

import io.swagger.v3.oas.annotations.media.Schema;

import com.etiya.crm.shared.contracts.constants.SwaggerText;

/** CreateContactCommand icindeki tek bir ADDR satiri. */
@Schema(description = SwaggerText.ADDRESS_COMMAND_DESCRIPTION)
public record AddressCommand(

		@Schema(description = SwaggerText.CITY_ID_DESCRIPTION, example = "201")
		Long cityId,

		@Schema(description = SwaggerText.STREET_NAME_DESCRIPTION, example = "Ataturk Cad.")
		String streetName,

		@Schema(description = SwaggerText.HOUSE_NAME_DESCRIPTION, example = "No:12 Kat:3")
		String buildingName,

		@Schema(description = SwaggerText.ADDRESS_DESC_DESCRIPTION, example = "Is yeri")
		String addressDesc,

		@Schema(description = SwaggerText.ADDRESS_COMMAND_PRIMARY_DESCRIPTION, example = "true")
		boolean primary) {
}
