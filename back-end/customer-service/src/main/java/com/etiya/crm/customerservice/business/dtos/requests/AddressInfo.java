package com.etiya.crm.customerservice.business.dtos.requests;

import com.etiya.crm.customerservice.constants.MessageKeys;
import com.etiya.crm.customerservice.constants.SwaggerText;
import com.etiya.crm.shared.contracts.gnltp.GnlTpGroups;
import com.etiya.crm.shared.contracts.validation.ExistsInLookupGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * contact-info-service'e (ADDR) gidecek adres bilgisi. ACC-015.
 * "primary" alani UI'da yok; ilk eklenen adres server-side primary sayilir
 * (bkz. AddressBusinessRules). Onboarding disinda adres eklemek/guncellemek
 * icin bkz. AddressEditRequest (orada primary aciktan gelir).
 */
@Schema(description = SwaggerText.ADDRESS_INFO_SCHEMA_DESCRIPTION)
public record AddressInfo(

		@Schema(description = SwaggerText.ADDRESS_INFO_CITY_ID_DESCRIPTION, example = "5")
		@NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@ExistsInLookupGroup(group = GnlTpGroups.CITY, message = "{" + MessageKeys.CITY_INVALID + "}")
		Long cityId,

		@Schema(description = SwaggerText.ADDRESS_INFO_STREET_NAME_DESCRIPTION, example = "Ataturk Cad.")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@Size(max = 200, message = "{" + MessageKeys.FIELD_MAX_LENGTH + "}")
		String streetName,

		@Schema(description = SwaggerText.ADDRESS_INFO_BUILDING_NAME_DESCRIPTION, example = "No:12 Kat:3")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String buildingName, // House/Flat Number bu alana yazilir (ADDR.BLDG_NAME)

		@Schema(description = SwaggerText.ADDRESS_INFO_ADDRESS_DESC_DESCRIPTION, example = "Is yeri")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String addressDesc) {
}
