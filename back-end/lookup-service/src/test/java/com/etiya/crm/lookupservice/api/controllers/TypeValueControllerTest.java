package com.etiya.crm.lookupservice.api.controllers;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.etiya.crm.lookupservice.business.abstracts.TypeValueService;
import com.etiya.crm.shared.contracts.typevalue.CreateTypeValueRequest;
import com.etiya.crm.shared.contracts.typevalue.TypeValueResponse;
import com.etiya.crm.shared.contracts.typevalue.UpdateTypeValueRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TypeValueControllerTest {

	@Mock
	private TypeValueService typeValueService;

	@InjectMocks
	private TypeValueController controller;

	private static TypeValueResponse response() {
		return new TypeValueResponse(1L, "CUST", 12L, "desc", "val", "module", null, null, null, null);
	}

	@Test
	void getAll_returnsList() {
		when(typeValueService.getAll()).thenReturn(List.of(response()));

		ResponseEntity<List<TypeValueResponse>> result = controller.getAll();

		assertThat(result.getBody()).containsExactly(response());
	}

	@Test
	void getById_returnsTypeValue() {
		when(typeValueService.getById(1L)).thenReturn(response());

		ResponseEntity<TypeValueResponse> result = controller.getById(1L);

		assertThat(result.getBody()).isEqualTo(response());
	}

	@Test
	void getByTableName_returnsTypeValue() {
		when(typeValueService.getByTableName("CUST")).thenReturn(response());

		ResponseEntity<TypeValueResponse> result = controller.getByTableName("CUST");

		assertThat(result.getBody()).isEqualTo(response());
	}

	@Test
	void add_returns201WithCreated() {
		CreateTypeValueRequest request = new CreateTypeValueRequest("CUST", 12L, "desc", "val", "module");
		when(typeValueService.add(request)).thenReturn(response());

		ResponseEntity<TypeValueResponse> result = controller.add(request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	void update_returnsUpdated() {
		UpdateTypeValueRequest request = new UpdateTypeValueRequest("desc2", "val2", "module");
		when(typeValueService.update(1L, request)).thenReturn(response());

		ResponseEntity<TypeValueResponse> result = controller.update(1L, request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void delete_returns204AndDelegates() {
		ResponseEntity<Void> result = controller.delete(1L);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(typeValueService).delete(1L);
	}

}
