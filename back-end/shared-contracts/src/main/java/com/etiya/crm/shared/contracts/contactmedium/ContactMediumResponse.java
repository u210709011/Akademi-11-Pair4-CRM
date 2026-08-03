package com.etiya.crm.shared.contracts.contactmedium;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;

/** contact-info-service GET/POST/PUT /api/v1/contact-mediums yanit govdesi. */
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Contact medium kaydi (e-posta, telefon, vb.).")
public record ContactMediumResponse(

		@Schema(description = "Kaydin kendi id'si (guncelleme/silme icin kullanilir).", example = "17")
		Long id,

		@Schema(description = "Kaydin sahibinin id'si (ornegin custId).", example = "1")
		Long rowId,

		@Schema(description = "lookup-service DATA_TYPE grubundaki deger id'si.", example = "102")
		Long dataTypeId,

		String cntcData,

		@Schema(description = "lookup-service CNTC_MEDIUM_TYPE grubundaki deger id'si (EMAIL=4001, MOBILE_PHONE=4002, HOME_PHONE=4003, FAX=4004).",
				example = "4001")
		Long cntcMediumTypeId,

		Instant cdate,

		String cuser,

		Instant udate,

		String uuser) {
}
