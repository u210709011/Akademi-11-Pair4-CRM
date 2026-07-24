package com.etiya.crm.partyservice.business.concretes;

import com.etiya.crm.partyservice.business.abstracts.IndividualService;
import com.etiya.crm.partyservice.business.abstracts.LookupCacheService;
import com.etiya.crm.partyservice.business.exceptions.BusinessException;
import com.etiya.crm.partyservice.business.rules.IndividualBusinessRules;
import com.etiya.crm.partyservice.dataAccess.abstracts.IndividualRepository;
import com.etiya.crm.partyservice.dataAccess.abstracts.PartyRepository;
import com.etiya.crm.partyservice.dataAccess.abstracts.PartyRoleRepository;
import com.etiya.crm.partyservice.entities.concretes.Individual;
import com.etiya.crm.partyservice.entities.concretes.Party;
import com.etiya.crm.partyservice.entities.concretes.PartyRole;
import com.etiya.crm.partyservice.mapper.IndividualMapper;
import com.etiya.crm.shared.contracts.individual.CreateIndividualCommand;
import com.etiya.crm.shared.contracts.individual.IndividualResponse;
import com.etiya.crm.shared.contracts.individual.PartyRoleResponse;
import com.etiya.crm.shared.contracts.individual.UpdateIndividualCommand;
import com.etiya.crm.shared.contracts.gnltp.GnlTpCodes;
import com.etiya.crm.shared.contracts.gnltp.GnlTpGroups;
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
        individualBusinessRules.checkNationalIdNotDuplicate(command.nationalId());

        Party party = new Party();
        party.setPartyTypeId(lookupCacheService.resolveIdByCode(GnlTpGroups.PARTY_TYPE, GnlTpCodes.INDIVIDUAL));
        party = partyRepository.save(party);

        Individual individual = individualMapper.toEntity(command);
        individual.setParty(party);
        individualRepository.save(individual);

        PartyRole partyRole = new PartyRole();
        partyRole.setPartyRoleTypeId(
                lookupCacheService.resolveIdByCode(GnlTpGroups.PARTY_ROLE_TYPE, GnlTpCodes.CUSTOMER_ROLE));
        partyRole.setParty(party);
        partyRole = partyRoleRepository.save(partyRole);

        publishIndividualPartyCreatedEvent(partyRole.getPartyRoleId(), command, partyRole.getPartyRoleTypeId());

        return new PartyRoleResponse(party.getPartyId(), partyRole.getPartyRoleId());
    }

    @Override
    public boolean existsByNationalId(String nationalId) {
        return individualRepository.existsByNationalId(nationalId);
    }

    @Override
    @Transactional(readOnly = true)
    public IndividualResponse getByPartyRoleId(Long partyRoleId) {
        return individualMapper.toResponse(findIndividualOrThrow(partyRoleId));
    }

    @Override
    @Transactional
    public IndividualResponse updateByPartyRoleId(Long partyRoleId, UpdateIndividualCommand command) {
        Individual individual = findIndividualOrThrow(partyRoleId);

        individualBusinessRules.checkNationalIdNotDuplicateForUpdate(command.nationalId(), individual.getIndividualId());

        individualMapper.updateEntity(command, individual);
        individual = individualRepository.save(individual);

        publishIndividualUpdatedEvent(partyRoleId, individual);

        return individualMapper.toResponse(individual);
    }

    private Individual findIndividualOrThrow(Long partyRoleId) {
        PartyRole partyRole = partyRoleRepository.findById(partyRoleId)
                .orElseThrow(() -> new BusinessException("PartyRole bulunamadi: " + partyRoleId));

        return individualRepository.findByParty_PartyId(partyRole.getParty().getPartyId())
                .orElseThrow(() -> new BusinessException(
                        "PartyRole " + partyRoleId + " icin individual bulunamadi (party bireysel degil olabilir)"));
    }

    /**
     * Ayni transaction icinde outbox tablosuna insert eder; Debezium bu satiri
     * WAL'den okuyup "party-events" topic'ine yayinlar (relay/polling YOK).
     */
    private void publishIndividualPartyCreatedEvent(Long partyRoleId, CreateIndividualCommand command,
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

    /**
     * customer-service'in PartyEventListener'i bu event'i zaten dinliyor ve
     * CUSTOMER_SEARCH_VIEW'i senkronluyor - bkz. CUSTOMER_EDIT_INTEGRATION.md SS1.
     * Rol UpdateIndividualCommand ile degismez ama tuketici tarafinda alan hep
     * dolu kalsin diye PartyRole'den tekrar okunup payload'a eklenir.
     */
    private void publishIndividualUpdatedEvent(Long partyRoleId, Individual individual) {
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
