package com.etiya.crm.customerservice.config;

import java.time.Duration;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.etiya.crm.customerservice.constants.CacheNames;
import com.etiya.crm.customerservice.constants.LogMessages;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.extern.slf4j.Slf4j;

/**
 * Iki katmanli cache: Caffeine (local) sik degismeyen kucuk lookup verileri
 * icin, Redis (distributed) instance'lar arasi paylasilan musteri verisi
 * icin. Hangi @Cacheable'in hangi manager'i kullanacagi cacheManager
 * attribute'u ile acikca belirtilir (bkz. CacheNames.*_CACHE_MANAGER).
 */
@Slf4j
@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

	// LookupCacheServiceImpl'in lookup-service sonuclarini local'de tuttugu cache. CAFFEINE
	@Bean(CacheNames.CAFFEINE_CACHE_MANAGER)
	public CacheManager caffeineCacheManager() {
		CaffeineCacheManager manager = new CaffeineCacheManager(CacheNames.LOOKUPS);
		manager.setCaffeine(Caffeine.newBuilder()
				.expireAfterWrite(Duration.ofMinutes(30))
				.maximumSize(1000));
		return manager;
	}


	// Kullanılan asıl cache manager. REDIS
	@Bean(CacheNames.REDIS_CACHE_MANAGER)
	@Primary
	public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
		RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofMinutes(10))
				.disableCachingNullValues()
				.serializeValuesWith(RedisSerializationContext.SerializationPair
						.fromSerializer(new GenericJackson2JsonRedisSerializer()));

		return RedisCacheManager.builder(connectionFactory)
				.cacheDefaults(configuration)
				.build();
	}

	/**
	 * Redis erisilemez oldugunda (baglanti hatasi, timeout) cache bir "best-effort
	 * hizlandirma" katmani olmaktan cikip API'yi asagi cekmesin diye - varsayilan
	 * SimpleCacheErrorHandler hatayi oldugu gibi caller'a firlatir, bu ise sadece
	 * loglayip yutar; @Cacheable/@CacheEvict metodu DB'ye dusmus gibi calismaya devam eder.
	 */
	@Override
	public CacheErrorHandler errorHandler() {
		return new CacheErrorHandler() {

			@Override
			public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
				log.warn(LogMessages.CACHE_GET_FAILED, cache.getName(), key, exception.getMessage());
			}

			@Override
			public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
				log.warn(LogMessages.CACHE_PUT_FAILED, cache.getName(), key, exception.getMessage());
			}

			@Override
			public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
				log.warn(LogMessages.CACHE_EVICT_FAILED, cache.getName(), key, exception.getMessage());
			}

			@Override
			public void handleCacheClearError(RuntimeException exception, Cache cache) {
				log.warn(LogMessages.CACHE_CLEAR_FAILED, cache.getName(), exception.getMessage());
			}
		};
	}
}
