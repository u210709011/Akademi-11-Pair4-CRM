package com.etiya.crm.contactinfoservice.api.controllers;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.etiya.crm.contactinfoservice.business.abstracts.AddressService;
import com.etiya.crm.shared.contracts.address.AddressResponse;
import com.etiya.crm.shared.contracts.address.CreateAddressRequest;
import com.etiya.crm.shared.contracts.address.UpdateAddressRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest {

	@Mock
	private AddressService addressService;

	@InjectMocks
	private AddressController addressController;

	private static AddressResponse response() {
		return new AddressResponse(1L, 42L, 12L, 5L, "Street", "12", "Desc", true, null, null, null, null);
	}

	@Test
	void getAll_returnsByRowIdAndDataTypeId_whenBothProvided() {
		when(addressService.getByRowIdAndDataTypeId(42L, 12L)).thenReturn(List.of(response()));

		ResponseEntity<List<AddressResponse>> result = addressController.getAll(42L, 12L);

		assertThat(result.getBody()).containsExactly(response());
		verify(addressService, times(0)).getAll();
	}

	@Test
	void getAll_returnsAll_whenRowIdOrDataTypeIdMissing() {
		when(addressService.getAll()).thenReturn(List.of(response()));

		ResponseEntity<List<AddressResponse>> result = addressController.getAll(null, null);

		assertThat(result.getBody()).containsExactly(response());
	}

	@Test
	void getById_returnsAddress() {
		when(addressService.getById(1L)).thenReturn(response());

		ResponseEntity<AddressResponse> result = addressController.getById(1L);

		assertThat(result.getBody()).isEqualTo(response());
	}

	@Test
	void add_returns201WithCreatedAddress() {
		CreateAddressRequest request = new CreateAddressRequest(42L, 12L, 5L, "Street", "12", "Desc", true);
		when(addressService.add(request)).thenReturn(response());

		ResponseEntity<AddressResponse> result = addressController.add(request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(result.getBody()).isEqualTo(response());
	}

	@Test
	void update_returnsUpdatedAddress() {
		UpdateAddressRequest request = new UpdateAddressRequest(5L, "Street2", "13", "Desc2", true);
		when(addressService.update(1L, request)).thenReturn(response());

		ResponseEntity<AddressResponse> result = addressController.update(1L, request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isEqualTo(response());
	}

	@Test
	void delete_returns204AndDelegatesToService() {
		ResponseEntity<Void> result = addressController.delete(1L);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(addressService).delete(1L);
	}

}
