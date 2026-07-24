package com.etiya.crm.partyservice.business.concretes;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.etiya.crm.partyservice.business.abstracts.LookupCacheService;
import com.etiya.crm.partyservice.business.exceptions.BusinessException;
import com.etiya.crm.partyservice.clients.LookupClient;
import com.etiya.crm.shared.contracts.gnltp.GnlTpResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LookupCacheServiceImpl implements LookupCacheService {

    private final LookupClient lookupClient;

    @Override
    @Cacheable(value = "lookups", key = "#entCodeName + ':' + #shrtCode")
    public Long resolveIdByCode(String entCodeName, String shrtCode) {
        GnlTpResponse response = lookupClient.resolveType(entCodeName, shrtCode);
        if (response == null || response.gnlTpId() == null) {
            throw new BusinessException("Lookup degeri bulunamadi: " + entCodeName + "/" + shrtCode);
        }
        return response.gnlTpId();
    }
}