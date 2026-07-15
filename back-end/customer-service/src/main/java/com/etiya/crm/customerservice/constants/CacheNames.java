package com.etiya.crm.customerservice.constants;

public final class CacheNames {

	/** Caffeine (local, in-memory): sik degismeyen kucuk lookup verileri. */
	public static final String TP_LOOKUPS = "lookups";
	public static final String ST_LOOKUPS = "st_lookups";

	/** Redis (distributed): instance'lar arasi paylasilan musteri/arama verisi. */
	public static final String CUSTOMERS = "customers";
	public static final String CUSTOMER_SEARCH = "customerSearch";

	/** config/CacheConfig'teki CacheManager bean isimleri. */
	public static final String CAFFEINE_CACHE_MANAGER = "caffeineCacheManager";
	public static final String REDIS_CACHE_MANAGER = "redisCacheManager";

	private CacheNames() {
	}
}
