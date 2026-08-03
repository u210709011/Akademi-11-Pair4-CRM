package com.etiya.crm.customerservice.business.abstracts;

import com.etiya.crm.customerservice.business.dtos.requests.IndividualInfo;
import com.etiya.crm.customerservice.business.dtos.requests.OnboardCustomerRequest;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerResponse;
import com.etiya.crm.customerservice.business.dtos.responses.IdentityVerificationResponse;

/**
 * Yeni musteri onboarding saga'si: party-service (kisi+rol) + customer-service
 * (musteri+varsayilan hesap+arama view'i) + contact-info-service (adres+iletisim)
 * uc servise tek istekte yazar; bir adim basarisiz olursa oncekiler telafi
 * (compensation) ile geri alinir.
 */
public interface CustomerOnboardingService {

	/** ACC-009..013: KPS dogrulama + Nationality ID tekillik kontrolu, DB'ye yazmadan. */
	IdentityVerificationResponse verifyIdentity(IndividualInfo individual);

	/** ACC-023: gercek onboarding. party + customer(+account) + contact/address yazar. */
	CustomerResponse onboard(OnboardCustomerRequest request);
}
