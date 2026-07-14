package com.etiya.crm.partyservice.business.concretes;

import com.etiya.crm.partyservice.business.abstracts.PartyService;
import com.etiya.crm.partyservice.business.exceptions.BusinessException;
import com.etiya.crm.partyservice.dataAccess.abstracts.IndividualRepository;
import com.etiya.crm.partyservice.dataAccess.abstracts.PartyRepository;
import com.etiya.crm.partyservice.entities.concretes.Party;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartyManager implements PartyService {

    private final PartyRepository partyRepository;
    private final IndividualRepository individualRepository;

    @Override
    @Transactional
    public void softDeleteParty(Long partyId) {
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new BusinessException("Party bulunamadi: " + partyId));

        party.setActive(false);
        party.getRoles().forEach(role -> role.setActive(false));
        individualRepository.findByParty_PartyId(partyId)
                .ifPresent(individual -> individual.setActive(false));
    }
}
