package com.etiya.crm.customerservice.business.abstracts;

import java.util.List;

import com.etiya.crm.customerservice.business.dtos.requests.AddressEditRequest;
import com.etiya.crm.customerservice.business.dtos.requests.ContactInfo;
import com.etiya.crm.customerservice.business.dtos.requests.CreateBillingAccountRequest;
import com.etiya.crm.customerservice.business.dtos.requests.CustomerSearchRequest;
import com.etiya.crm.customerservice.business.dtos.requests.IndividualInfo;
import com.etiya.crm.customerservice.business.dtos.requests.OnboardCustomerRequest;
import com.etiya.crm.customerservice.business.dtos.requests.UpdateIndividualInfo;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerAccountResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerSearchResponse;
import com.etiya.crm.customerservice.business.dtos.responses.IdentityVerificationResponse;
import com.etiya.crm.shared.contracts.address.AddressResponse;
import com.etiya.crm.shared.contracts.individual.IndividualResponse;

public interface CustomerService {

	/** ACC-009..013: KPS dogrulama + Nationality ID tekillik kontrolu, DB'ye yazmadan. */
	IdentityVerificationResponse verifyIdentity(IndividualInfo individual);

	/** ACC-023: gercek onboarding. party + customer(+account) + contact/address yazar. */
	CustomerResponse onboard(OnboardCustomerRequest request);

	List<CustomerSearchResponse> search(CustomerSearchRequest request);

	CustomerResponse getById(Long custId);

	void softDelete(Long custId);

	// --- Editleme: kisisel bilgi (party-service), adres/contact (contact-info-service) ---

	IndividualResponse getIndividual(Long custId);

	IndividualResponse updateIndividual(Long custId, UpdateIndividualInfo request);

	List<AddressResponse> getAddresses(Long custId);

	AddressResponse addAddress(Long custId, AddressEditRequest request);

	AddressResponse updateAddress(Long custId, Long addressId, AddressEditRequest request);

	ContactInfo getContact(Long custId);

	ContactInfo updateContact(Long custId, ContactInfo request);

	// --- ACC-001..014: Customer Account tab / Create Billing Account ---

	List<CustomerAccountResponse> getAccounts(Long custId);

	CustomerAccountResponse createBillingAccount(Long custId, CreateBillingAccountRequest request);

	/** contact-info-service'in adres silmeden once soracagi varlik kontrolu. */
	boolean existsAccountByAddressId(Long addressId);
}
