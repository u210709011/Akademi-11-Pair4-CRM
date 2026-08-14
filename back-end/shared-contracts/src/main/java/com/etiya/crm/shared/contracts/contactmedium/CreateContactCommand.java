package com.etiya.crm.shared.contracts.contactmedium;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

import com.etiya.crm.shared.contracts.constants.SwaggerText;

/**
 * POST /api/v1/contact-mediums (composite) istek govdesi - customer-service
 * onboarding sirasinda cagirir. ROW_ID = custId; DATA_TP_ID caller'dan gelir
 * (customer-service kendi LookupCacheService'i ile lookup-service'in TYPE_VALUE
 * tablosundan dinamik cozer, bkz. CreateAddressRequest/CreateContactMediumRequest
 * ile ayni desen) - burada hardcode edilmez.
 */
@Schema(description = SwaggerText.CREATE_CONTACT_COMMAND_DESCRIPTION)
public record CreateContactCommand(

		@Schema(description = SwaggerText.CREATE_CONTACT_COMMAND_CUST_ID_DESCRIPTION, example = "1")
		Long custId,

		@Schema(description = SwaggerText.CREATE_CONTACT_COMMAND_DATA_TYPE_ID_DESCRIPTION, example = "12")
		Long dataTypeId,

		@Schema(description = SwaggerText.CREATE_CONTACT_COMMAND_ADDRESSES_DESCRIPTION)
		List<AddressCommand> addresses,

		@Schema(description = SwaggerText.CREATE_CONTACT_COMMAND_CONTACT_MEDIUMS_DESCRIPTION)
		List<ContactMediumCommand> contactMediums) {
}
