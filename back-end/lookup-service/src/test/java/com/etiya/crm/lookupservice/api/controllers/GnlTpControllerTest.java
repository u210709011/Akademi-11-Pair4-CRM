package com.etiya.crm.lookupservice.api.controllers;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.etiya.crm.lookupservice.business.abstracts.GnlTpService;
import com.etiya.crm.shared.contracts.gnltp.CreateGnlTpRequest;
import com.etiya.crm.shared.contracts.gnltp.GnlTpResponse;
import com.etiya.crm.shared.contracts.gnltp.UpdateGnlTpRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GnlTpControllerTest {

	@Mock
	private GnlTpService gnlTpService;

	@InjectMocks
	private GnlTpController controller;

	private static GnlTpResponse response() {
		return new GnlTpResponse(1L, "Istanbul", "desc", "IST", "CITY", "CITY", true, null, null, null, null);
	}

	@Test
	void getAll_returnsByEntCodeName_whenProvided() {
		when(gnlTpService.getAllByEntCodeName("CITY")).thenReturn(List.of(response()));

		ResponseEntity<List<GnlTpResponse>> result = controller.getAll("CITY");

		assertThat(result.getBody()).containsExactly(response());
		verify(gnlTpService, times(0)).getAll();
	}

	@Test
	void getAll_returnsAll_whenEntCodeNameMissing() {
		when(gnlTpService.getAll()).thenReturn(List.of(response()));

		ResponseEntity<List<GnlTpResponse>> result = controller.getAll(null);

		assertThat(result.getBody()).containsExactly(response());
	}

	@Test
	void getById_returnsType() {
		when(gnlTpService.getById(1L)).thenReturn(response());

		ResponseEntity<GnlTpResponse> result = controller.getById(1L);

		assertThat(result.getBody()).isEqualTo(response());
	}

	@Test
	void getByEntCodeNameAndShrtCode_returnsType() {
		when(gnlTpService.getByEntCodeNameAndShrtCode("CITY", "IST")).thenReturn(response());

		ResponseEntity<GnlTpResponse> result = controller.getByEntCodeNameAndShrtCode("CITY", "IST");

		assertThat(result.getBody()).isEqualTo(response());
	}

	@Test
	void add_returns201WithCreated() {
		CreateGnlTpRequest request = new CreateGnlTpRequest("Istanbul", "desc", "IST", "CITY", "CITY", true);
		when(gnlTpService.add(request)).thenReturn(response());

		ResponseEntity<GnlTpResponse> result = controller.add(request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	void update_returnsUpdated() {
		UpdateGnlTpRequest request = new UpdateGnlTpRequest("Istanbul2", "desc2", "IST", "CITY", "CITY", true);
		when(gnlTpService.update(1L, request)).thenReturn(response());

		ResponseEntity<GnlTpResponse> result = controller.update(1L, request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void delete_returns204AndDelegates() {
		ResponseEntity<Void> result = controller.delete(1L);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(gnlTpService).delete(1L);
	}

}
