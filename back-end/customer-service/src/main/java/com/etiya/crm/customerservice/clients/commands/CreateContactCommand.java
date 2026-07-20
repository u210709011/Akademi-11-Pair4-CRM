package com.etiya.crm.customerservice.clients.commands;

import java.util.List;

/**
 * contact-address-service POST /api/contacts istek govdesi. ROW_ID = custId,
 * DATA_TP_ID = CUST sabittir ve contact-address-service tarafinda set edilir;
 * bu servis sadece custId'yi tasir.
 */
public record CreateContactCommand(
		Long custId,
		List<AddressCommand> addresses,
		List<ContactMediumCommand> contactMediums) {
}
