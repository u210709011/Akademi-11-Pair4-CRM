package com.etiya.crm.customerservice.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CacheConfigTest {

	@Mock
	private RedisConnectionFactory redisConnectionFactory;

	@Mock
	private Cache cache;

	private final CacheConfig cacheConfig = new CacheConfig();

	@Test
	void caffeineCacheManager_registersLookupsCache() {
		CacheManager manager = cacheConfig.caffeineCacheManager();

		assertThat(manager.getCache("lookups")).isNotNull();
	}

	@Test
	void redisCacheManager_buildsWithoutConnecting() {
		CacheManager manager = cacheConfig.redisCacheManager(redisConnectionFactory);

		assertThat(manager).isNotNull();
	}

	@Test
	void errorHandler_swallowsGetFailure_insteadOfPropagating() {
		when(cache.getName()).thenReturn("lookups");
		CacheErrorHandler handler = cacheConfig.errorHandler();

		assertThatCode(() -> handler.handleCacheGetError(new RuntimeException("redis down"), cache, "key"))
				.doesNotThrowAnyException();
	}

	@Test
	void errorHandler_swallowsPutFailure_insteadOfPropagating() {
		when(cache.getName()).thenReturn("lookups");
		CacheErrorHandler handler = cacheConfig.errorHandler();

		assertThatCode(() -> handler.handleCachePutError(new RuntimeException("redis down"), cache, "key", "value"))
				.doesNotThrowAnyException();
	}

	@Test
	void errorHandler_swallowsEvictFailure_insteadOfPropagating() {
		when(cache.getName()).thenReturn("lookups");
		CacheErrorHandler handler = cacheConfig.errorHandler();

		assertThatCode(() -> handler.handleCacheEvictError(new RuntimeException("redis down"), cache, "key"))
				.doesNotThrowAnyException();
	}

	@Test
	void errorHandler_swallowsClearFailure_insteadOfPropagating() {
		when(cache.getName()).thenReturn("lookups");
		CacheErrorHandler handler = cacheConfig.errorHandler();

		assertThatCode(() -> handler.handleCacheClearError(new RuntimeException("redis down"), cache))
				.doesNotThrowAnyException();
	}

}
