package com.etiya.crm.partyservice.business.concretes;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.partyservice.business.abstracts.LookupCacheService;
import com.etiya.crm.partyservice.business.abstracts.PartyEventPublisher;
import com.etiya.crm.partyservice.business.exceptions.IndividualNotFoundException;
import com.etiya.crm.partyservice.business.exceptions.PartyRoleNotFoundException;
import com.etiya.crm.partyservice.business.rules.IndividualBusinessRules;
import com.etiya.crm.partyservice.dataAccess.abstracts.IndividualRepository;
import com.etiya.crm.partyservice.dataAccess.abstracts.PartyRepository;
import com.etiya.crm.partyservice.dataAccess.abstracts.PartyRoleRepository;
import com.etiya.crm.partyservice.entities.concretes.Individual;
import com.etiya.crm.partyservice.entities.concretes.Party;
import com.etiya.crm.partyservice.entities.concretes.PartyRole;
import com.etiya.crm.partyservice.mapper.IndividualMapper;
import com.etiya.crm.shared.contracts.gnltp.GnlTpCodes;
import com.etiya.crm.shared.contracts.gnltp.GnlTpGroups;
import com.etiya.crm.shared.contracts.individual.CreateIndividualCommand;
import com.etiya.crm.shared.contracts.individual.IndividualResponse;
import com.etiya.crm.shared.contracts.individual.PartyRoleResponse;
import com.etiya.crm.shared.contracts.individual.UpdateIndividualCommand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndividualManagerTest {

	@Mock
	private PartyRepository partyRepository;

	@Mock
	private IndividualRepository individualRepository;

	@Mock
	private PartyRoleRepository partyRoleRepository;

	@Mock
	private IndividualMapper individualMapper;

	@Mock
	private IndividualBusinessRules individualBusinessRules;

	@Mock
	private LookupCacheService lookupCacheService;

	@Mock
	private PartyEventPublisher partyEventPublisher;

	@InjectMocks
	private IndividualManager manager;

	@Test
	void createIndividual_checksDuplicateFirst_thenCreatesPartyIndividualAndRole_thenPublishesEvent() {
		CreateIndividualCommand command = new CreateIndividualCommand("Ahmet", null, "Yilmaz",
				LocalDate.of(1990, 6, 15), 1L, null, null, "10000000146");
		when(lookupCacheService.resolveIdByCode(GnlTpGroups.PARTY_TYPE, GnlTpCodes.INDIVIDUAL)).thenReturn(163L);
		when(partyRepository.save(any())).thenAnswer(invocation -> {
			Party party = invocation.getArgument(0);
			party.setPartyId(10L);
			return party;
		});
		Individual individual = new Individual();
		when(individualMapper.toEntity(command)).thenReturn(individual);
		when(lookupCacheService.resolveIdByCode(GnlTpGroups.PARTY_ROLE_TYPE, GnlTpCodes.CUSTOMER_ROLE))
				.thenReturn(700L);
		when(partyRoleRepository.save(any())).thenAnswer(invocation -> {
			PartyRole role = invocation.getArgument(0);
			role.setPartyRoleId(100L);
			return role;
		});

		PartyRoleResponse result = manager.createIndividual(command);

		assertThat(result.partyId()).isEqualTo(10L);
		assertThat(result.partyRoleId()).isEqualTo(100L);
		verify(individualBusinessRules).checkNationalIdNotDuplicate("10000000146");
		assertThat(individual.getParty().getPartyId()).isEqualTo(10L);
		verify(partyEventPublisher).publishIndividualPartyCreated(100L, command, 700L);
	}

	@Test
	void existsByNationalId_delegatesToRepository() {
		when(individualRepository.existsByNationalIdAndActiveTrue("10000000146")).thenReturn(true);

		assertThat(manager.existsByNationalId("10000000146")).isTrue();
	}

	@Test
	void getByPartyRoleId_returnsMappedResponse() {
		Party party = new Party();
		party.setPartyId(10L);
		PartyRole role = new PartyRole();
		role.setParty(party);
		when(partyRoleRepository.findById(100L)).thenReturn(Optional.of(role));
		Individual individual = new Individual();
		when(individualRepository.findByParty_PartyId(10L)).thenReturn(Optional.of(individual));
		IndividualResponse response = new IndividualResponse("Ahmet", null, "Yilmaz", null, 1L, null, null,
				"10000000146");
		when(individualMapper.toResponse(individual)).thenReturn(response);

		assertThat(manager.getByPartyRoleId(100L)).isSameAs(response);
	}

	@Test
	void getByPartyRoleId_throwsPartyRoleNotFound_whenRoleMissing() {
		when(partyRoleRepository.findById(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager.getByPartyRoleId(999L))
				.isInstanceOf(PartyRoleNotFoundException.class);
	}

	@Test
	void getByPartyRoleId_throwsIndividualNotFound_whenNoIndividualLinkedToParty() {
		Party party = new Party();
		party.setPartyId(10L);
		PartyRole role = new PartyRole();
		role.setParty(party);
		when(partyRoleRepository.findById(100L)).thenReturn(Optional.of(role));
		when(individualRepository.findByParty_PartyId(10L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager.getByPartyRoleId(100L))
				.isInstanceOf(IndividualNotFoundException.class);
	}

	@Test
	void updateByPartyRoleId_checksDuplicateExcludingSelf_updatesAndPublishesEvent() {
		Party party = new Party();
		party.setPartyId(10L);
		PartyRole role = new PartyRole();
		role.setParty(party);
		when(partyRoleRepository.findById(100L)).thenReturn(Optional.of(role));
		Individual individual = new Individual();
		individual.setIndividualId(5L);
		when(individualRepository.findByParty_PartyId(10L)).thenReturn(Optional.of(individual));
		when(individualRepository.save(individual)).thenReturn(individual);
		UpdateIndividualCommand command = new UpdateIndividualCommand("Ahmet", null, "Yilmazoglu", 1L, null, null,
				LocalDate.of(1990, 6, 15), "10000000146");
		IndividualResponse response = new IndividualResponse("Ahmet", null, "Yilmazoglu", null, 1L, null, null,
				"10000000146");
		when(individualMapper.toResponse(individual)).thenReturn(response);

		IndividualResponse result = manager.updateByPartyRoleId(100L, command);

		assertThat(result).isSameAs(response);
		verify(individualBusinessRules).checkNationalIdNotDuplicateForUpdate("10000000146", 5L);
		verify(individualMapper).updateEntity(command, individual);
		verify(partyEventPublisher).publishIndividualUpdated(100L, individual);
	}
}
