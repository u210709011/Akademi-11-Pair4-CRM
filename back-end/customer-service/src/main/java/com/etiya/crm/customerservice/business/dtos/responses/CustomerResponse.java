package com.etiya.crm.customerservice.business.dtos.responses;

import java.util.List;

public record CustomerResponse(
		Long custId,
		/** custId'nin AccountDefaults.formatAccountNo ile sifirla soldan doldurulmus hali (front-end
		 * "CUST-" onekini buna ekler) - accountNo ile ayni kural, front-end'de tekrar hesaplanmasin diye. */
		String custNo,
		Long partyRoleId,
		Long custTpId,
		boolean active,
		List<CustomerAccountResponse> accounts) {
}
