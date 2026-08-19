package com.etiya.crm.customerservice.api.controllers;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.etiya.crm.customerservice.business.abstracts.CustomerOnboardingService;
import com.etiya.crm.customerservice.business.abstracts.CustomerService;
import com.etiya.crm.customerservice.business.dtos.requests.AddressInfo;
import com.etiya.crm.customerservice.business.dtos.requests.ContactInfo;
import com.etiya.crm.customerservice.business.dtos.requests.CustomerSearchRequest;
import com.etiya.crm.customerservice.business.dtos.requests.IndividualInfo;
import com.etiya.crm.customerservice.business.dtos.requests.OnboardCustomerRequest;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerSearchResponse;
import com.etiya.crm.customerservice.business.dtos.responses.IdentityVerificationResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

	@Mock
	private CustomerService customerService;

	@Mock
	private CustomerOnboardingService onboardingService;

	@InjectMocks
	private CustomerController controller;

	private final IndividualInfo individual = new IndividualInfo("Ahmet", null, "Yilmaz",
			LocalDate.of(1990, 6, 15), 1L, null, null, "10000000146");

	@Test
	void verifyIdentity_returns200() {
		IdentityVerificationResponse verified = IdentityVerificationResponse.ok();
		when(onboardingService.verifyIdentity(individual)).thenReturn(verified);

		ResponseEntity<IdentityVerificationResponse> response = controller.verifyIdentity(individual);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isSameAs(verified);
	}

	@Test
	void onboard_returns201Created() {
		OnboardCustomerRequest request = new OnboardCustomerRequest(individual,
				List.of(new AddressInfo(5L, "Cad", "No 1", "Ev")),
				new ContactInfo("a@b.com", "5551234567", null, null));
		CustomerResponse created = new CustomerResponse(10L, "000010", 100L, 1L, true, List.of());
		when(onboardingService.onboard(request)).thenReturn(created);

		ResponseEntity<CustomerResponse> response = controller.onboard(request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isSameAs(created);
	}

	@Test
	void getById_returns200() {
		CustomerResponse customer = new CustomerResponse(10L, "000010", 100L, 1L, true, List.of());
		when(customerService.getById(10L)).thenReturn(customer);

		ResponseEntity<CustomerResponse> response = controller.getById(10L);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isSameAs(customer);
	}

	@Test
	void search_usesUnsortedPage_whenSortByIsNull() {
		when(customerService.search(any(), any())).thenReturn(new PageImpl<>(List.of()));

		controller.search("Ahmet", null, null, null, null, null, 0, 10, null, "asc");

		Pageable pageable = capturePageable();
		assertThat(pageable.getSort().isUnsorted()).isTrue();
	}

	@Test
	void search_usesUnsortedPage_whenSortByNotInWhitelist() {
		when(customerService.search(any(), any())).thenReturn(new PageImpl<>(List.of()));

		controller.search("Ahmet", null, null, null, null, null, 0, 10, "notAWhitelistedField", "asc");

		Pageable pageable = capturePageable();
		assertThat(pageable.getSort().isUnsorted()).isTrue();
	}

	@Test
	void search_sortsAscending_whenSortByWhitelistedAndDirAsc() {
		when(customerService.search(any(), any())).thenReturn(new PageImpl<>(List.of()));

		controller.search("Ahmet", null, null, null, null, null, 0, 10, "lastName", "asc");

		Pageable pageable = capturePageable();
		Sort.Order order = pageable.getSort().getOrderFor("lastName");
		assertThat(order).isNotNull();
		assertThat(order.getDirection()).isEqualTo(Sort.Direction.ASC);
	}

	@Test
	void search_sortsDescending_whenDirIsDesc() {
		when(customerService.search(any(), any())).thenReturn(new PageImpl<>(List.of()));

		controller.search("Ahmet", null, null, null, null, null, 0, 10, "lastName", "desc");

		Pageable pageable = capturePageable();
		Sort.Order order = pageable.getSort().getOrderFor("lastName");
		assertThat(order).isNotNull();
		assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);
	}

	@Test
	void search_buildsSearchRequest_fromQueryParams() {
		when(customerService.search(any(), any())).thenReturn(new PageImpl<>(List.of()));

		controller.search("Ahmet", "Yilmaz", "10000000146", "000042", 10L, "5551234567", 0, 10, null, "asc");

		ArgumentCaptor<CustomerSearchRequest> captor = ArgumentCaptor.forClass(CustomerSearchRequest.class);
		verify(customerService).search(captor.capture(), any());
		assertThat(captor.getValue().firstName()).isEqualTo("Ahmet");
		assertThat(captor.getValue().custId()).isEqualTo(10L);
	}

	@Test
	void softDelete_returns204NoContent() {
		ResponseEntity<Void> response = controller.softDelete(10L);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(customerService).softDelete(10L);
	}

	@SuppressWarnings("unchecked")
	private Pageable capturePageable() {
		ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
		verify(customerService).search(any(), captor.capture());
		return captor.getValue();
	}
}
