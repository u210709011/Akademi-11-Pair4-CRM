package com.etiya.crm.customerservice.business.concretes;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.etiya.crm.customerservice.business.abstracts.LookupCacheService;
import com.etiya.crm.customerservice.clients.controllers.LookupClient;
import com.etiya.crm.customerservice.constants.CacheNames;
import com.etiya.crm.shared.contracts.typevalue.TypeValueResponse;

import lombok.RequiredArgsConstructor;

/** lookup-service her istekte cagrilmaz; Caffeine (local) ile cache'lenir. */
@Service
@RequiredArgsConstructor
public class LookupCacheServiceImpl implements LookupCacheService {

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

	/**
	 * type-values'ta /resolve/{tableName} ucu yok - sadece getAll()/getById(id) var, ve id
	 * type_value_id (PK) demek, ihtiyac duyulan fieldName degil. Bu yuzden her cagride tum
	 * satirlar cekilip tableName'e gore bellekte filtrelenir; tableName basina en fazla 4
	 * farkli deger olacagindan (PARTY/CUST/CUST_ACCT/PROD) sonucun kendisi cache'lenerek
	 * ayni tableName icin tekrar HTTP cagrisi yapilmasi onlenir.
	 */
	@Override
	@Cacheable(cacheManager = CacheNames.CAFFEINE_CACHE_MANAGER, cacheNames = CacheNames.LOOKUPS,
			key = "'datatype_' + #tableName")
	public Long resolveDataTypeId(String tableName) {
		return lookupClient.getAllTypeValues().stream()
				.filter(typeValue -> tableName.equals(typeValue.tableName()))
				.findFirst()
				.map(TypeValueResponse::fieldName)
				.orElseThrow(() -> new IllegalStateException(
						"No type_value row found for tableName=" + tableName));
	}

	@Override
	@Cacheable(cacheManager = CacheNames.CAFFEINE_CACHE_MANAGER, cacheNames = CacheNames.LOOKUPS,
			key = "'value_' + #id")
	public String resolveTypeValue(Long id) {
		return lookupClient.getTypeById(id).name();
	}
}
