package com.etiya.crm.customerservice.api.controllers;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.etiya.crm.customerservice.business.abstracts.CustomerAddressService;
import com.etiya.crm.customerservice.business.dtos.requests.AddressEditRequest;
import com.etiya.crm.shared.contracts.address.AddressResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerAddressControllerTest {

	@Mock
	private CustomerAddressService addressService;

	@InjectMocks
	private CustomerAddressController controller;

	@Test
	void getAddresses_returns200WithList() {
		List<AddressResponse> addresses = List.of(address(20L));
		when(addressService.getAddresses(10L)).thenReturn(addresses);

		ResponseEntity<List<AddressResponse>> response = controller.getAddresses(10L);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isSameAs(addresses);
	}

	@Test
	void addAddress_returns201Created() {
		AddressEditRequest request = new AddressEditRequest(5L, "Cad", "No 1", "Ev", false);
		AddressResponse created = address(21L);
		when(addressService.addAddress(10L, request)).thenReturn(created);

		ResponseEntity<AddressResponse> response = controller.addAddress(10L, request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isSameAs(created);
	}

	@Test
	void updateAddress_returns200() {
		AddressEditRequest request = new AddressEditRequest(5L, "Cad", "No 1", "Ev", true);
		AddressResponse updated = address(20L);
		when(addressService.updateAddress(10L, 20L, request)).thenReturn(updated);

		ResponseEntity<AddressResponse> response = controller.updateAddress(10L, 20L, request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isSameAs(updated);
	}

	@Test
	void deleteAddress_returns204NoContent() {
		ResponseEntity<Void> response = controller.deleteAddress(10L, 20L);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(addressService).deleteAddress(10L, 20L);
	}

	private AddressResponse address(Long id) {
		return new AddressResponse(id, 10L, 1L, 5L, "Cad", "No", "Ev", false, Instant.now(), "sys", Instant.now(),
				"sys");
	}
}
