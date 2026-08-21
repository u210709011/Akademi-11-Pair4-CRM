package com.etiya.crm.partyservice.api.controllers;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.etiya.crm.partyservice.business.abstracts.IndividualService;
import com.etiya.crm.shared.contracts.individual.CreateIndividualCommand;
import com.etiya.crm.shared.contracts.individual.IndividualResponse;
import com.etiya.crm.shared.contracts.individual.PartyRoleResponse;
import com.etiya.crm.shared.contracts.individual.UpdateIndividualCommand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndividualControllerTest {

	@Mock
	private IndividualService individualService;

	@InjectMocks
	private IndividualController controller;

	@Test
	void createIndividual_returns201Created() {
		CreateIndividualCommand command = new CreateIndividualCommand("Ahmet", null, "Yilmaz",
				LocalDate.of(1990, 6, 15), 1L, null, null, "10000000146");
		PartyRoleResponse created = new PartyRoleResponse(10L, 100L);
		when(individualService.createIndividual(command)).thenReturn(created);

		ResponseEntity<PartyRoleResponse> response = controller.createIndividual(command);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isSameAs(created);
	}

	@Test
	void existsByNationalId_returns200WithBoolean() {
		when(individualService.existsByNationalId("10000000146")).thenReturn(true);

		ResponseEntity<Boolean> response = controller.existsByNationalId("10000000146");

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isTrue();
	}

	@Test
	void getByPartyRoleId_returns200() {
		IndividualResponse individual = new IndividualResponse("Ahmet", null, "Yilmaz", null, 1L, null, null,
				"10000000146");
		when(individualService.getByPartyRoleId(100L)).thenReturn(individual);

		ResponseEntity<IndividualResponse> response = controller.getByPartyRoleId(100L);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isSameAs(individual);
	}

	@Test
	void updateByPartyRoleId_returns200() {
		UpdateIndividualCommand command = new UpdateIndividualCommand("Ahmet", null, "Yilmazoglu", 1L, null, null,
				LocalDate.of(1990, 6, 15), "10000000146");
		IndividualResponse updated = new IndividualResponse("Ahmet", null, "Yilmazoglu", null, 1L, null, null,
				"10000000146");
		when(individualService.updateByPartyRoleId(100L, command)).thenReturn(updated);

		ResponseEntity<IndividualResponse> response = controller.updateByPartyRoleId(100L, command);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isSameAs(updated);
	}
}
