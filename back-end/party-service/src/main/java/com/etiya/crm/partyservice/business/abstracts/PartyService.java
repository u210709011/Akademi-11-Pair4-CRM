package com.etiya.crm.partyservice.business.abstracts;

public interface PartyService {

    /**
     * Onboarding saga compensation: PARTY + IND + o PARTY'ye bagli tum
     * PARTY_ROLE kayitlari soft delete edilir (ST_ID -> PASSIVE). Fiziksel
     * silme yapilmaz (AS-002).
     */
    void softDeleteParty(Long partyId);
}
