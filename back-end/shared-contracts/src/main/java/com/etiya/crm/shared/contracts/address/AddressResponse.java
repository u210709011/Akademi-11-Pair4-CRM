package com.etiya.crm.shared.contracts.address;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;

/** contact-info-service GET/POST/PUT /api/v1/addresses yanit govdesi. */
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Adres kaydi.")
public record AddressResponse(

		@Schema(description = "Adresin kendi id'si (guncelleme/silme icin kullanilir).", example = "42")
		Long id,

		@Schema(description = "Adresin sahibinin id'si (ornegin custId).", example = "1")
		Long rowId,

		@Schema(description = "lookup-service DATA_TYPE grubundaki deger id'si.", example = "102")
		Long dataTypeId,

		@Schema(description = "lookup-service CITY grubundaki deger id'si.", example = "201")
		Long cityId,

		String streetName,

		String houseName,

		String addrDesc,

		@Schema(description = "Musterinin birden fazla adresi olabilir, en fazla biri primary=true olur.")
		boolean primary,

		Instant cdate,

		String cuser,

		Instant udate,

		String uuser) {
}
