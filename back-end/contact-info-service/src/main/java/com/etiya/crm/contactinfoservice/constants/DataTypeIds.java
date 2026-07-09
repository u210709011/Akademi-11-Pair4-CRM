package com.etiya.crm.contactinfoservice.constants;

/**
 * BaseEntity.dataTypeId, adres/contact medium kaydinin hangi entity'ye
 * (musteri, parti, vb.) ait oldugunu tasir. lookup-service hazir olana kadar
 * bu Long deger dogrudan kullanilir; lookup-service devreye girince
 * LookupGroups uzerinden cozülmelidir.
 */
public final class DataTypeIds {

    public static final Long CUSTOMER = 1L;

    private DataTypeIds() {
    }
}
