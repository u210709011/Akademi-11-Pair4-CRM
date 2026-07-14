package com.etiya.crm.apigateway.auth.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Keycloak /protocol/openid-connect/token yanit govdesi (snake_case). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record KeycloakTokenResponse(

		@JsonProperty("access_token")
		String accessToken,

		@JsonProperty("refresh_token")
		String refreshToken,

		@JsonProperty("token_type")
		String tokenType,

		@JsonProperty("expires_in")
		Long expiresIn) {

	public TokenResponse toTokenResponse() {
		return new TokenResponse(accessToken, refreshToken, tokenType, expiresIn);
	}
}
