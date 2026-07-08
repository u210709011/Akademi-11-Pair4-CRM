package com.etiya.crm.partyservice.business.concretes;

import com.etiya.crm.partyservice.business.abstracts.PartyRoleService;
import com.etiya.crm.partyservice.constants.StatusIds;
import com.etiya.crm.partyservice.dataAccess.abstracts.PartyRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartyRoleManager implements PartyRoleService {

    private final PartyRoleRepository partyRoleRepository;

    @Override
    @Transactional
    public void deactivatePartyRole(Long partyRoleId) {
        partyRoleRepository.findById(partyRoleId).ifPresentOrElse(
                role -> role.setStatusId(StatusIds.PASSIVE),
                () -> log.warn("CustomerDeleted event'i icin PartyRole bulunamadi: partyRoleId={}", partyRoleId));
    }
}
