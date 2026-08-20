package com.etiya.crm.lookupservice.api.controllers;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.etiya.crm.lookupservice.business.abstracts.GnlCharValService;
import com.etiya.crm.shared.contracts.gnlcharval.CreateGnlCharValRequest;
import com.etiya.crm.shared.contracts.gnlcharval.GnlCharValResponse;
import com.etiya.crm.shared.contracts.gnlcharval.UpdateGnlCharValRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GnlCharValControllerTest {

	@Mock
	private GnlCharValService gnlCharValService;

	@InjectMocks
	private GnlCharValController controller;

	private static GnlCharValResponse response() {
		return new GnlCharValResponse(1L, 2L, true, "Red", "RED", LocalDate.now(), null, true, null, null, null,
				null);
	}

	@Test
	void getAll_returnsList() {
		when(gnlCharValService.getAll()).thenReturn(List.of(response()));

		ResponseEntity<List<GnlCharValResponse>> result = controller.getAll();

		assertThat(result.getBody()).containsExactly(response());
	}

	@Test
	void getById_returnsCharacteristicValue() {
		when(gnlCharValService.getById(1L)).thenReturn(response());

		ResponseEntity<GnlCharValResponse> result = controller.getById(1L);

		assertThat(result.getBody()).isEqualTo(response());
	}

	@Test
	void add_returns201WithCreated() {
		CreateGnlCharValRequest request = new CreateGnlCharValRequest(2L, true, "Red", "RED", LocalDate.now(), null,
				true);
		when(gnlCharValService.add(request)).thenReturn(response());

		ResponseEntity<GnlCharValResponse> result = controller.add(request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	void update_returnsUpdated() {
		UpdateGnlCharValRequest request = new UpdateGnlCharValRequest(true, "Red2", "RED", LocalDate.now(), null,
				true);
		when(gnlCharValService.update(1L, request)).thenReturn(response());

		ResponseEntity<GnlCharValResponse> result = controller.update(1L, request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void delete_returns204AndDelegates() {
		ResponseEntity<Void> result = controller.delete(1L);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(gnlCharValService).delete(1L);
	}

}
