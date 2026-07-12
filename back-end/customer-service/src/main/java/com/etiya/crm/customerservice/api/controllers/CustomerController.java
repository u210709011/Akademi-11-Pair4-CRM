package com.etiya.crm.customerservice.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.etiya.crm.customerservice.business.abstracts.CustomerService;
import com.etiya.crm.customerservice.business.dtos.requests.AddressEditRequest;
import com.etiya.crm.customerservice.business.dtos.requests.ContactInfo;
import com.etiya.crm.customerservice.business.dtos.requests.CustomerSearchRequest;
import com.etiya.crm.customerservice.business.dtos.requests.IndividualInfo;
import com.etiya.crm.customerservice.business.dtos.requests.OnboardCustomerRequest;
import com.etiya.crm.customerservice.business.dtos.requests.UpdateIndividualInfo;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerSearchResponse;
import com.etiya.crm.customerservice.business.dtos.responses.IdentityVerificationResponse;
import com.etiya.crm.customerservice.clients.responses.AddressResponse;
import com.etiya.crm.customerservice.clients.responses.IndividualResponse;
import com.etiya.crm.customerservice.constants.Roles;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('" + Roles.CRM_AGENT + "')")
public class CustomerController {

	private final CustomerService customerService;

	// ACC-009..013: adres/kontakt adimlarindan once kimlik dogrulama + tekillik kontrolu.
	@PostMapping("/onboarding/verify-identity")
	public ResponseEntity<IdentityVerificationResponse> verifyIdentity(@Valid @RequestBody IndividualInfo individual) {
		return ResponseEntity.ok(customerService.verifyIdentity(individual));
	}

	// ACC-023: Create butonu, tum onboarding'i (party + customer + contact/address) yapar.
	@PostMapping("/onboarding")
	public ResponseEntity<CustomerResponse> onboard(@Valid @RequestBody OnboardCustomerRequest request) {
		CustomerResponse response = customerService.onboard(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{custId}")
	public ResponseEntity<CustomerResponse> getById(@PathVariable Long custId) {
		return ResponseEntity.ok(customerService.getById(custId));
	}

	@GetMapping("/search")
	public ResponseEntity<List<CustomerSearchResponse>> search(
			@RequestParam(required = false) String firstName,
			@RequestParam(required = false) String lastName,
			@RequestParam(required = false) String tcNo,
			@RequestParam(required = false) String acctNo) {
		CustomerSearchRequest request = new CustomerSearchRequest(firstName, lastName, tcNo, acctNo);
		return ResponseEntity.ok(customerService.search(request));
	}

	@DeleteMapping("/{custId}")
	public ResponseEntity<Void> softDelete(@PathVariable Long custId) {
		customerService.softDelete(custId);
		return ResponseEntity.noContent().build();
	}

	// --- Editleme: kisisel bilgi party-service'e, adres/contact contact-info-service'e proxylenir ---

	@GetMapping("/{custId}/individual")
	public ResponseEntity<IndividualResponse> getIndividual(@PathVariable Long custId) {
		return ResponseEntity.ok(customerService.getIndividual(custId));
	}

	@PutMapping("/{custId}/individual")
	public ResponseEntity<IndividualResponse> updateIndividual(@PathVariable Long custId,
			@Valid @RequestBody UpdateIndividualInfo request) {
		return ResponseEntity.ok(customerService.updateIndividual(custId, request));
	}

	@GetMapping("/{custId}/addresses")
	public ResponseEntity<List<AddressResponse>> getAddresses(@PathVariable Long custId) {
		return ResponseEntity.ok(customerService.getAddresses(custId));
	}

	@PostMapping("/{custId}/addresses")
	public ResponseEntity<AddressResponse> addAddress(@PathVariable Long custId,
			@Valid @RequestBody AddressEditRequest request) {
		AddressResponse response = customerService.addAddress(custId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{custId}/addresses/{addressId}")
	public ResponseEntity<AddressResponse> updateAddress(@PathVariable Long custId, @PathVariable Long addressId,
			@Valid @RequestBody AddressEditRequest request) {
		return ResponseEntity.ok(customerService.updateAddress(custId, addressId, request));
	}

	@GetMapping("/{custId}/contact")
	public ResponseEntity<ContactInfo> getContact(@PathVariable Long custId) {
		return ResponseEntity.ok(customerService.getContact(custId));
	}

	@PutMapping("/{custId}/contact")
	public ResponseEntity<ContactInfo> updateContact(@PathVariable Long custId,
			@Valid @RequestBody ContactInfo request) {
		return ResponseEntity.ok(customerService.updateContact(custId, request));
	}
}
