package com.etiya.crm.customerservice.business.abstracts;

import java.util.List;

import com.etiya.crm.customerservice.business.dtos.requests.CustomerSearchRequest;
import com.etiya.crm.customerservice.business.dtos.requests.IndividualInfo;
import com.etiya.crm.customerservice.business.dtos.requests.OnboardCustomerRequest;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerSearchResponse;
import com.etiya.crm.customerservice.business.dtos.responses.IdentityVerificationResponse;

public interface CustomerService {

	/** ACC-009..013: KPS dogrulama + Nationality ID tekillik kontrolu, DB'ye yazmadan. */
	IdentityVerificationResponse verifyIdentity(IndividualInfo individual);

	/** ACC-023: gercek onboarding. party + customer(+account) + contact/address yazar. */
	CustomerResponse onboard(OnboardCustomerRequest request);

	List<CustomerSearchResponse> search(CustomerSearchRequest request);

	CustomerResponse getById(Long custId);

	void softDelete(Long custId);
}
