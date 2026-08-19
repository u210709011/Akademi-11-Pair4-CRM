package com.etiya.crm.customerservice.api.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.etiya.crm.customerservice.business.abstracts.BillingAccountService;
import com.etiya.crm.customerservice.business.dtos.requests.CreateBillingAccountRequest;
import com.etiya.crm.customerservice.business.dtos.requests.UpdateBillingAccountRequest;
import com.etiya.crm.customerservice.business.dtos.requests.UpdateBillingAccountStatusRequest;
import com.etiya.crm.customerservice.business.dtos.responses.AddressBillingAccountsResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerAccountResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerAccountControllerTest {

	@Mock
	private BillingAccountService billingAccountService;

	@InjectMocks
	private CustomerAccountController controller;

	private CustomerAccountResponse account() {
		return new CustomerAccountResponse(42L, "000042", "Home", "desc", 501L, 5L, 601L, true);
	}

	@Test
	void getAccounts_returns200WithPage() {
		Page<CustomerAccountResponse> page = new PageImpl<>(List.of(account()));
		when(billingAccountService.getAccounts(any(), any())).thenReturn(page);

		ResponseEntity<Page<CustomerAccountResponse>> response = controller.getAccounts(10L, 0, 5);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isSameAs(page);
	}

	@Test
	void createBillingAccount_returns201Created() {
		CreateBillingAccountRequest request = new CreateBillingAccountRequest("Home", "desc", 5L, null);
		CustomerAccountResponse created = account();
		when(billingAccountService.createBillingAccount(10L, request)).thenReturn(created);

		ResponseEntity<CustomerAccountResponse> response = controller.createBillingAccount(10L, request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isSameAs(created);
	}

	@Test
	void updateBillingAccount_returns200() {
		UpdateBillingAccountRequest request = new UpdateBillingAccountRequest("Home", "desc", 5L, null);
		CustomerAccountResponse updated = account();
		when(billingAccountService.updateBillingAccount(10L, 42L, request)).thenReturn(updated);

		ResponseEntity<CustomerAccountResponse> response = controller.updateBillingAccount(10L, 42L, request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isSameAs(updated);
	}

	@Test
	void updateBillingAccountStatus_returns200() {
		UpdateBillingAccountStatusRequest request = new UpdateBillingAccountStatusRequest(
				UpdateBillingAccountStatusRequest.PASSIVE);
		CustomerAccountResponse updated = account();
		when(billingAccountService.updateBillingAccountStatus(10L, 42L, request)).thenReturn(updated);

		ResponseEntity<CustomerAccountResponse> response = controller.updateBillingAccountStatus(10L, 42L, request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isSameAs(updated);
	}

	@Test
	void deleteBillingAccount_returns204NoContent() {
		ResponseEntity<Void> response = controller.deleteBillingAccount(10L, 42L);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(billingAccountService).deleteBillingAccount(10L, 42L);
	}

	@Test
	void existsAccountByAddress_returns200WithBoolean() {
		when(billingAccountService.existsAccountByAddressId(5L)).thenReturn(true);

		ResponseEntity<Boolean> response = controller.existsAccountByAddress(5L);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isTrue();
	}

	@Test
	void getAccountsByAddress_returns200() {
		AddressBillingAccountsResponse addressAccounts = new AddressBillingAccountsResponse(1, List.of(account()));
		when(billingAccountService.getAccountsByAddressId(5L)).thenReturn(addressAccounts);

		ResponseEntity<AddressBillingAccountsResponse> response = controller.getAccountsByAddress(5L);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isSameAs(addressAccounts);
	}
}
