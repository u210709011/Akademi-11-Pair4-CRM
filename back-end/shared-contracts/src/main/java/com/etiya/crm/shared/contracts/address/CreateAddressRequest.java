package com.etiya.crm.shared.contracts.address;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.etiya.crm.shared.contracts.constants.SwaggerText;

/**
 * contact-info-service POST /api/v1/addresses istek govdesi. rowId+dataTypeId,
 * adresin kime ait oldugunu belirtir (polimorfik iliski) - caller bilmek
 * zorunda; ornek: customer icin rowId=custId, dataTypeId=lookup-service'in
 * TYPE_VALUE tablosundan dinamik cozulen CUST etiketi (bkz. customer-service
 * LookupCacheService.resolveDataTypeId).
 */
@Schema(description = SwaggerText.CREATE_ADDRESS_REQUEST_DESCRIPTION)
public record CreateAddressRequest(

		@Schema(description = SwaggerText.OWNER_ROW_ID_DESCRIPTION, example = "1")
		@NotNull
		Long rowId,

		@Schema(description = SwaggerText.CREATE_ADDRESS_DATA_TYPE_ID_DESCRIPTION, example = "12")
		@NotNull
		Long dataTypeId,

		@Schema(description = SwaggerText.CITY_ID_DESCRIPTION, example = "201")
		@NotNull
		Long cityId,

		@Schema(description = SwaggerText.STREET_NAME_DESCRIPTION, example = "Ataturk Cad.")
		@NotBlank
		@Size(max = 200)
		String streetName,

		@Schema(description = SwaggerText.HOUSE_NAME_DESCRIPTION, example = "No:12 Kat:3")
		@NotBlank
		@Size(max = 100)
		String houseName,

		@Schema(description = SwaggerText.ADDRESS_DESC_DESCRIPTION, example = "Is yeri")
		@NotBlank
		@Size(max = 200)
		String addrDesc,

		@Schema(description = SwaggerText.ADDRESS_PRIMARY_DESCRIPTION, example = "false")
		boolean primary) {
}
