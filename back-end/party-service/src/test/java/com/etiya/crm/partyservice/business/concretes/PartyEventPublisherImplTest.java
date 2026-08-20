package com.etiya.crm.partyservice.business.concretes;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.partyservice.dataAccess.abstracts.PartyRoleRepository;
import com.etiya.crm.partyservice.entities.concretes.Individual;
import com.etiya.crm.partyservice.entities.concretes.PartyRole;
import com.etiya.crm.shared.contracts.individual.CreateIndividualCommand;
import com.etiya.crm.shared.events.KafkaTopics;
import com.etiya.crm.shared.events.outbox.OutboxEventPublisher;
import com.etiya.crm.shared.events.party.PartyEvent;
import com.etiya.crm.shared.events.party.PartyEventTypes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartyEventPublisherImplTest {

	@Mock
	private OutboxEventPublisher outboxEventPublisher;

	@Mock
	private PartyRoleRepository partyRoleRepository;

	@InjectMocks
	private PartyEventPublisherImpl publisher;

	@Test
	void publishIndividualPartyCreated_buildsAndPublishesEvent() {
		CreateIndividualCommand command = new CreateIndividualCommand("Ahmet", null, "Yilmaz",
				LocalDate.of(1990, 6, 15), 1L, null, null, "10000000146");

		publisher.publishIndividualPartyCreated(100L, command, 700L);

		ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
		verify(outboxEventPublisher).publish(eq(KafkaTopics.PARTY_AGGREGATE_TYPE), eq("100"),
				eq(PartyEventTypes.INDIVIDUAL_PARTY_CREATED), payloadCaptor.capture());
		PartyEvent payload = (PartyEvent) payloadCaptor.getValue();
		assertThat(payload.partyRoleId()).isEqualTo(100L);
		assertThat(payload.firstName()).isEqualTo("Ahmet");
		assertThat(payload.nationalId()).isEqualTo("10000000146");
		assertThat(payload.partyRoleTypeId()).isEqualTo(700L);
		assertThat(payload.type()).isEqualTo(PartyEventTypes.INDIVIDUAL_PARTY_CREATED);
	}

	@Test
	void publishIndividualUpdated_resolvesPartyRoleTypeId_whenPartyRoleFound() {
		PartyRole role = new PartyRole();
		role.setPartyRoleTypeId(700L);
		when(partyRoleRepository.findById(100L)).thenReturn(Optional.of(role));
		Individual individual = new Individual();
		individual.setFirstName("Ahmet");
		individual.setLastName("Yilmazoglu");
		individual.setNationalId("10000000146");

		publisher.publishIndividualUpdated(100L, individual);

		ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
		verify(outboxEventPublisher).publish(eq(KafkaTopics.PARTY_AGGREGATE_TYPE), eq("100"),
				eq(PartyEventTypes.INDIVIDUAL_UPDATED), payloadCaptor.capture());
		PartyEvent payload = (PartyEvent) payloadCaptor.getValue();
		assertThat(payload.lastName()).isEqualTo("Yilmazoglu");
		assertThat(payload.partyRoleTypeId()).isEqualTo(700L);
	}

	@Test
	void publishIndividualUpdated_usesNullPartyRoleTypeId_whenPartyRoleNotFound() {
		when(partyRoleRepository.findById(100L)).thenReturn(Optional.empty());
		Individual individual = new Individual();
		individual.setFirstName("Ahmet");

		publisher.publishIndividualUpdated(100L, individual);

		ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
		verify(outboxEventPublisher).publish(any(), any(), any(), payloadCaptor.capture());
		PartyEvent payload = (PartyEvent) payloadCaptor.getValue();
		assertThat(payload.partyRoleTypeId()).isNull();
	}
}
