package com.etiya.crm.orderservice.business.concretes;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.etiya.crm.orderservice.business.abstracts.LookupCacheService;
import com.etiya.crm.orderservice.clients.controllers.LookupClient;

import lombok.RequiredArgsConstructor;

/** lookup-service her istekte cagrilmaz; Caffeine (local) ile cache'lenir. */
@Service
@RequiredArgsConstructor
public class LookupCacheServiceImpl implements LookupCacheService {

	private final LookupClient lookupClient;

	@Override
	@Cacheable(value = "lookups", key = "'status_' + #entCodeName + '_' + #shrtCode")
	public Long resolveStatusId(String entCodeName, String shrtCode) {
		return lookupClient.resolveGeneralStatus(entCodeName, shrtCode).gnlStId();
	}

	@Override
	@Cacheable(value = "lookups", key = "'datatype_' + #tableName")
	public Long resolveDataTypeId(String tableName) {
		return lookupClient.getTypeValueByTable(tableName).fieldName();
	}
}
