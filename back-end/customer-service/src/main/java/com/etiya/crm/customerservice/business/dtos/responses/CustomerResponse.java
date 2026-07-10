package com.etiya.crm.customerservice.business.dtos.responses;

import java.util.List;

public record CustomerResponse(
		Long custId,
		Long partyRoleId,
		Long custTpId,
		Long stId,
		boolean active,
		List<CustomerAccountResponse> accounts) {
}
