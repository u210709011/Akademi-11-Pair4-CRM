package com.etiya.crm.productservice.business.abstracts;

public interface LookupCacheService {

    /** GNL_TP'den koda karşılık gelen id'yi döner. */
    Long resolveTypeIdByCode(String entCodeName, String shrtCode);

    /** GNL_ST'den koda karşılık gelen id'yi döner. */
    Long resolveStatusIdByCode(String entCodeName, String shrtCode);

    Long validateResourceSpecId(Long resourceSpecId);
    Long validateServiceSpecId(Long serviceSpecId);
    Long validateCharacteristicId(Long characteristicId);
    Long validateCharacteristicValueId(Long characteristicValueId);

    String getCharacteristicName(Long characteristicId);
    String getCharacteristicValueName(Long characteristicValueId);
}