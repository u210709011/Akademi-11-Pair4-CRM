package com.etiya.crm.orderservice.business.concretes;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.etiya.crm.orderservice.business.abstracts.LookupCacheService;
import com.etiya.crm.orderservice.clients.controllers.LookupClient;
import com.etiya.crm.orderservice.constants.CacheNames;
import com.etiya.crm.shared.contracts.gnlchar.GnlCharResponse;
import com.etiya.crm.shared.contracts.gnlcharval.GnlCharValResponse;
import com.etiya.crm.shared.contracts.gnltp.GnlTpResponse;

import lombok.RequiredArgsConstructor;

/** lookup-service her istekte cagrilmaz; Caffeine (local) ile cache'lenir. */
@Service
@RequiredArgsConstructor
public class LookupCacheServiceImpl implements LookupCacheService {

	private final LookupClient lookupClient;

	@Override
	@Cacheable(value = CacheNames.LOOKUPS, key = "'status_' + #entCodeName + '_' + #shrtCode")
	public Long resolveStatusId(String entCodeName, String shrtCode) {
		return lookupClient.resolveGeneralStatus(entCodeName, shrtCode).gnlStId();
	}

	@Override
	@Cacheable(value = CacheNames.LOOKUPS, key = "'datatype_' + #tableName")
	public Long resolveDataTypeId(String tableName) {
		return lookupClient.getTypeValueByTable(tableName).fieldName();
	}

	@Override
	@Cacheable(value = CacheNames.LOOKUPS, key = "'char_' + #charId")
	public GnlCharResponse getCharacteristic(Long charId) {
		return lookupClient.getCharacteristicById(charId);
	}

	@Override
	@Cacheable(value = CacheNames.LOOKUPS, key = "'charval_' + #charValId")
	public GnlCharValResponse getCharacteristicValue(Long charValId) {
		return lookupClient.getCharacteristicValueById(charValId);
	}

	@Override
	@Cacheable(value = CacheNames.LOOKUPS, key = "'gnltp_' + #gnlTpId")
	public GnlTpResponse getGeneralType(Long gnlTpId) {
		return lookupClient.getGeneralTypeById(gnlTpId);
	}
}
