package com.etiya.crm.customerservice.business.dtos.responses;

public record CustomerAccountResponse(
		Long custAcctId,
		String accountNo,
		String accountName,
		Long accountTpId,
		boolean active) {
}
