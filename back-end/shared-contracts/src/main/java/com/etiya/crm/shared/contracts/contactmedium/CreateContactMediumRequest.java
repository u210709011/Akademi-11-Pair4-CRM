package com.etiya.crm.shared.contracts.contactmedium;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** contact-info-service POST /api/v1/contact-mediums/single istek govdesi. */
@Schema(description = "POST /api/v1/contact-mediums/single istek govdesi.")
public record CreateContactMediumRequest(

		@Schema(description = "Contact medium'un sahibinin id'si (ornegin custId).", example = "1")
		@NotNull
		Long rowId,

		@Schema(description = "lookup-service DATA_TYPE grubundaki deger id'si (musteri icin 102).", example = "102")
		@NotNull
		Long dataTypeId,

		@Schema(description = "Iletisim verisi (e-posta, telefon no, vb.)", example = "ahmet.yilmaz@example.com")
		@NotBlank
		@Size(max = 100)
		String cntcData,

		@Schema(description = "lookup-service CNTC_MEDIUM_TYPE grubundaki deger id'si (EMAIL=4001, MOBILE_PHONE=4002, HOME_PHONE=4003, FAX=4004).",
				example = "4001")
		@NotNull
		Long cntcMediumTypeId) {
}
