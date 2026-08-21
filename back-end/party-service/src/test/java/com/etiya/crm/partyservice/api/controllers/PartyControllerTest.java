package com.etiya.crm.partyservice.api.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.etiya.crm.partyservice.business.abstracts.PartyService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PartyControllerTest {

	@Mock
	private PartyService partyService;

	@InjectMocks
	private PartyController controller;

	@Test
	void deleteParty_returns204NoContent() {
		ResponseEntity<Void> response = controller.deleteParty(10L);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(partyService).softDeleteParty(10L);
	}
}
