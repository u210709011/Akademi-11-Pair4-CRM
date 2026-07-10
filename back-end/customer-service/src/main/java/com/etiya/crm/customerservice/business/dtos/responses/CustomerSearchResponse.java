package com.etiya.crm.customerservice.business.dtos.responses;

public record CustomerSearchResponse(
		Long custId,
		String firstName,
		String lastName,
		String tcNo,
		String acctNo,
		String status) {
}
