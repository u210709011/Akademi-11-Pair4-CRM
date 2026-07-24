package com.etiya.crm.customerservice.business.dtos.responses;

public record CustomerAccountResponse(
		Long custAcctId,
		String accountNo,
		String accountName,
		String accountDesc,
		Long accountTpId,
		Long addressId,
		/** lookup-service GNL_ST/ACCOUNT_STATUS grubu id'si (FR-009 ACC-002 "Account Status" kolonu). null = ACTIVE. */
		Long acctStId,
		boolean active) {
}
