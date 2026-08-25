package com.etiya.crm.customerservice.business.concretes;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.etiya.crm.customerservice.clients.controllers.LookupClient;
import com.etiya.crm.customerservice.constants.CacheNames;
import com.etiya.crm.shared.contracts.gnltp.GnlTpResponse;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class LookupTypeByIdCache {

	private final LookupClient lookupClient;

	@Cacheable(cacheManager = CacheNames.CAFFEINE_CACHE_MANAGER, cacheNames = CacheNames.LOOKUPS,
			key = "'type_by_id_' + #id")
	public GnlTpResponse getTypeById(Long id) {
		return lookupClient.getTypeById(id);
	}
}
