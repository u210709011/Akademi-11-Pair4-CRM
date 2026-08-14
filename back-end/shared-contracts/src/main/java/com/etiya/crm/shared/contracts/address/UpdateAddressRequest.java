package com.etiya.crm.shared.contracts.address;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.etiya.crm.shared.contracts.constants.SwaggerText;

/**
 * contact-info-service PUT /api/v1/addresses/{id} istek govdesi. rowId/dataTypeId
 * burada YOK - URL'deki id'nin kime ait oldugu degismez, sadece adres
 * alanlari guncellenir.
 */
@Schema(description = SwaggerText.UPDATE_ADDRESS_REQUEST_DESCRIPTION)
public record UpdateAddressRequest(

		@Schema(description = SwaggerText.CITY_ID_DESCRIPTION, example = "201")
		@NotNull
		Long cityId,

		@Schema(description = SwaggerText.STREET_NAME_DESCRIPTION, example = "Ataturk Cad.")
		@NotBlank
		@Size(max = 200)
		String streetName,

		@Schema(description = SwaggerText.HOUSE_NAME_DESCRIPTION, example = "No:12 Kat:5")
		@NotBlank
		@Size(max = 100)
		String houseName,

		@Schema(description = SwaggerText.ADDRESS_DESC_DESCRIPTION, example = "Is yeri (guncellendi)")
		@NotBlank
		@Size(max = 200)
		String addrDesc,

		@Schema(description = SwaggerText.ADDRESS_PRIMARY_DESCRIPTION, example = "true")
		boolean primary) {
}
