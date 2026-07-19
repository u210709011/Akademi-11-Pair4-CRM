package com.etiya.crm.shared.contracts.address;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * contact-info-service PUT /api/v1/addresses/{id} istek govdesi. rowId/dataTypeId
 * burada YOK - URL'deki id'nin kime ait oldugu degismez, sadece adres
 * alanlari guncellenir.
 */
@Schema(description = "PUT /api/v1/addresses/{id} istek govdesi.")
public record UpdateAddressRequest(

		@Schema(description = "lookup-service CITY grubundaki deger id'si.", example = "201")
		@NotNull
		Long cityId,

		@Schema(description = "Cadde/sokak", example = "Ataturk Cad.")
		@NotBlank
		@Size(max = 200)
		String streetName,

		@Schema(description = "Bina/kat/daire no", example = "No:12 Kat:5")
		@NotBlank
		@Size(max = 100)
		String houseName,

		@Schema(description = "Serbest metin aciklama", example = "Is yeri (guncellendi)")
		@NotBlank
		@Size(max = 200)
		String addrDesc,

		@Schema(description = "true ise diger adreslerin primary'si otomatik false yapilir (tek primary kurali).", example = "true")
		boolean primary) {
}
