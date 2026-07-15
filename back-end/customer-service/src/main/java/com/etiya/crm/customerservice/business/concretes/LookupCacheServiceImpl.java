package com.etiya.crm.customerservice.business.concretes;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.etiya.crm.customerservice.business.abstracts.LookupCacheService;
import com.etiya.crm.customerservice.clients.controllers.LookupClient;
import com.etiya.crm.customerservice.constants.CacheNames;

import lombok.RequiredArgsConstructor;

/** lookup-service her istekte cagrilmaz; Caffeine (local) ile cache'lenir. */
@Service
@RequiredArgsConstructor
public class LookupCacheServiceImpl implements LookupCacheService {

	private final LookupClient lookupClient;

	@Override
	@Cacheable(cacheManager = CacheNames.CAFFEINE_CACHE_MANAGER, cacheNames = CacheNames.TP_LOOKUPS,
			key = "#ent_code_name + '_id_' + #tp_id")
	public String resolveTpValue(String ent_code_name, Long tp_id) {
		return lookupClient.getTpById(ent_code_name, tp_id).shrt_code();
	}

	@Override
	@Cacheable(cacheManager = CacheNames.CAFFEINE_CACHE_MANAGER, cacheNames = CacheNames.TP_LOOKUPS,
			key = "#ent_code_name + '_code_' + #shrt_code")
	public Long resolveTpId(String ent_code_name, String shrt_code) {
		return lookupClient.getTpByCode(ent_code_name, shrt_code).tp_id();
	}

	@Override
	@Cacheable(cacheManager = CacheNames.CAFFEINE_CACHE_MANAGER, cacheNames = CacheNames.ST_LOOKUPS,
			key = "#ent_code_name + '_id_' + #st_id")
	public String resolveStValue(String ent_code_name, Long st_id) {
		return lookupClient.getStById(ent_code_name, st_id).shrt_code();
	}

	@Override
	@Cacheable(cacheManager = CacheNames.CAFFEINE_CACHE_MANAGER, cacheNames = CacheNames.ST_LOOKUPS,
			key = "#ent_code_name + '_code_' + #shrt_code")
	public Long resolveStId(String ent_code_name, String shrt_code) {
		return lookupClient.getStByCode(ent_code_name, shrt_code).st_id();
	}
}
