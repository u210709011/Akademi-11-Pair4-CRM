package com.etiya.crm.customerservice.business.abstracts;

/**
 * lookup-service'in yeni semasina (general-types/general-statuses/type-values)
 * gore dort acik metot. Tek bir resolveId(group, code) facade'i artik uygun
 * degil: uc farkli kavram uc farkli uca/semantige gidiyor - cagiran hangi
 * turu istedigini zaten biliyor.
 */
public interface LookupCacheService {

	/** general-types/resolve/{entCodeName}/{shrtCode} - GNL_TP id'sini doner. */
	Long resolveTypeId(String entCodeName, String shrtCode);

	/** general-statuses/resolve/{entCodeName}/{shrtCode} - GNL_ST id'sini doner. */
	Long resolveStatusId(String entCodeName, String shrtCode);

	/** type-values icinde tableName'e karsilik gelen fieldName'i (polimorfik tip etiketi) doner. */
	Long resolveDataTypeId(String tableName);

	/** general-types/{id} - gosterim metnini (name) doner. */
	String resolveTypeValue(Long id);
}
