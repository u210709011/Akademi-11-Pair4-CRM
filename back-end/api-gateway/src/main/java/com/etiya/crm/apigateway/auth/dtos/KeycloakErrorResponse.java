package com.etiya.crm.apigateway.auth.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Keycloak /protocol/openid-connect/token hata govdesi (snake_case). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record KeycloakErrorResponse(

		@JsonProperty("error")
		String error,

		@JsonProperty("error_description")
		String errorDescription) {
}
