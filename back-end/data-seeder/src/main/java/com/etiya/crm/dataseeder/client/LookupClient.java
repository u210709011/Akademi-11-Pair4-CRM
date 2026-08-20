package com.etiya.crm.dataseeder.client;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * lookup-service degerlerini ID olarak hic hardcode etmeden coder/{entCodeName}/{shrtCode}
 * uzerinden coder (CLAUDE.md kurali). Ayni (entCodeName, shrtCode) tekrar sorulursa cache'lenir.
 */
@Component
public class LookupClient {

	private final GatewayClient gatewayClient;
	private final ConcurrentHashMap<String, Long> cache = new ConcurrentHashMap<>();

	public LookupClient(GatewayClient gatewayClient) {
		this.gatewayClient = gatewayClient;
	}

	public Long resolveGnlTpId(String entCodeName, String shrtCode) {
		return cache.computeIfAbsent(entCodeName + "/" + shrtCode, key -> {
			GnlTypeResponse response = gatewayClient.get(
					"/api/v1/general-types/resolve/" + entCodeName + "/" + shrtCode, GnlTypeResponse.class);
			if (response == null) {
				throw new IllegalStateException("Lookup deger bulunamadi: " + key);
			}
			return response.gnlTpId();
		});
	}
}

record GnlTypeResponse(Long gnlTpId, String name, String shrtCode, String entCodeName) {
}
