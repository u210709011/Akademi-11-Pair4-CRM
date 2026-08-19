package com.etiya.crm.lookupservice.api.controllers;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.etiya.crm.lookupservice.business.abstracts.SrvcSpecService;
import com.etiya.crm.shared.contracts.srvcspec.CreateSrvcSpecRequest;
import com.etiya.crm.shared.contracts.srvcspec.SrvcSpecResponse;
import com.etiya.crm.shared.contracts.srvcspec.UpdateSrvcSpecRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SrvcSpecControllerTest {

	@Mock
	private SrvcSpecService srvcSpecService;

	@InjectMocks
	private SrvcSpecController controller;

	private static SrvcSpecResponse response() {
		return new SrvcSpecResponse(1L, "Mobile Line", "desc", "MOB", 1L, null, null, null, null);
	}

	@Test
	void getAll_returnsList() {
		when(srvcSpecService.getAll()).thenReturn(List.of(response()));

		ResponseEntity<List<SrvcSpecResponse>> result = controller.getAll();

		assertThat(result.getBody()).containsExactly(response());
	}

	@Test
	void getById_returnsSpec() {
		when(srvcSpecService.getById(1L)).thenReturn(response());

		ResponseEntity<SrvcSpecResponse> result = controller.getById(1L);

		assertThat(result.getBody()).isEqualTo(response());
	}

	@Test
	void add_returns201WithCreated() {
		CreateSrvcSpecRequest request = new CreateSrvcSpecRequest("Mobile Line", "desc", "MOB", 1L);
		when(srvcSpecService.add(request)).thenReturn(response());

		ResponseEntity<SrvcSpecResponse> result = controller.add(request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	void update_returnsUpdated() {
		UpdateSrvcSpecRequest request = new UpdateSrvcSpecRequest("Mobile Line 2", "desc2", "MOB", 1L);
		when(srvcSpecService.update(1L, request)).thenReturn(response());

		ResponseEntity<SrvcSpecResponse> result = controller.update(1L, request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void delete_returns204AndDelegates() {
		ResponseEntity<Void> result = controller.delete(1L);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(srvcSpecService).delete(1L);
	}

}
