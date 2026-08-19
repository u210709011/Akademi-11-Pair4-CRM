package com.etiya.crm.lookupservice.api.controllers;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.etiya.crm.lookupservice.business.abstracts.GnlStService;
import com.etiya.crm.shared.contracts.gnlst.CreateGnlStRequest;
import com.etiya.crm.shared.contracts.gnlst.GnlStResponse;
import com.etiya.crm.shared.contracts.gnlst.UpdateGnlStRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GnlStControllerTest {

	@Mock
	private GnlStService gnlStService;

	@InjectMocks
	private GnlStController controller;

	private static GnlStResponse response() {
		return new GnlStResponse(1L, "Wait", "desc", "WAIT", true, "CUST_ORD", "CUST_ORD", null, null, null, null);
	}

	@Test
	void getAll_returnsByEntCodeName_whenProvided() {
		when(gnlStService.getAllByEntCodeName("CUST_ORD")).thenReturn(List.of(response()));

		ResponseEntity<List<GnlStResponse>> result = controller.getAll("CUST_ORD");

		assertThat(result.getBody()).containsExactly(response());
		verify(gnlStService, times(0)).getAll();
	}

	@Test
	void getAll_returnsAll_whenEntCodeNameMissing() {
		when(gnlStService.getAll()).thenReturn(List.of(response()));

		ResponseEntity<List<GnlStResponse>> result = controller.getAll(null);

		assertThat(result.getBody()).containsExactly(response());
	}

	@Test
	void getById_returnsStatus() {
		when(gnlStService.getById(1L)).thenReturn(response());

		ResponseEntity<GnlStResponse> result = controller.getById(1L);

		assertThat(result.getBody()).isEqualTo(response());
	}

	@Test
	void getByEntCodeNameAndShrtCode_returnsStatus() {
		when(gnlStService.getByEntCodeNameAndShrtCode("CUST_ORD", "WAIT")).thenReturn(response());

		ResponseEntity<GnlStResponse> result = controller.getByEntCodeNameAndShrtCode("CUST_ORD", "WAIT");

		assertThat(result.getBody()).isEqualTo(response());
	}

	@Test
	void add_returns201WithCreated() {
		CreateGnlStRequest request = new CreateGnlStRequest("Wait", "desc", "WAIT", true, "CUST_ORD", "CUST_ORD");
		when(gnlStService.add(request)).thenReturn(response());

		ResponseEntity<GnlStResponse> result = controller.add(request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	void update_returnsUpdated() {
		UpdateGnlStRequest request = new UpdateGnlStRequest("Wait2", "desc2", "WAIT", true, "CUST_ORD", "CUST_ORD");
		when(gnlStService.update(1L, request)).thenReturn(response());

		ResponseEntity<GnlStResponse> result = controller.update(1L, request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void delete_returns204AndDelegates() {
		ResponseEntity<Void> result = controller.delete(1L);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(gnlStService).delete(1L);
	}

}
