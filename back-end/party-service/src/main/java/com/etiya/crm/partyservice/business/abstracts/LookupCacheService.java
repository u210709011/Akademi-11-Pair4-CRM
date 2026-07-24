package com.etiya.crm.partyservice.business.abstracts;

public interface LookupCacheService {

    /** entCodeName = GNL_TP grubu (orn. PARTY_TYPE), shrtCode = grup icindeki deger kodu (orn. INDIVIDUAL). */
    Long resolveIdByCode(String entCodeName, String shrtCode);
}