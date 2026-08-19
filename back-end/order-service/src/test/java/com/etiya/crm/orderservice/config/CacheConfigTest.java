package com.etiya.crm.orderservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.etiya.crm.orderservice.constants.CacheNames;

import static org.assertj.core.api.Assertions.assertThat;

class CacheConfigTest {

	@Test
	void cacheManager_registersLookupsCache() {
		CacheManager cacheManager = new CacheConfig().cacheManager();

		Cache cache = cacheManager.getCache(CacheNames.LOOKUPS);

		assertThat(cache).isNotNull();
		assertThat(cacheManager.getCacheNames()).containsExactly(CacheNames.LOOKUPS);
	}

	@Test
	void cacheManager_actuallyCachesAndEvictsValues() {
		CacheManager cacheManager = new CacheConfig().cacheManager();
		Cache cache = cacheManager.getCache(CacheNames.LOOKUPS);

		cache.put("key", "value");

		assertThat(cache.get("key").get()).isEqualTo("value");
	}
}
