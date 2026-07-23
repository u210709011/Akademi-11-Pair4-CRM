package com.etiya.crm.customerservice.business.dtos.requests;

import com.etiya.crm.customerservice.constants.MessageKeys;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Adres ekleme/guncelleme istegi. AddressInfo'dan (onboarding) farki:
 * primary alani burada acikca istemciden gelir - onboarding'deki "ilk adres
 * primary" kurali sadece ilk kayit anina ozeldir.
 */
@Schema(description = "POST/PUT /api/v1/customers/{custId}/addresses(/{addressId}) istek govdesi. "
		+ "primary=true gonderilirse musterinin diger adreslerinin primary'si otomatik false yapilir (tek primary kurali).")
public record AddressEditRequest(

		@Schema(description = "lookup-service CITY grubundaki deger id'si (seed'de tek deger: 201=Ankara).", example = "201")
		@NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		Long cityId,

		@Schema(description = "Cadde/sokak", example = "Cumhuriyet Mah.")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String streetName,

		@Schema(description = "Bina/kat/daire no", example = "No:5 D:2")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String buildingName,

		@Schema(description = "Serbest metin aciklama - zorunlu (contact-info-service.CreateAddressRequest/UpdateAddressRequest.addrDesc @NotBlank).",
				example = "Ev adresi")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String addressDesc,

		@Schema(description = "true gonderilirse bu adres primary yapilir, musterinin diger adresleri otomatik primary=false olur.",
				example = "false")
		boolean primary) {
}
