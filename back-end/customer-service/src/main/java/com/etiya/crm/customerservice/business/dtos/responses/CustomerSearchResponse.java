package com.etiya.crm.customerservice.business.dtos.responses;

public record CustomerSearchResponse(
		Long custId,
		String firstName,
		String middleName,
		String lastName,
		String tcNo,
		String acctNo,
		String role,
		String gsm,
		String status) {
}
