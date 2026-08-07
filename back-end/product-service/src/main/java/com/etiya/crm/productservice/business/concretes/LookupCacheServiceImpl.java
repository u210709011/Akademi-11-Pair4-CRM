package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.exceptions.*;
import com.etiya.crm.shared.contracts.gnlchar.GnlCharResponse;
import com.etiya.crm.shared.contracts.gnlcharval.GnlCharValResponse;
import com.etiya.crm.shared.contracts.rsrcspec.RsrcSpecResponse;
import com.etiya.crm.shared.contracts.srvcspec.SrvcSpecResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
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

    @Override
    public Long validateResourceSpecId(Long resourceSpecId) {
        RsrcSpecResponse response = lookupClient.getResourceSpecById(resourceSpecId);
        if (response == null || response.rsrcSpecId() == null) {
            throw new ResourceSpecNotFoundException(resourceSpecId);
        }
        return response.rsrcSpecId();
    }

    @Override
    public Long validateServiceSpecId(Long serviceSpecId) {
        SrvcSpecResponse response = lookupClient.getServiceSpecById(serviceSpecId);
        if (response == null || response.srvcSpecId() == null) {
            throw new ServiceSpecNotFoundException(serviceSpecId);
        }
        return response.srvcSpecId();
    }

    @Override
    public Long validateCharacteristicId(Long characteristicId) {
        GnlCharResponse response = lookupClient.getCharacteristicById(characteristicId);
        if (response == null || response.charId() == null) {
            throw new CharacteristicNotFoundException(characteristicId);
        }
        return response.charId();
    }

    @Override
    public Long validateCharacteristicValueId(Long characteristicValueId) {
        GnlCharValResponse response = lookupClient.getCharacteristicValueById(characteristicValueId);
        if (response == null || response.charValId() == null) {
            throw new CharacteristicValueNotFoundException(characteristicValueId);
        }
        return response.charValId();
    }

    @Override
    @Cacheable(value = "lookups", key = "'CHARNAME:' + #characteristicId")
    public String getCharacteristicName(Long characteristicId) {
        GnlCharResponse response = lookupClient.getCharacteristicById(characteristicId);
        if (response == null) {
            throw new CharacteristicNotFoundException(characteristicId);
        }
        return response.name();
    }

    @Override
    @Cacheable(value = "lookups", key = "'CHARVALNAME:' + #characteristicValueId")
    public String getCharacteristicValueName(Long characteristicValueId) {
        GnlCharValResponse response = lookupClient.getCharacteristicValueById(characteristicValueId);
        if (response == null) {
            throw new CharacteristicValueNotFoundException(characteristicValueId);
        }
        return response.val();
    }
}