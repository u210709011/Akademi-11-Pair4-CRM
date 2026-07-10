package com.etiya.crm.partyservice.business.concretes;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.etiya.crm.partyservice.business.abstracts.LookupCacheService;
import com.etiya.crm.partyservice.business.exceptions.BusinessException;
import com.etiya.crm.partyservice.clients.LookupClient;
import com.etiya.crm.partyservice.clients.LookupValueResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LookupCacheServiceImpl implements LookupCacheService {

    private final LookupClient lookupClient;

    @Override
    @Cacheable(value = "lookups", key = "#groupCode + ':' + #code")
    public Long resolveIdByCode(String groupCode, String code) {
        LookupValueResponse response = lookupClient.getByCode(groupCode, code);
        if (response == null || response.id() == null) {
            throw new BusinessException("Lookup degeri bulunamadi: " + groupCode + "/" + code);
        }
        return response.id();
    }
}