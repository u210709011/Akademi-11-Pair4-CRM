package com.etiya.crm.customerservice.api.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.etiya.crm.customerservice.business.abstracts.BillingAccountService;
import com.etiya.crm.customerservice.business.dtos.requests.CreateBillingAccountRequest;
import com.etiya.crm.customerservice.business.dtos.requests.UpdateBillingAccountRequest;
import com.etiya.crm.customerservice.business.dtos.requests.UpdateBillingAccountStatusRequest;
import com.etiya.crm.customerservice.business.dtos.responses.AddressBillingAccountsResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerAccountResponse;
import com.etiya.crm.customerservice.constants.Roles;
import com.etiya.crm.customerservice.constants.SwaggerText;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Musterinin fatura hesaplari (Customer Account tab / Create Billing Account) uc noktalari. */
@Tag(name = SwaggerText.CUSTOMER_TAG_NAME, description = SwaggerText.CUSTOMER_ACCOUNT_TAG_DESCRIPTION)
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('" + Roles.CRM_AGENT + "')")
public class CustomerAccountController {

	private final BillingAccountService billingAccountService;

	@Operation(summary = SwaggerText.GET_ACCOUNTS_SUMMARY, description = SwaggerText.GET_ACCOUNTS_DESCRIPTION)
	@GetMapping("/{custId}/accounts")
	public ResponseEntity<Page<CustomerAccountResponse>> getAccounts(@PathVariable Long custId,
			@Parameter(description = SwaggerText.PAGE_PARAM_DESCRIPTION, example = "0")
			@RequestParam(defaultValue = "0") int page,
			@Parameter(description = SwaggerText.SIZE_PARAM_DESCRIPTION, example = "5")
			@RequestParam(defaultValue = "5") int size) {
		return ResponseEntity.ok(billingAccountService.getAccounts(custId, PageRequest.of(page, size)));
	}

	@Operation(summary = SwaggerText.CREATE_BILLING_ACCOUNT_SUMMARY,
			description = SwaggerText.CREATE_BILLING_ACCOUNT_DESCRIPTION)
	@PostMapping("/{custId}/accounts")
	public ResponseEntity<CustomerAccountResponse> createBillingAccount(@PathVariable Long custId,
			@Valid @RequestBody CreateBillingAccountRequest request) {
		CustomerAccountResponse response = billingAccountService.createBillingAccount(custId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = SwaggerText.UPDATE_BILLING_ACCOUNT_SUMMARY,
			description = SwaggerText.UPDATE_BILLING_ACCOUNT_DESCRIPTION)
	@PutMapping("/{custId}/accounts/{accountId}")
	public ResponseEntity<CustomerAccountResponse> updateBillingAccount(@PathVariable Long custId,
			@Parameter(description = SwaggerText.UPDATE_BILLING_ACCOUNT_ACCOUNT_ID_PARAM_DESCRIPTION) @PathVariable Long accountId,
			@Valid @RequestBody UpdateBillingAccountRequest request) {
		return ResponseEntity.ok(billingAccountService.updateBillingAccount(custId, accountId, request));
	}

	@Operation(summary = SwaggerText.UPDATE_BILLING_ACCOUNT_STATUS_SUMMARY,
			description = SwaggerText.UPDATE_BILLING_ACCOUNT_STATUS_DESCRIPTION)
	@PatchMapping("/{custId}/accounts/{accountId}/status")
	public ResponseEntity<CustomerAccountResponse> updateBillingAccountStatus(@PathVariable Long custId,
			@Parameter(description = SwaggerText.UPDATE_BILLING_ACCOUNT_STATUS_ACCOUNT_ID_PARAM_DESCRIPTION) @PathVariable Long accountId,
			@Valid @RequestBody UpdateBillingAccountStatusRequest request) {
		return ResponseEntity.ok(billingAccountService.updateBillingAccountStatus(custId, accountId, request));
	}

	@Operation(summary = SwaggerText.DELETE_BILLING_ACCOUNT_SUMMARY,
			description = SwaggerText.DELETE_BILLING_ACCOUNT_DESCRIPTION)
	@DeleteMapping("/{custId}/accounts/{accountId}")
	public ResponseEntity<Void> deleteBillingAccount(@PathVariable Long custId,
			@Parameter(description = SwaggerText.DELETE_BILLING_ACCOUNT_ACCOUNT_ID_PARAM_DESCRIPTION) @PathVariable Long accountId) {
		billingAccountService.deleteBillingAccount(custId, accountId);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = SwaggerText.EXISTS_ACCOUNT_BY_ADDRESS_SUMMARY,
			description = SwaggerText.EXISTS_ACCOUNT_BY_ADDRESS_DESCRIPTION)
	@GetMapping("/accounts/exists-by-address/{addressId}")
	public ResponseEntity<Boolean> existsAccountByAddress(@PathVariable Long addressId) {
		return ResponseEntity.ok(billingAccountService.existsAccountByAddressId(addressId));
	}

	@Operation(summary = SwaggerText.GET_ACCOUNTS_BY_ADDRESS_SUMMARY,
			description = SwaggerText.GET_ACCOUNTS_BY_ADDRESS_DESCRIPTION)
	@GetMapping("/accounts/by-address/{addressId}")
	public ResponseEntity<AddressBillingAccountsResponse> getAccountsByAddress(@PathVariable Long addressId) {
		return ResponseEntity.ok(billingAccountService.getAccountsByAddressId(addressId));
	}
}
