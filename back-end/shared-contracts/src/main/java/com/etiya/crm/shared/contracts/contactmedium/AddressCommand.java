package com.etiya.crm.shared.contracts.contactmedium;

import io.swagger.v3.oas.annotations.media.Schema;

/** CreateContactCommand icindeki tek bir ADDR satiri. */
@Schema(description = "Composite create icindeki tek adres.")
public record AddressCommand(

		@Schema(description = "lookup-service CITY grubundaki deger id'si.", example = "201")
		Long cityId,

		@Schema(description = "Cadde/sokak", example = "Ataturk Cad.")
		String streetName,

		@Schema(description = "Bina/kat/daire no", example = "No:12 Kat:3")
		String buildingName,

		@Schema(description = "Serbest metin aciklama", example = "Is yeri")
		String addressDesc,

		@Schema(description = "Onboarding'de ilk adres icin true, digerleri icin false gelir.", example = "true")
		boolean primary) {
}
