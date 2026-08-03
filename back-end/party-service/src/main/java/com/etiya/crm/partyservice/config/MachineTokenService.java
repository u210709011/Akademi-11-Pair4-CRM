package com.etiya.crm.partyservice.config;

import java.time.Instant;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;

/**
 * CustomerEventListener gibi Kafka consumer'lar bir HTTP istegi icinde CALISMAZ -
 * FeignConfig'in JWT propagation'i yapacak bir SecurityContext'i yoktur. Ayrica
 * HTTP-tetiklemeli akislarda bile Resilience4j circuit breaker/cache sarmalayicilari
 * cagriyi farkli bir thread'de calistirabilir, bu durumda SecurityContextHolder
 * (thread-local) bos gelir. Bu servis, o durumlarda lookup-service'e erismek icin
 * "party-service-m2m" client'inin (serviceAccountsEnabled=true, bkz.
 * infra/keycloak/crm-realm.json) client_credentials grant'iyla kendi
 * service-account token'ini alir ve suresi dolana kadar bellekte cache'ler
 * (her cagri icin Keycloak'a gitmemek icin). Bu client, interaktif login'de
 * kullanilan crm-client'tan KASITLI OLARAK ayridir - bir client'in secret'i
 * sizarsa diger client'in yetkisini acmasin diye (blast radius ayrimi).
 */
@Service
@RequiredArgsConstructor
public class MachineTokenService {

	private static final String TOKEN_PATH = "/protocol/openid-connect/token";

	/** Token gercekte bu kadar erken bitmeden yenilenir - agdaki gecikme + clock skew payi. */
	private static final long EXPIRY_SAFETY_MARGIN_SECONDS = 30;

	private final RestClient keycloakRestClient;
	private final KeycloakProperties keycloakProperties;

	private volatile CachedToken cachedToken;

	public synchronized String getAccessToken() {
		if (cachedToken == null || cachedToken.isExpiringSoon()) {
			cachedToken = fetchToken();
		}
		return cachedToken.accessToken();
	}

	private CachedToken fetchToken() {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("grant_type", "client_credentials");
		form.add("client_id", keycloakProperties.getClientId());
		form.add("client_secret", keycloakProperties.getClientSecret());

		KeycloakTokenResponse response = keycloakRestClient.post()
				.uri(TOKEN_PATH)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(form)
				.retrieve()
				.body(KeycloakTokenResponse.class);

		Instant expiresAt = Instant.now().plusSeconds(response.expiresIn() - EXPIRY_SAFETY_MARGIN_SECONDS);
		return new CachedToken(response.accessToken(), expiresAt);
	}

	private record CachedToken(String accessToken, Instant expiresAt) {
		boolean isExpiringSoon() {
			return Instant.now().isAfter(expiresAt);
		}
	}
}
