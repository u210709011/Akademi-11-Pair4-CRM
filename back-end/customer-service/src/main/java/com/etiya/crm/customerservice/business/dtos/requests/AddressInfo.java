package com.etiya.crm.customerservice.business.dtos.requests;

import com.etiya.crm.customerservice.constants.MessageKeys;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * contact-info-service'e (ADDR) gidecek adres bilgisi. ACC-015.
 * "primary" alani UI'da yok; ilk eklenen adres server-side primary sayilir
 * (bkz. CustomerBusinessRules). Onboarding disinda adres eklemek/guncellemek
 * icin bkz. AddressEditRequest (orada primary aciktan gelir).
 */
@Schema(description = "Onboarding sirasinda girilen adres bilgisi (1-5 adet).")
public record AddressInfo(

		@Schema(description = "lookup-service CITY grubundaki deger id'si (seed'de tek deger: 201=Ankara).", example = "201")
		@NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		Long cityId,

		@Schema(description = "Cadde/sokak", example = "Ataturk Cad.")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String streetName,

		@Schema(description = "Bina/kat/daire no", example = "No:12 Kat:3")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String buildingName, // House/Flat Number bu alana yazilir (ADDR.BLDG_NAME)

		@Schema(description = "Serbest metin aciklama - zorunlu (contact-info-service.CreateAddressRequest.addrDesc @NotBlank).",
				example = "Is yeri")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String addressDesc) {
}
