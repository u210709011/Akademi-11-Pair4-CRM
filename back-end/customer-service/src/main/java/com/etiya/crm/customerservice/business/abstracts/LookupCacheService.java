package com.etiya.crm.customerservice.business.abstracts;

public interface LookupCacheService {

	/** ent_code_name + tp_id (ornn. gnl_tp satirinin ID'si) icin gosterim metnini doner. */
	String resolveTpValue(String ent_code_name, Long tp_id);

	/** ent_code_name + shrt_code (ornn. "ACTIVE") icin lookup ID'sini doner. */
	Long resolveTpId(String ent_code_name, String shrt_code);

	/** ent_code_name + st_id (ornn. gnl_st satirinin ID'si) icin gosterim metnini doner. */
	String resolveStValue(String ent_code_name, Long st_id);

	/** ent_code_name + shrt_code (ornn. "ACTIVE") icin lookup ID'sini doner. */
	Long resolveStId(String ent_code_name, String shrt_code);




}
