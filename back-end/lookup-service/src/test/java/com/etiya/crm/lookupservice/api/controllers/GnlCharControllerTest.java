package com.etiya.crm.lookupservice.api.controllers;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.etiya.crm.lookupservice.business.abstracts.GnlCharService;
import com.etiya.crm.shared.contracts.gnlchar.CreateGnlCharRequest;
import com.etiya.crm.shared.contracts.gnlchar.GnlCharResponse;
import com.etiya.crm.shared.contracts.gnlchar.UpdateGnlCharRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GnlCharControllerTest {

	@Mock
	private GnlCharService gnlCharService;

	@InjectMocks
	private GnlCharController controller;

	private static GnlCharResponse response() {
		return new GnlCharResponse(1L, "Color", "desc", "cls", "COLOR", true, null, null, null, null);
	}

	@Test
	void getAll_returnsList() {
		when(gnlCharService.getAll()).thenReturn(List.of(response()));

		ResponseEntity<List<GnlCharResponse>> result = controller.getAll();

		assertThat(result.getBody()).containsExactly(response());
	}

	@Test
	void getById_returnsCharacteristic() {
		when(gnlCharService.getById(1L)).thenReturn(response());

		ResponseEntity<GnlCharResponse> result = controller.getById(1L);

		assertThat(result.getBody()).isEqualTo(response());
	}

	@Test
	void add_returns201WithCreated() {
		CreateGnlCharRequest request = new CreateGnlCharRequest("Color", "desc", "cls", "COLOR", true);
		when(gnlCharService.add(request)).thenReturn(response());

		ResponseEntity<GnlCharResponse> result = controller.add(request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(result.getBody()).isEqualTo(response());
	}

	@Test
	void update_returnsUpdated() {
		UpdateGnlCharRequest request = new UpdateGnlCharRequest("Color2", "desc2", "cls", true);
		when(gnlCharService.update(1L, request)).thenReturn(response());

		ResponseEntity<GnlCharResponse> result = controller.update(1L, request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void delete_returns204AndDelegates() {
		ResponseEntity<Void> result = controller.delete(1L);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(gnlCharService).delete(1L);
	}

}
