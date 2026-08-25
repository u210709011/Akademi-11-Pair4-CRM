package com.etiya.crm.customerservice.api.controllers;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.etiya.crm.customerservice.business.abstracts.CustomerOnboardingService;
import com.etiya.crm.customerservice.business.abstracts.CustomerService;
import com.etiya.crm.customerservice.business.dtos.requests.CustomerSearchRequest;
import com.etiya.crm.customerservice.business.dtos.requests.IndividualInfo;
import com.etiya.crm.customerservice.business.dtos.requests.OnboardCustomerRequest;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerSearchResponse;
import com.etiya.crm.customerservice.business.dtos.responses.IdentityVerificationResponse;
import com.etiya.crm.customerservice.constants.MessageKeys;
import com.etiya.crm.customerservice.constants.Roles;
import com.etiya.crm.customerservice.constants.SwaggerText;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@Tag(name = SwaggerText.CUSTOMER_TAG_NAME, description = SwaggerText.CUSTOMER_TAG_DESCRIPTION)
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('" + Roles.CRM_AGENT + "')")
public class CustomerController {

	private final CustomerService customerService;
	private final CustomerOnboardingService onboardingService;

	// ACC-009..013: adres/kontakt adimlarindan once kimlik dogrulama + tekillik kontrolu.
	@Operation(summary = SwaggerText.VERIFY_IDENTITY_SUMMARY, description = SwaggerText.VERIFY_IDENTITY_DESCRIPTION)
	@PostMapping("/onboarding/verify-identity")
	public ResponseEntity<IdentityVerificationResponse> verifyIdentity(@Valid @RequestBody IndividualInfo individual) {
		return ResponseEntity.ok(onboardingService.verifyIdentity(individual));
	}

	// ACC-023: Create butonu, tum onboarding'i (party + customer + contact/address) yapar.
	@Operation(summary = SwaggerText.ONBOARD_CUSTOMER_SUMMARY, description = SwaggerText.ONBOARD_CUSTOMER_DESCRIPTION)
	@PostMapping("/onboarding")
	public ResponseEntity<CustomerResponse> onboard(@Valid @RequestBody OnboardCustomerRequest request) {
		CustomerResponse response = onboardingService.onboard(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = SwaggerText.GET_CUSTOMER_BY_ID_SUMMARY)
	@GetMapping("/{custId}")
	public ResponseEntity<CustomerResponse> getById(@PathVariable Long custId) {
		return ResponseEntity.ok(customerService.getById(custId));
	}

	@Operation(summary = SwaggerText.SEARCH_CUSTOMER_SUMMARY, description = SwaggerText.SEARCH_CUSTOMER_DESCRIPTION)
	@GetMapping("/search")
	public ResponseEntity<Page<CustomerSearchResponse>> search(
			@Parameter(description = SwaggerText.SEARCH_FIRST_NAME_PARAM_DESCRIPTION, example = "Ahmet")
			@Size(max = 50, message = "{" + MessageKeys.SEARCH_FIRST_NAME_INVALID + "}")
			@RequestParam(required = false) String firstName,
			@Parameter(description = SwaggerText.SEARCH_LAST_NAME_PARAM_DESCRIPTION, example = "Yilmaz")
			@Size(max = 50, message = "{" + MessageKeys.SEARCH_LAST_NAME_INVALID + "}")
			@RequestParam(required = false) String lastName,
			@Parameter(description = SwaggerText.SEARCH_TC_NO_PARAM_DESCRIPTION, example = "10000000146")
			@Pattern(regexp = "^[0-9]{11}$", message = "{" + MessageKeys.SEARCH_NATIONAL_ID_INVALID + "}")
			@RequestParam(required = false) String tcNo,
			@Parameter(description = SwaggerText.SEARCH_ACCT_NO_PARAM_DESCRIPTION, example = "1")
			@Pattern(regexp = "^[0-9]+$", message = "{" + MessageKeys.SEARCH_ACCOUNT_NUMBER_INVALID + "}")
			@RequestParam(required = false) String acctNo,
			@Parameter(description = SwaggerText.SEARCH_CUST_ID_PARAM_DESCRIPTION, example = "1")
			@RequestParam(required = false) Long custId,
			@Parameter(description = SwaggerText.SEARCH_GSM_PARAM_DESCRIPTION, example = "5551234567")
			@Pattern(regexp = "^5[0-9]{9}$", message = "{" + MessageKeys.SEARCH_GSM_INVALID + "}")
			@RequestParam(required = false) String gsm,
			@Parameter(description = SwaggerText.PAGE_PARAM_DESCRIPTION, example = "0")
			@RequestParam(defaultValue = "0") int page,
			@Parameter(description = SwaggerText.SIZE_PARAM_DESCRIPTION, example = "10")
			@RequestParam(defaultValue = "10") int size,
			@Parameter(description = SwaggerText.SEARCH_SORT_BY_PARAM_DESCRIPTION, example = "lastName")
			@RequestParam(required = false) String sortBy,
			@Parameter(description = SwaggerText.SEARCH_SORT_DIR_PARAM_DESCRIPTION, example = "asc")
			@RequestParam(defaultValue = "asc") String sortDir) {
		CustomerSearchRequest request = new CustomerSearchRequest(firstName, lastName, tcNo, acctNo, custId, gsm);
		return ResponseEntity.ok(customerService.search(request, buildPageable(page, size, sortBy, sortDir)));
	}

	/**
	 * sortBy, CustomerSearchView'in gercek alan adlariyla birebir sinirlanir (whitelist) -
	 * hem gelisigüzel property-path'lerin JPA'ya sizmasini engeller hem de front-end'in
	 * tikladigi kolonlarla (search-customer.component.ts SortColumn) ayni kumeyi tutar.
	 * Tam sonuc kumesini (sayfa degil) siralar cunku sort, veritabani sorgusunun bir
	 * parcasidir - front-end artik sadece o an ekrandaki sayfayi kendi tarafinda siralamaz.
	 */
	private static final Set<String> SORTABLE_FIELDS =
			Set.of("custId", "firstName", "middleName", "lastName", "tcNo", "role");

	private Pageable buildPageable(int page, int size, String sortBy, String sortDir) {
		if (sortBy == null || !SORTABLE_FIELDS.contains(sortBy)) {
			return PageRequest.of(page, size);
		}
		Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
		return PageRequest.of(page, size, Sort.by(direction, sortBy));
	}

	@Operation(summary = SwaggerText.SOFT_DELETE_CUSTOMER_SUMMARY,
			description = SwaggerText.SOFT_DELETE_CUSTOMER_DESCRIPTION)
	@DeleteMapping("/{custId}")
	public ResponseEntity<Void> softDelete(@PathVariable Long custId) {
		customerService.softDelete(custId);
		return ResponseEntity.noContent().build();
	}
}
