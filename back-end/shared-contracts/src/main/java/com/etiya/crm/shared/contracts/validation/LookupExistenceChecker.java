package com.etiya.crm.shared.contracts.validation;

/**
 * Client'tan gelen ham bir lookup-service id'sinin (cityId, genderId gibi) gercekten
 * var/aktif/dogru grupta olup olmadigini dogrulayan servislerin ortak sozlesmesi.
 * Her servis kendi LookupCacheService'inde (genelde caching ile) uygular -
 * {@link ExistsInLookupGroupValidator} her servisin kendi Spring context'indeki tek
 * implementasyonu enjekte eder.
 */
public interface LookupExistenceChecker {

	boolean existsInGroup(Long id, String entCodeName);
}
