package com.etiya.crm.partyservice.business.concretes;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.etiya.crm.partyservice.business.abstracts.PartyEventPublisher;
import com.etiya.crm.partyservice.dataAccess.abstracts.PartyRoleRepository;
import com.etiya.crm.partyservice.entities.concretes.Individual;
import com.etiya.crm.partyservice.entities.concretes.PartyRole;
import com.etiya.crm.shared.contracts.individual.CreateIndividualCommand;
import com.etiya.crm.shared.events.KafkaTopics;
import com.etiya.crm.shared.events.outbox.OutboxEventPublisher;
import com.etiya.crm.shared.events.party.PartyEvent;
import com.etiya.crm.shared.events.party.PartyEventTypes;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PartyEventPublisherImpl implements PartyEventPublisher {

	private final OutboxEventPublisher outboxEventPublisher;
	private final PartyRoleRepository partyRoleRepository;

	@Override
	public void publishIndividualPartyCreated(Long partyRoleId, CreateIndividualCommand command,
			Long partyRoleTypeId) {
		PartyEvent payload = new PartyEvent(
				UUID.randomUUID(),
				PartyEventTypes.INDIVIDUAL_PARTY_CREATED,
				partyRoleId,
				command.firstName(),
				command.middleName(),
				command.lastName(),
				command.nationalId(),
				partyRoleTypeId);

		outboxEventPublisher.publish(KafkaTopics.PARTY_AGGREGATE_TYPE, String.valueOf(partyRoleId),
				PartyEventTypes.INDIVIDUAL_PARTY_CREATED, payload);
	}

	@Override
	public void publishIndividualUpdated(Long partyRoleId, Individual individual) {
		Long partyRoleTypeId = partyRoleRepository.findById(partyRoleId)
				.map(PartyRole::getPartyRoleTypeId)
				.orElse(null);

		PartyEvent payload = new PartyEvent(
				UUID.randomUUID(),
				PartyEventTypes.INDIVIDUAL_UPDATED,
				partyRoleId,
				individual.getFirstName(),
				individual.getMiddleName(),
				individual.getLastName(),
				individual.getNationalId(),
				partyRoleTypeId);

		outboxEventPublisher.publish(KafkaTopics.PARTY_AGGREGATE_TYPE, String.valueOf(partyRoleId),
				PartyEventTypes.INDIVIDUAL_UPDATED, payload);
	}
}
