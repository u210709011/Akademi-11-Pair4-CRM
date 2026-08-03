package com.etiya.crm.customerservice.business.abstracts;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.etiya.crm.customerservice.business.dtos.requests.CustomerSearchRequest;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerSearchResponse;

/**
 * Customer aggregate'inin oz yasam donguсu: arama, okuma, soft-delete.
 * Onboarding {@link CustomerOnboardingService}'e, kisisel bilgi/adres/contact/
 * billing-account yonetimi kendi arayuzlerine (bkz. CustomerIndividualService,
 * CustomerAddressService, CustomerContactService, BillingAccountService)
 * ayrilmistir - ilgili controller'lar bu servislere dogrudan bagli calisir.
 */
public interface CustomerService {

	/** ACC-007: ilk 50 kayit + sayfalama (page/size caller tarafindan verilir). */
	Page<CustomerSearchResponse> search(CustomerSearchRequest request, Pageable pageable);

	CustomerResponse getById(Long custId);

	void softDelete(Long custId);
}
