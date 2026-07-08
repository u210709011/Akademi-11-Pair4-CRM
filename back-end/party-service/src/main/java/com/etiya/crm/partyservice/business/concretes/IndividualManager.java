package com.etiya.crm.partyservice.business.concretes;

import com.etiya.crm.partyservice.business.abstracts.IndividualService;
import com.etiya.crm.partyservice.business.dtos.requests.CreateIndividualCommand;
import com.etiya.crm.partyservice.business.dtos.responses.PartyRoleResponse;
import com.etiya.crm.partyservice.business.rules.IndividualBusinessRules;
import com.etiya.crm.partyservice.config.PartyLookupProperties;
import com.etiya.crm.partyservice.constants.StatusIds;
import com.etiya.crm.partyservice.dataAccess.abstracts.IndividualRepository;
import com.etiya.crm.partyservice.dataAccess.abstracts.PartyRepository;
import com.etiya.crm.partyservice.dataAccess.abstracts.PartyRoleRepository;
import com.etiya.crm.partyservice.entities.concretes.Individual;
import com.etiya.crm.partyservice.entities.concretes.Party;
import com.etiya.crm.partyservice.entities.concretes.PartyRole;
import com.etiya.crm.partyservice.events.PartyEventPayload;
import com.etiya.crm.partyservice.events.PartyEventTypes;
import com.etiya.crm.partyservice.mapper.IndividualMapper;
import com.etiya.crm.partyservice.outbox.Outbox;
import com.etiya.crm.partyservice.outbox.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IndividualManager implements IndividualService {

    private static final String AGGREGATE_TYPE = "party";

    private final PartyRepository partyRepository;
    private final IndividualRepository individualRepository;
    private final PartyRoleRepository partyRoleRepository;
    private final OutboxRepository outboxRepository;
    private final IndividualMapper individualMapper;
    private final IndividualBusinessRules individualBusinessRules;
    private final PartyLookupProperties lookupProperties;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public PartyRoleResponse createIndividual(CreateIndividualCommand command) {
        individualBusinessRules.checkNationalIdNotDuplicate(command.getNationalId());

        Party party = new Party();
        party.setPartyTypeId(lookupProperties.getPartyTypeIndividualId());
        party.setStatusId(StatusIds.ACTIVE);
        party = partyRepository.save(party);

        Individual individual = individualMapper.toEntity(command);
        individual.setStatusId(StatusIds.ACTIVE);
        individual.setParty(party);
        individualRepository.save(individual);

        PartyRole partyRole = new PartyRole();
        partyRole.setPartyRoleTypeId(lookupProperties.getPartyRoleTypeCustomerId());
        partyRole.setStatusId(StatusIds.ACTIVE);
        partyRole.setParty(party);
        partyRole = partyRoleRepository.save(partyRole);

        publishIndividualPartyCreatedEvent(partyRole.getPartyRoleId(), command);

        return new PartyRoleResponse(party.getPartyId(), partyRole.getPartyRoleId());
    }

    @Override
    public boolean existsByNationalId(String nationalId) {
        return individualRepository.existsByNationalId(nationalId);
    }

    /**
     * Ayni transaction icinde outbox tablosuna insert eder; Debezium bu satiri
     * WAL'den okuyup "party-events" topic'ine yayinlar (relay/polling YOK).
     */
    private void publishIndividualPartyCreatedEvent(Long partyRoleId, CreateIndividualCommand command) {
        UUID eventId = UUID.randomUUID();
        PartyEventPayload payload = new PartyEventPayload(
                eventId,
                PartyEventTypes.INDIVIDUAL_PARTY_CREATED,
                partyRoleId,
                command.getFirstName(),
                command.getLastName(),
                command.getNationalId());

        Outbox outbox = new Outbox();
        outbox.setId(eventId);
        outbox.setAggregateType(AGGREGATE_TYPE);
        outbox.setAggregateId(String.valueOf(partyRoleId));
        outbox.setType(PartyEventTypes.INDIVIDUAL_PARTY_CREATED);
        outbox.setPayload(writeJson(payload));
        outboxRepository.save(outbox);
    }

    private String writeJson(PartyEventPayload payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("PartyEventPayload serialize edilemedi", e);
        }
    }
}
