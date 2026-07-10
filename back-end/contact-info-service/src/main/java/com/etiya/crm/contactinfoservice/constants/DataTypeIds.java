package com.etiya.crm.contactinfoservice.constants;

/**
 * BaseEntity.dataTypeId, adres/contact medium kaydinin hangi entity'ye
 * (musteri, parti, vb.) ait oldugunu tasir. lookup-service'teki DATA_TYPE
 * grubunun sabit deger ID'leri (bkz. lookup-service V2__seed_lookup.sql):
 * PARTY=101, CUST=102. Bu deger ID'leri degismez bir kontrat, dogrudan
 * kullanilir.
 */
public final class DataTypeIds {

    public static final Long CUSTOMER = 102L;

    private DataTypeIds() {
    }
}
