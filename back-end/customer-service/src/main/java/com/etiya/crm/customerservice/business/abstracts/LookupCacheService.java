package com.etiya.crm.customerservice.business.abstracts;

public interface LookupCacheService {

	/** groupCode + valueId (ornn. lookup satirinin ID'si) icin gosterim metnini doner. */
	String resolveValue(String groupCode, Long valueId);

	/** groupCode + code (ornn. "ACTIVE") icin lookup ID'sini doner. */
	Long resolveId(String groupCode, String code);
}
