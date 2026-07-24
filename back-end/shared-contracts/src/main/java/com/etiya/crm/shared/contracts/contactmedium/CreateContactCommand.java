package com.etiya.crm.shared.contracts.contactmedium;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * POST /api/v1/contact-mediums (composite) istek govdesi - customer-service
 * onboarding sirasinda cagirir. ROW_ID = custId, DATA_TP_ID = CUST(102)
 * sabittir ve contact-info-service tarafindan set edilir; caller sadece
 * custId'yi tasir.
 */
@Schema(description = "Onboarding composite create: bir musterinin tum adreslerini ve contact "
		+ "medium'larini tek transaction'da yazar. Tekil ekleme icin bkz. CreateAddressRequest / CreateContactMediumRequest.")
public record CreateContactCommand(

		@Schema(description = "Adreslerin/contact medium'larin ait olacagi customer id.", example = "1")
		Long custId,

		@Schema(description = "1-5 adet adres; ilk eleman genelde primary olarak isaretlenir.")
		List<AddressCommand> addresses,

		@Schema(description = "Contact medium listesi (email, mobilePhone zorunlu; homePhone, fax opsiyonel).")
		List<ContactMediumCommand> contactMediums) {
}
