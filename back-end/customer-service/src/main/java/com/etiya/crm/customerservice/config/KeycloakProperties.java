package com.etiya.crm.customerservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

/** api-gateway'deki KeycloakProperties ile ayni desen - crm-client'in service-account kimlik bilgileri. */
@Validated
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {

	@NotBlank
	private String clientId;

	@NotBlank
	private String clientSecret;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
}
