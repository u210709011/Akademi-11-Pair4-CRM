package com.etiya.crm.partyservice.business.abstracts;

public interface PartyRoleService {

    /**
     * CustomerDeleted event'i ile tek bir PARTY_ROLE'u soft delete eder
     * (ST_ID -> PASSIVE). Party/Individual'a dokunmaz - musterinin baska
     * rolleri olabilir.
     */
    void deactivatePartyRole(Long partyRoleId);
}
