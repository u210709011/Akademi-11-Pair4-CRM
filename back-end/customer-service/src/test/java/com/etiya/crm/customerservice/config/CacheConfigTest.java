package com.etiya.crm.customerservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * CacheErrorHandler'in tek sorumlulugu: Redis/cache arizasini API cagrisini
 * kirmadan yutmak. Buradaki tek anlamli assertion, hicbir handle* metodunun
 * exception'i yeniden firlatmamasi.
 */
class CacheConfigTest {

	private final CacheErrorHandler errorHandler = new CacheConfig().errorHandler();

	@Test
	void handleCacheGetError_doesNotRethrow() {
		Cache cache = mock(Cache.class);
		when(cache.getName()).thenReturn("customers");

		assertThatCode(() -> errorHandler.handleCacheGetError(new RuntimeException("redis down"), cache, "key"))
				.doesNotThrowAnyException();
	}

	@Test
	void handleCachePutError_doesNotRethrow() {
		Cache cache = mock(Cache.class);
		when(cache.getName()).thenReturn("customers");

		assertThatCode(
				() -> errorHandler.handleCachePutError(new RuntimeException("redis down"), cache, "key", "value"))
				.doesNotThrowAnyException();
	}

	@Test
	void handleCacheEvictError_doesNotRethrow() {
		Cache cache = mock(Cache.class);
		when(cache.getName()).thenReturn("customers");

		assertThatCode(() -> errorHandler.handleCacheEvictError(new RuntimeException("redis down"), cache, "key"))
				.doesNotThrowAnyException();
	}

	@Test
	void handleCacheClearError_doesNotRethrow() {
		Cache cache = mock(Cache.class);
		when(cache.getName()).thenReturn("customers");

		assertThatCode(() -> errorHandler.handleCacheClearError(new RuntimeException("redis down"), cache))
				.doesNotThrowAnyException();
	}
}
