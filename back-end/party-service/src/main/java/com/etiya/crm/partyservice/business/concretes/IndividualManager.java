package com.etiya.crm.partyservice.business.concretes;

import com.etiya.crm.partyservice.business.abstracts.IndividualService;
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
import com.etiya.crm.shared.contracts.individual.CreateIndividualCommand;
import com.etiya.crm.shared.contracts.individual.IndividualResponse;
import com.etiya.crm.shared.contracts.individual.PartyRoleResponse;
import com.etiya.crm.shared.contracts.individual.UpdateIndividualCommand;
import com.etiya.crm.shared.contracts.gnltp.GnlTpCodes;
import com.etiya.crm.shared.contracts.gnltp.GnlTpGroups;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Party/Individual/PartyRole aggregate'lerinin orkestrasyonunu tutar. Event
 * insa etme/yayinlama sorumlulugu PartyEventPublisher'a devredilir - bu
 * sinifin degisme sebebi tek kalir: "bir individual nasil olusturulur/
 * guncellenir".
 */
@Service
@RequiredArgsConstructor
public class IndividualManager implements IndividualService {

    private final PartyRepository partyRepository;
    private final IndividualRepository individualRepository;
    private final PartyRoleRepository partyRoleRepository;
    private final IndividualMapper individualMapper;
    private final IndividualBusinessRules individualBusinessRules;
    private final LookupCacheService lookupCacheService;
    private final PartyEventPublisher partyEventPublisher;

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

        partyEventPublisher.publishIndividualPartyCreated(partyRole.getPartyRoleId(), command,
                partyRole.getPartyRoleTypeId());

        return new PartyRoleResponse(party.getPartyId(), partyRole.getPartyRoleId());
    }

    @Override
    public boolean existsByNationalId(String nationalId) {
        return individualRepository.existsByNationalIdAndActiveTrue(nationalId);
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

        partyEventPublisher.publishIndividualUpdated(partyRoleId, individual);

        return individualMapper.toResponse(individual);
    }

    private Individual findIndividualOrThrow(Long partyRoleId) {
        PartyRole partyRole = partyRoleRepository.findById(partyRoleId)
                .orElseThrow(() -> new PartyRoleNotFoundException(partyRoleId));

        return individualRepository.findByParty_PartyId(partyRole.getParty().getPartyId())
                .orElseThrow(() -> new IndividualNotFoundException(partyRoleId));
    }
}
