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
 * Adres ekleme/guncelleme istegi. AddressInfo'dan (onboarding) farki:
 * primary alani burada acikca istemciden gelir - onboarding'deki "ilk adres
 * primary" kurali sadece ilk kayit anina ozeldir.
 */
@Schema(description = SwaggerText.ADDRESS_EDIT_REQUEST_SCHEMA_DESCRIPTION)
public record AddressEditRequest(

		@Schema(description = SwaggerText.ADDRESS_INFO_CITY_ID_DESCRIPTION, example = "5")
		@NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@ExistsInLookupGroup(group = GnlTpGroups.CITY, message = "{" + MessageKeys.CITY_INVALID + "}")
		Long cityId,

		@Schema(description = SwaggerText.ADDRESS_INFO_STREET_NAME_DESCRIPTION, example = "Cumhuriyet Mah.")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@Size(max = 200, message = "{" + MessageKeys.FIELD_MAX_LENGTH + "}")
		String streetName,

		@Schema(description = SwaggerText.ADDRESS_INFO_BUILDING_NAME_DESCRIPTION, example = "No:5 D:2")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String buildingName,

		@Schema(description = SwaggerText.ADDRESS_INFO_ADDRESS_DESC_DESCRIPTION, example = "Ev adresi")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String addressDesc,

		@Schema(description = SwaggerText.ADDRESS_EDIT_REQUEST_PRIMARY_DESCRIPTION, example = "false")
		boolean primary) {
}
