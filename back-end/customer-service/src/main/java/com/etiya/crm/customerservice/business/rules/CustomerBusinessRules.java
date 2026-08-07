package com.etiya.crm.customerservice.business.rules;

import org.springframework.stereotype.Component;

import com.etiya.crm.customerservice.business.dtos.requests.CustomerSearchRequest;
import com.etiya.crm.customerservice.business.exceptions.SearchFilterRequiredException;

/** Customer aggregate'ine ozel (search/onboarding disi) genel kurallar. */
@Component
public class CustomerBusinessRules {

	/**
	 * FR-002 ACC-003: hicbir filtre verilmeden /customers/search cagrilirsa tum (aktif)
	 * musteriler donuyordu - bu artik 400 sayilir, caller en az bir filtre girmeli.
	 */
	public void ensureAtLeastOneFilterProvided(CustomerSearchRequest request) {
		boolean hasFilter = hasText(request.firstName()) || hasText(request.lastName()) || hasText(request.tcNo())
				|| hasText(request.acctNo()) || request.custId() != null || hasText(request.gsm());
		if (!hasFilter) {
			throw new SearchFilterRequiredException();
		}
	}

	private boolean hasText(String value) {
		return value != null && !value.isBlank();
	}
}
