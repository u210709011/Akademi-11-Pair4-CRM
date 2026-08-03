package com.etiya.crm.customerservice.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Keycloak /protocol/openid-connect/token yanit govdesi (snake_case) - bkz. api-gateway'deki ayni isimli DTO. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record KeycloakTokenResponse(

		@JsonProperty("access_token")
		String accessToken,

		@JsonProperty("expires_in")
		Long expiresIn) {
}
