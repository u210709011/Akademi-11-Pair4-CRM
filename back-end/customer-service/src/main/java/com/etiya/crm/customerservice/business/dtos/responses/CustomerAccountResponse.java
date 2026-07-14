package com.etiya.crm.customerservice.business.dtos.responses;

public record CustomerAccountResponse(
		Long custAcctId,
		String accountNo,
		String accountName,
		String accountDesc,
		Long accountTpId,
		Long addressId,
		boolean active) {
}
