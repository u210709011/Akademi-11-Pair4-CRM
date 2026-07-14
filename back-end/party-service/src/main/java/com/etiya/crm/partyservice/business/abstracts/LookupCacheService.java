package com.etiya.crm.partyservice.business.abstracts;

public interface LookupCacheService {

    Long resolveIdByCode(String groupCode, String code);
}