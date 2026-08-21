package com.etiya.crm.partyservice.mapper;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.etiya.crm.partyservice.entities.concretes.Individual;
import com.etiya.crm.shared.contracts.individual.CreateIndividualCommand;
import com.etiya.crm.shared.contracts.individual.IndividualResponse;
import com.etiya.crm.shared.contracts.individual.UpdateIndividualCommand;

import static org.assertj.core.api.Assertions.assertThat;

class IndividualMapperTest {

	private final IndividualMapper mapper = new IndividualMapperImpl();

	@Test
	void toEntity_mapsFieldsAndIgnoresManagedColumns() {
		CreateIndividualCommand command = new CreateIndividualCommand("Ahmet", "Can", "Yilmaz",
				LocalDate.of(1990, 6, 15), 1L, "Ayse", "Mehmet", "10000000146");

		Individual individual = mapper.toEntity(command);

		assertThat(individual.getFirstName()).isEqualTo("Ahmet");
		assertThat(individual.getNationalId()).isEqualTo("10000000146");
		assertThat(individual.getIndividualId()).isNull();
		assertThat(individual.getParty()).isNull();
	}

	@Test
	void toResponse_mapsAllFields() {
		Individual individual = new Individual();
		individual.setFirstName("Ahmet");
		individual.setLastName("Yilmaz");
		individual.setNationalId("10000000146");
		individual.setBirthDate(LocalDate.of(1990, 6, 15));

		IndividualResponse response = mapper.toResponse(individual);

		assertThat(response.firstName()).isEqualTo("Ahmet");
		assertThat(response.lastName()).isEqualTo("Yilmaz");
		assertThat(response.nationalId()).isEqualTo("10000000146");
	}

	@Test
	void updateEntity_overwritesFields_butNotManagedColumns() {
		Individual individual = new Individual();
		individual.setIndividualId(5L);
		individual.setFirstName("Ahmet");
		individual.setLastName("Yilmaz");
		UpdateIndividualCommand command = new UpdateIndividualCommand("Ahmet", null, "Yilmazoglu", 1L, null, null,
				LocalDate.of(1990, 6, 15), "10000000146");

		mapper.updateEntity(command, individual);

		assertThat(individual.getLastName()).isEqualTo("Yilmazoglu");
		assertThat(individual.getNationalId()).isEqualTo("10000000146");
		assertThat(individual.getIndividualId()).isEqualTo(5L);
	}
}
