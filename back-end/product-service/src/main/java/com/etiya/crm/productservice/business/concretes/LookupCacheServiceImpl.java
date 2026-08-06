package com.etiya.crm.productservice.business.concretes;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.exceptions.LookupValueNotFoundException;
import com.etiya.crm.productservice.clients.LookupClient;
import com.etiya.crm.shared.contracts.gnltp.GnlTpResponse;
import com.etiya.crm.shared.contracts.gnlst.GnlStResponse;

@Service
public class LookupCacheServiceImpl implements LookupCacheService {

    private final LookupClient lookupClient;

    public LookupCacheServiceImpl(LookupClient lookupClient) {
        this.lookupClient = lookupClient;
    }

    @Override
    @Cacheable(value = "lookups", key = "'TP:' + #entCodeName + ':' + #shrtCode")
    public Long resolveTypeIdByCode(String entCodeName, String shrtCode) {
        GnlTpResponse response = lookupClient.resolveType(entCodeName, shrtCode);
        if (response == null || response.gnlTpId() == null) {
            throw new LookupValueNotFoundException(entCodeName, shrtCode);
        }
        return response.gnlTpId();
    }

    @Override
    @Cacheable(value = "lookups", key = "'ST:' + #entCodeName + ':' + #shrtCode")
    public Long resolveStatusIdByCode(String entCodeName, String shrtCode) {
        GnlStResponse response = lookupClient.resolveStatus(entCodeName, shrtCode);
        if (response == null || response.gnlStId() == null) {
            throw new LookupValueNotFoundException(entCodeName, shrtCode);
        }
        return response.gnlStId();
    }
}