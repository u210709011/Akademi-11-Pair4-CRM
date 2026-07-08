package com.etiya.crm.partyservice.business.rules;

import com.etiya.crm.partyservice.business.exceptions.BusinessException;
import com.etiya.crm.partyservice.dataAccess.abstracts.IndividualRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IndividualBusinessRules {

    private final IndividualRepository individualRepository;

    public void checkNationalIdNotDuplicate(String nationalId) {
        if (individualRepository.existsByNationalId(nationalId)) {
            throw new BusinessException("Bu nationalId ile kayitli bir birey zaten mevcut: " + nationalId);
        }
    }
}
