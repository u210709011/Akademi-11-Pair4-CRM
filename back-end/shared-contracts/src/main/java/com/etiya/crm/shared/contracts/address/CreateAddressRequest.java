package com.etiya.crm.shared.contracts.address;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * contact-info-service POST /api/v1/addresses istek govdesi. rowId+dataTypeId,
 * adresin kime ait oldugunu belirtir (polimorfik iliski) - caller bilmek
 * zorunda; ornek: customer icin rowId=custId, dataTypeId=102 (DATA_TYPE
 * grubu CUST, bkz. DataTypeIds.CUSTOMER).
 */
@Schema(description = "POST /api/v1/addresses istek govdesi.")
public record CreateAddressRequest(

		@Schema(description = "Adresin sahibinin id'si (ornegin custId).", example = "1")
		@NotNull
		Long rowId,

		@Schema(description = "lookup-service DATA_TYPE grubundaki deger id'si (musteri icin 102).", example = "102")
		@NotNull
		Long dataTypeId,

		@Schema(description = "lookup-service CITY grubundaki deger id'si.", example = "201")
		@NotNull
		Long cityId,

		@Schema(description = "Cadde/sokak", example = "Ataturk Cad.")
		@NotBlank
		@Size(max = 200)
		String streetName,

		@Schema(description = "Bina/kat/daire no", example = "No:12 Kat:3")
		@NotBlank
		@Size(max = 100)
		String houseName,

		@Schema(description = "Serbest metin aciklama", example = "Is yeri")
		@NotBlank
		@Size(max = 200)
		String addrDesc,

		@Schema(description = "true ise diger adreslerin primary'si otomatik false yapilir (tek primary kurali).", example = "false")
		boolean primary) {
}
