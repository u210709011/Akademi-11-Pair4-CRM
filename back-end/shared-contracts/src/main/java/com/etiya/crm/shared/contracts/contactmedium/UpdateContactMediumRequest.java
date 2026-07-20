package com.etiya.crm.shared.contracts.contactmedium;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** contact-info-service PUT /api/v1/contact-mediums/{id} istek govdesi. */
@Schema(description = "PUT /api/v1/contact-mediums/{id} istek govdesi.")
public record UpdateContactMediumRequest(

		@Schema(description = "Iletisim verisi (e-posta, telefon no, vb.)", example = "ahmet.yilmazoglu@example.com")
		@NotBlank
		@Size(max = 100)
		String cntcData,

		@Schema(description = "lookup-service CNTC_MEDIUM_TYPE grubundaki deger id'si (EMAIL=4001, MOBILE_PHONE=4002, HOME_PHONE=4003, FAX=4004).",
				example = "4001")
		@NotNull
		Long cntcMediumTypeId) {
}
