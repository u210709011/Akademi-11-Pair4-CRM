package com.etiya.crm.apigateway.auth;

import java.util.Locale;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.etiya.crm.apigateway.auth.constants.MessageKeys;
import com.etiya.crm.apigateway.auth.dtos.KeycloakErrorResponse;
import com.etiya.crm.apigateway.auth.dtos.KeycloakTokenResponse;
import com.etiya.crm.apigateway.auth.dtos.TokenResponse;
import com.etiya.crm.apigateway.auth.exceptions.AccountLockedException;
import com.etiya.crm.apigateway.auth.exceptions.InvalidCredentialsException;

import reactor.core.publisher.Mono;

@Service
public class AuthService {

	private static final String TOKEN_PATH = "/protocol/openid-connect/token";
	private static final String LOGOUT_PATH = "/protocol/openid-connect/logout";

	private final WebClient keycloakWebClient;
	private final KeycloakProperties keycloakProperties;

	public AuthService(WebClient keycloakWebClient, KeycloakProperties keycloakProperties) {
		this.keycloakWebClient = keycloakWebClient;
		this.keycloakProperties = keycloakProperties;
	}

	public Mono<TokenResponse> login(String username, String password, String clientId) {
		MultiValueMap<String, String> form = baseForm(clientId);
		form.add("grant_type", "password");
		form.add("username", username);
		form.add("password", password);
		return requestToken(form);
	}

	public Mono<TokenResponse> refresh(String refreshToken) {
		MultiValueMap<String, String> form = baseForm();
		form.add("grant_type", "refresh_token");
		form.add("refresh_token", refreshToken);
		return requestToken(form);
	}

	public Mono<Void> logout(String refreshToken) {
		MultiValueMap<String, String> form = baseForm();
		form.add("refresh_token", refreshToken);
		return keycloakWebClient.post()
				.uri(LOGOUT_PATH)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(BodyInserters.fromFormData(form))
				.retrieve()
				.onStatus(HttpStatusCode::isError,
						response -> Mono.error(new InvalidCredentialsException(MessageKeys.LOGOUT_FAILED)))
				.toBodilessEntity()
				.then();
	}

	private Mono<TokenResponse> requestToken(MultiValueMap<String, String> form) {
		return keycloakWebClient.post()
				.uri(TOKEN_PATH)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(BodyInserters.fromFormData(form))
				.retrieve()
				.onStatus(HttpStatusCode::isError, this::toAuthError)
				.bodyToMono(KeycloakTokenResponse.class)
				.map(KeycloakTokenResponse::toTokenResponse);
	}

	/**
	 * Keycloak, hem yanlis sifrede hem de bruteforce kilitlemesinde (bkz. crm-realm.json
	 * bruteForceProtected/failureFactor/waitIncrementSeconds) ayni HTTP durumunu
	 * ("invalid_grant") doner - ikisini ayirt etmek icin error_description govdesine
	 * bakmak gerekir. Bu ayrim olmadan caller (frontend) gercek kilitlenme durumunu asla
	 * bilemez ve kendi basina, Keycloak'in gercek durumundan bagimsiz bir sayac tutmak
	 * zorunda kalir.
	 */
	private Mono<? extends Throwable> toAuthError(ClientResponse response) {
		return response.bodyToMono(KeycloakErrorResponse.class)
				.defaultIfEmpty(new KeycloakErrorResponse(null, null))
				.map(error -> isAccountLocked(error.errorDescription())
						? new AccountLockedException(MessageKeys.ACCOUNT_LOCKED)
						: new InvalidCredentialsException(MessageKeys.INVALID_CREDENTIALS));
	}

	private boolean isAccountLocked(String errorDescription) {
		return errorDescription != null && errorDescription.toLowerCase(Locale.ROOT).contains("disabled");
	}

	private MultiValueMap<String, String> baseForm() {
		return baseForm(null);
	}

	/**
	 * clientId caller'dan gelen bir ALIAS'tir ("default"/"short-lived"), gercek Keycloak
	 * client_id/secret degildir - secret'in caller'dan gelmesine asla izin verilmez, sadece
	 * onceden tanimli iki client arasindan secim yapilabilir (bkz. crm-realm.json).
	 */
	private MultiValueMap<String, String> baseForm(String clientId) {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		if ("short-lived".equalsIgnoreCase(clientId)) {
			form.add("client_id", keycloakProperties.getShortLivedClientId());
			form.add("client_secret", keycloakProperties.getShortLivedClientSecret());
		} else {
			form.add("client_id", keycloakProperties.getClientId());
			form.add("client_secret", keycloakProperties.getClientSecret());
		}
		return form;
	}
}
