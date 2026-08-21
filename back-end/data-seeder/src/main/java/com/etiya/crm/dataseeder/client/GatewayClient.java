package com.etiya.crm.dataseeder.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.etiya.crm.dataseeder.SeederProperties;
import com.etiya.crm.dataseeder.auth.TokenProvider;

/**
 * api-gateway uzerinden tum servislere giden tek HTTP istemcisi - Angular front-end'in yaptigi
 * gibi, her cagriya salesperson'un JWT'sini Authorization header'i olarak ekler. Token bir kere
 * alinir ve calisma boyunca tekrar kullanilir (seed calismasi Keycloak access-token omrunden
 * kisa surer).
 */
@Component
public class GatewayClient {

	private final RestClient restClient;
	private final String bearerToken;

	public GatewayClient(SeederProperties props, TokenProvider tokenProvider) {
		this.bearerToken = tokenProvider.fetchAccessToken();
		this.restClient = RestClient.builder()
				.baseUrl(props.gatewayBaseUrl())
				.defaultHeader("Authorization", "Bearer " + bearerToken)
				.build();
	}

	public <T> T get(String path, Class<T> responseType) {
		return restClient.get().uri(path).retrieve().body(responseType);
	}

	public <T> T get(String path, ParameterizedTypeReference<T> responseType) {
		return restClient.get().uri(path).retrieve().body(responseType);
	}

	public <T> T post(String path, Object body, Class<T> responseType) {
		return restClient.post().uri(path)
				.contentType(MediaType.APPLICATION_JSON)
				.body(body)
				.retrieve()
				.body(responseType);
	}

	public void post(String path, Object body) {
		restClient.post().uri(path)
				.contentType(MediaType.APPLICATION_JSON)
				.body(body)
				.retrieve()
				.toBodilessEntity();
	}

	/** Govdesiz POST (finish/cancel gibi eylem uc noktalari). */
	public <T> T postNoBody(String path, Class<T> responseType) {
		return restClient.post().uri(path).retrieve().body(responseType);
	}

	public <T> T put(String path, Object body, Class<T> responseType) {
		return restClient.put().uri(path)
				.contentType(MediaType.APPLICATION_JSON)
				.body(body)
				.retrieve()
				.body(responseType);
	}
}
