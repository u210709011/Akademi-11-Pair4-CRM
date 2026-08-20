package com.etiya.crm.lookupservice.api.controllers;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.etiya.crm.lookupservice.business.abstracts.RsrcSpecService;
import com.etiya.crm.shared.contracts.rsrcspec.CreateRsrcSpecRequest;
import com.etiya.crm.shared.contracts.rsrcspec.RsrcSpecResponse;
import com.etiya.crm.shared.contracts.rsrcspec.UpdateRsrcSpecRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RsrcSpecControllerTest {

	@Mock
	private RsrcSpecService rsrcSpecService;

	@InjectMocks
	private RsrcSpecController controller;

	private static RsrcSpecResponse response() {
		return new RsrcSpecResponse(1L, "SIM Card", "desc", 1L, "SIM", null, null, null, null);
	}

	@Test
	void getAll_returnsList() {
		when(rsrcSpecService.getAll()).thenReturn(List.of(response()));

		ResponseEntity<List<RsrcSpecResponse>> result = controller.getAll();

		assertThat(result.getBody()).containsExactly(response());
	}

	@Test
	void getById_returnsSpec() {
		when(rsrcSpecService.getById(1L)).thenReturn(response());

		ResponseEntity<RsrcSpecResponse> result = controller.getById(1L);

		assertThat(result.getBody()).isEqualTo(response());
	}

	@Test
	void add_returns201WithCreated() {
		CreateRsrcSpecRequest request = new CreateRsrcSpecRequest("SIM Card", "desc", 1L, "SIM");
		when(rsrcSpecService.add(request)).thenReturn(response());

		ResponseEntity<RsrcSpecResponse> result = controller.add(request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	void update_returnsUpdated() {
		UpdateRsrcSpecRequest request = new UpdateRsrcSpecRequest("SIM Card 2", "desc2", 1L, "SIM");
		when(rsrcSpecService.update(1L, request)).thenReturn(response());

		ResponseEntity<RsrcSpecResponse> result = controller.update(1L, request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void delete_returns204AndDelegates() {
		ResponseEntity<Void> result = controller.delete(1L);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(rsrcSpecService).delete(1L);
	}

}
