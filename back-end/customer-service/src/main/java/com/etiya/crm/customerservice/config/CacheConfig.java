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

/** Lookup için local, müşteri verileri için dağıtık cache yapılandırır. */
@Slf4j
@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

	// Genelde lookup sonuçları için kullanılır. Instance içinde tutulur.
	@Bean(CacheNames.CAFFEINE_CACHE_MANAGER)
	public CacheManager caffeineCacheManager() {
		CaffeineCacheManager manager = new CaffeineCacheManager(CacheNames.LOOKUPS);
		manager.setCaffeine(Caffeine.newBuilder()
				.expireAfterWrite(Duration.ofMinutes(30))
				.maximumSize(1000));
		return manager;
	}


	// Sık değişen verisi olan servislerin verileri instance'lar arasında paylaşılır.
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

	/** Cache arızası API çağrısını durdurmasın diye hatayı loglar. */
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
