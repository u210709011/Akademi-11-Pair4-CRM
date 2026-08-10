package com.etiya.crm.customerservice.business.concretes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.etiya.crm.customerservice.business.abstracts.LookupCacheService;
import com.etiya.crm.customerservice.clients.controllers.LookupClient;
import com.etiya.crm.customerservice.constants.CacheNames;
import com.etiya.crm.customerservice.constants.LogMessages;

import lombok.RequiredArgsConstructor;

/** lookup-service her istekte cagrilmaz; Caffeine (local) ile cache'lenir. */
@Service
@RequiredArgsConstructor
public class LookupCacheServiceImpl implements LookupCacheService {

	private static final Logger log = LoggerFactory.getLogger(LookupCacheServiceImpl.class);

	private final LookupClient lookupClient;

	@Override
	@Cacheable(cacheManager = CacheNames.CAFFEINE_CACHE_MANAGER, cacheNames = CacheNames.LOOKUPS,
			key = "'type_' + #entCodeName + '_' + #shrtCode")
	public Long resolveTypeId(String entCodeName, String shrtCode) {
		return lookupClient.resolveType(entCodeName, shrtCode).gnlTpId();
	}

	@Override
	@Cacheable(cacheManager = CacheNames.CAFFEINE_CACHE_MANAGER, cacheNames = CacheNames.LOOKUPS,
			key = "'status_' + #entCodeName + '_' + #shrtCode")
	public Long resolveStatusId(String entCodeName, String shrtCode) {
		return lookupClient.resolveStatus(entCodeName, shrtCode).gnlStId();
	}

	@Override
	@Cacheable(cacheManager = CacheNames.CAFFEINE_CACHE_MANAGER, cacheNames = CacheNames.LOOKUPS,
			key = "'datatype_' + #tableName")
	public Long resolveDataTypeId(String tableName) {
		return lookupClient.getTypeValueByTableName(tableName).fieldName();
	}

	@Override
	@Cacheable(cacheManager = CacheNames.CAFFEINE_CACHE_MANAGER, cacheNames = CacheNames.LOOKUPS,
			key = "'value_' + #id")
	public String resolveTypeValue(Long id) {
		return lookupClient.getTypeById(id).name();
	}

	@Override
	@Cacheable(cacheManager = CacheNames.CAFFEINE_CACHE_MANAGER, cacheNames = CacheNames.LOOKUPS,
			key = "'exists_' + #entCodeName + '_' + #id")
	public boolean existsInGroup(Long id, String entCodeName) {
		if (id == null) {
			return false;
		}
		try {
			var type = lookupClient.getTypeById(id);
			return type.active() && entCodeName.equals(type.entCodeName());
		} catch (RuntimeException ex) {
			// id yok (404) ya da downstream baska bir sekilde basarisiz oldu (feign.circuitbreaker.enabled=true
			// oldugunda ham FeignException degil NoFallbackAvailableException gelir, bkz.
			// AbstractDownstreamExceptionHandler'daki B-03 notu) - hangisi olursa olsun cityId
			// dogrulanamadi demektir, "gecersiz" sayilir. Yine de "yok" (404) ile "lookup-service
			// erisilemez" birbirinden ayirt edilebilsin diye logluyoruz.
			log.warn(LogMessages.LOOKUP_EXISTS_IN_GROUP_FAILED, id, entCodeName, ex.toString());
			return false;
		}
	}
}
