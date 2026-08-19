package com.etiya.crm.partyservice.business.concretes;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.partyservice.business.exceptions.PartyNotFoundException;
import com.etiya.crm.partyservice.dataAccess.abstracts.IndividualRepository;
import com.etiya.crm.partyservice.dataAccess.abstracts.PartyRepository;
import com.etiya.crm.partyservice.entities.concretes.Individual;
import com.etiya.crm.partyservice.entities.concretes.Party;
import com.etiya.crm.partyservice.entities.concretes.PartyRole;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartyManagerTest {

	@Mock
	private PartyRepository partyRepository;

	@Mock
	private IndividualRepository individualRepository;

	@InjectMocks
	private PartyManager manager;

	@Test
	void softDeleteParty_deactivatesPartyRolesAndIndividual() {
		Party party = new Party();
		party.setPartyId(10L);
		PartyRole role = new PartyRole();
		role.setActive(true);
		party.getRoles().add(role);
		when(partyRepository.findById(10L)).thenReturn(Optional.of(party));
		Individual individual = new Individual();
		individual.setActive(true);
		when(individualRepository.findByParty_PartyId(10L)).thenReturn(Optional.of(individual));

		manager.softDeleteParty(10L);

		assertThat(party.isActive()).isFalse();
		assertThat(role.isActive()).isFalse();
		assertThat(individual.isActive()).isFalse();
	}

	@Test
	void softDeleteParty_skipsIndividualDeactivation_whenNoIndividualLinked() {
		Party party = new Party();
		party.setPartyId(10L);
		when(partyRepository.findById(10L)).thenReturn(Optional.of(party));
		when(individualRepository.findByParty_PartyId(10L)).thenReturn(Optional.empty());

		manager.softDeleteParty(10L);

		assertThat(party.isActive()).isFalse();
	}

	@Test
	void softDeleteParty_throws_whenPartyNotFound() {
		when(partyRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager.softDeleteParty(99L))
				.isInstanceOf(PartyNotFoundException.class);
	}
}
