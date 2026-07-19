package com.etiya.crm.shared.contracts.contactmedium;

import io.swagger.v3.oas.annotations.media.Schema;

/** CreateContactCommand icindeki tek bir CNTC_MEDIUM satiri. */
@Schema(description = "Composite create icindeki tek contact medium.")
public record ContactMediumCommand(

		@Schema(description = "lookup-service CNTC_MEDIUM_TYPE grubundaki deger id'si (EMAIL=4001, MOBILE_PHONE=4002, HOME_PHONE=4003, FAX=4004).",
				example = "4001")
		Long contactMediumTpId,

		@Schema(description = "Iletisim verisi (e-posta, telefon no, vb.)", example = "ahmet.yilmaz@example.com")
		String contactData) {
}
