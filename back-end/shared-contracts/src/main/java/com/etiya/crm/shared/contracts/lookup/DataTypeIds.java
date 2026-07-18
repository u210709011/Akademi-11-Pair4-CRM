package com.etiya.crm.shared.contracts.lookup;

/**
 * BaseEntity.dataTypeId, adres/contact medium kaydinin hangi entity'ye
 * (musteri, parti, vb.) ait oldugunu tasir. lookup-service'teki DATA_TYPE
 * grubunun sabit deger ID'leri (bkz. lookup-service V2__seed_lookup.sql):
 * PARTY=101, CUST=102. Bu deger ID'leri degismez bir kontrat, dogrudan
 * kullanilir - LookupCodes.DATA_TYPE_CUSTOMER("CUST") string kodunun
 * lookup-service'ten dinamik cozumlenmis hali ile AYNI sayiyi temsil eder;
 * contact-info-service bunu lookup-service'e sormadan dogrudan kullanir.
 */
public final class DataTypeIds {

	public static final Long PARTY = 101L;
	public static final Long CUSTOMER = 102L;

	private DataTypeIds() {
	}
}
