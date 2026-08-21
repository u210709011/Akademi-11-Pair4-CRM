package com.etiya.crm.customerservice.api.controllers;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.etiya.crm.customerservice.business.abstracts.CustomerIndividualService;
import com.etiya.crm.customerservice.business.dtos.requests.UpdateIndividualInfo;
import com.etiya.crm.shared.contracts.individual.IndividualResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerIndividualControllerTest {

	@Mock
	private CustomerIndividualService individualService;

	@InjectMocks
	private CustomerIndividualController controller;

	@Test
	void getIndividual_returns200WithServiceResult() {
		IndividualResponse individual = new IndividualResponse("Ahmet", null, "Yilmaz", null, 1L, null, null,
				"10000000146");
		when(individualService.getIndividual(10L)).thenReturn(individual);

		ResponseEntity<IndividualResponse> response = controller.getIndividual(10L);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isSameAs(individual);
	}

	@Test
	void updateIndividual_returns200WithUpdatedResult() {
		UpdateIndividualInfo request = new UpdateIndividualInfo("Ahmet", null, "Yilmazoglu", 1L, null, null,
				LocalDate.of(1990, 6, 15), "10000000146");
		IndividualResponse updated = new IndividualResponse("Ahmet", null, "Yilmazoglu", null, 1L, null, null,
				"10000000146");
		when(individualService.updateIndividual(10L, request)).thenReturn(updated);

		ResponseEntity<IndividualResponse> response = controller.updateIndividual(10L, request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isSameAs(updated);
	}
}
