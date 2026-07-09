package com.etiya.crm.customerservice.config;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.etiya.crm.customerservice.constants.CacheNames;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * Iki katmanli cache: Caffeine (local) sik degismeyen kucuk lookup verileri
 * icin, Redis (distributed) instance'lar arasi paylasilan musteri verisi
 * icin. Hangi @Cacheable'in hangi manager'i kullanacagi cacheManager
 * attribute'u ile acikca belirtilir (bkz. CacheNames.*_CACHE_MANAGER).
 */
@Configuration
@EnableCaching
public class CacheConfig {

	@Bean(CacheNames.CAFFEINE_CACHE_MANAGER)
	public CacheManager caffeineCacheManager() {
		CaffeineCacheManager manager = new CaffeineCacheManager(CacheNames.LOOKUPS);
		manager.setCaffeine(Caffeine.newBuilder()
				.expireAfterWrite(Duration.ofMinutes(30))
				.maximumSize(1000));
		return manager;
	}
	@Bean(CacheNames.REDIS_CACHE_MANAGER)
	@Primary
	public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
		RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofMinutes(10))
				.serializeValuesWith(RedisSerializationContext.SerializationPair
						.fromSerializer(new GenericJackson2JsonRedisSerializer()));

		return RedisCacheManager.builder(connectionFactory)
				.cacheDefaults(configuration)
				.withCacheConfiguration(CacheNames.CUSTOMERS, configuration)
				.build();
	}
}
