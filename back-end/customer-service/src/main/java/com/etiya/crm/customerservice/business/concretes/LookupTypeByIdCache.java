package com.etiya.crm.customerservice.business.concretes;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.etiya.crm.customerservice.clients.controllers.LookupClient;
import com.etiya.crm.customerservice.constants.CacheNames;
import com.etiya.crm.shared.contracts.gnltp.GnlTpResponse;

import lombok.RequiredArgsConstructor;

/**
 * general-types/{id} sonucunu cache'ler - LookupCacheServiceImpl.existsInGroup'tan AYRI bir
 * bean'de, cunku @Cacheable sadece PROXY uzerinden gelen (dis) cagrilari yakalar: existsInGroup
 * bu metodu kendi govdesinden (self-invocation) cagirsaydi Spring'in caching AOP'si devreye
 * girmezdi. Burada da KASITLI olarak try/catch YOK - @Cacheable yalnizca basariyla DONEN
 * sonuclari cache'ler, exception firlatirsa hicbir sey yazilmaz; boylece lookup-service'in gecici
 * bir hatasi (ör. henuz ayaga kalkmamisken ilk istek), o id gercekte var/aktif olsa bile 30 dakika
 * boyunca "yok" olarak cache'lenmis olmaz - existsInGroup'taki try/catch her seferinde taze bir
 * deneme yapar.
 */
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
