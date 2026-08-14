package com.etiya.crm.customerservice.business.dtos.requests;

import com.etiya.crm.customerservice.constants.MessageKeys;
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
@Schema(description = "POST/PUT /api/v1/customers/{custId}/addresses(/{addressId}) istek govdesi. "
		+ "primary=true gonderilirse musterinin diger adreslerinin primary'si otomatik false yapilir (tek primary kurali).")
public record AddressEditRequest(

		@Schema(description = "Sehir id'si - GET /api/v1/general-types/resolve/CITY/{shrtCode} ile alinmalidir.",
				example = "5")
		@NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@ExistsInLookupGroup(group = GnlTpGroups.CITY, message = "{" + MessageKeys.CITY_INVALID + "}")
		Long cityId,

		@Schema(description = "Cadde/sokak", example = "Cumhuriyet Mah.")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@Size(max = 200, message = "{" + MessageKeys.FIELD_MAX_LENGTH + "}")
		String streetName,

		@Schema(description = "Bina/kat/daire no", example = "No:5 D:2")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String buildingName,

		@Schema(description = "Serbest metin aciklama - zorunlu", example = "Ev adresi")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String addressDesc,

		@Schema(description = "true gonderilirse bu adres primary yapilir, musterinin diger adresleri otomatik primary=false olur.",
				example = "false")
		boolean primary) {
}
