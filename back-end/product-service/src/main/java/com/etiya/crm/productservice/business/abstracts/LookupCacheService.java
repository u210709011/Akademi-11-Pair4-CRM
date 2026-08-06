package com.etiya.crm.productservice.business.abstracts;

public interface LookupCacheService {

    /** GNL_TP'den koda karşılık gelen id'yi döner. */
    Long resolveTypeIdByCode(String entCodeName, String shrtCode);

    /** GNL_ST'den koda karşılık gelen id'yi döner. */
    Long resolveStatusIdByCode(String entCodeName, String shrtCode);
}