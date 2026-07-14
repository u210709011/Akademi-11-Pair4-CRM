package com.etiya.crm.partyservice.business.concretes;

import com.etiya.crm.partyservice.business.abstracts.IndividualService;
import com.etiya.crm.partyservice.business.abstracts.LookupCacheService;
import com.etiya.crm.partyservice.business.dtos.requests.CreateIndividualCommand;
import com.etiya.crm.partyservice.business.dtos.responses.PartyRoleResponse;
import com.etiya.crm.partyservice.business.rules.IndividualBusinessRules;
import com.etiya.crm.partyservice.dataAccess.abstracts.IndividualRepository;
import com.etiya.crm.partyservice.dataAccess.abstracts.PartyRepository;
import com.etiya.crm.partyservice.dataAccess.abstracts.PartyRoleRepository;
import com.etiya.crm.partyservice.entities.concretes.Individual;
import com.etiya.crm.partyservice.entities.concretes.Party;
import com.etiya.crm.partyservice.entities.concretes.PartyRole;
import com.etiya.crm.partyservice.mapper.IndividualMapper;
import com.etiya.crm.shared.events.KafkaTopics;
import com.etiya.crm.shared.events.outbox.OutboxEventPublisher;
import com.etiya.crm.shared.events.party.PartyEvent;
import com.etiya.crm.shared.events.party.PartyEventTypes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IndividualManager implements IndividualService {

    private static final String LOOKUP_GROUP_PARTY_TYPE = "PARTY_TYPE";
    private static final String LOOKUP_CODE_INDIVIDUAL = "INDIVIDUAL";
    private static final String LOOKUP_GROUP_PARTY_ROLE_TYPE = "PARTY_ROLE_TYPE";
    private static final String LOOKUP_CODE_CUSTOMER = "CUSTOMER";

    private final PartyRepository partyRepository;
    private final IndividualRepository individualRepository;
    private final PartyRoleRepository partyRoleRepository;
    private final OutboxEventPublisher outboxEventPublisher;
    private final IndividualMapper individualMapper;
    private final IndividualBusinessRules individualBusinessRules;
    private final LookupCacheService lookupCacheService;

    @Override
    @Transactional
    public PartyRoleResponse createIndividual(CreateIndividualCommand command) {
        individualBusinessRules.checkNationalIdNotDuplicate(command.getNationalId());

        Party party = new Party();
        party.setPartyTypeId(lookupCacheService.resolveIdByCode(LOOKUP_GROUP_PARTY_TYPE, LOOKUP_CODE_INDIVIDUAL));
        party = partyRepository.save(party);

        Individual individual = individualMapper.toEntity(command);
        individual.setParty(party);
        individualRepository.save(individual);

        PartyRole partyRole = new PartyRole();
        partyRole.setPartyRoleTypeId(
                lookupCacheService.resolveIdByCode(LOOKUP_GROUP_PARTY_ROLE_TYPE, LOOKUP_CODE_CUSTOMER));
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
        PartyEvent payload = new PartyEvent(
                UUID.randomUUID(),
                PartyEventTypes.INDIVIDUAL_PARTY_CREATED,
                partyRoleId,
                command.getFirstName(),
                command.getLastName(),
                command.getNationalId());

        outboxEventPublisher.publish(KafkaTopics.PARTY_AGGREGATE_TYPE, String.valueOf(partyRoleId),
                PartyEventTypes.INDIVIDUAL_PARTY_CREATED, payload);
    }
}
