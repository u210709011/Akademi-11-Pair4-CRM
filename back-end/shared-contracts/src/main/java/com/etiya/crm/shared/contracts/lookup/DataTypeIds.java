package com.etiya.crm.shared.contracts.lookup;

/**
 * BaseEntity.dataTypeId, adres/contact medium kaydinin hangi entity'ye
 * (musteri, parti, vb.) ait oldugunu tasir. lookup-service'teki eski DATA_TYPE
 * grubunun sabit deger ID'leri (bkz. lookup-service V2__seed_lookup.sql):
 * PARTY=101, CUST=102.
 *
 * @deprecated lookup-service artik DATA_TYPE grubunu kullanmiyor - ayni rolu simdi TYPE_VALUE
 * tablosu goruyor (table_name='PARTY'/'CUST' + field_name = polimorfik tip etiketi), ve oradaki
 * sayilar BUNLARDAN FARKLI (orn. PARTY=9, CUST=12, 101/102 degil) - yani bu sabitler zaten
 * lookup-service'in gercek verisiyle uyusmuyor. contact-info-service/customer-service TYPE_VALUE'yu
 * dinamik sorgulayacak sekilde guncellenince bu sinif tamamen kaldirilmali.
 */
@Deprecated(forRemoval = true)
public final class DataTypeIds {

	public static final Long PARTY = 101L;
	public static final Long CUSTOMER = 102L;

	private DataTypeIds() {
	}
}
