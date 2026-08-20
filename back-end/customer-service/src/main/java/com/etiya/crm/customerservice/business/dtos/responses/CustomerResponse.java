package com.etiya.crm.customerservice.business.dtos.responses;

import java.util.List;

/** Müşteri ve hesap bilgilerinin dış servislere dönen görünümüdür. */
public record CustomerResponse(
		Long custId,

		/** Servis içinde oluşturulan cust id'nin denormalize 6 digit hali. */
		String custNo,
		
		Long partyRoleId,
		Long custTpId,
		boolean active,
		List<CustomerAccountResponse> accounts) {
}
