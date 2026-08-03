package com.etiya.crm.contactinfoservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

/** customer-service'deki KeycloakProperties ile ayni desen - contact-info-service-m2m'in service-account kimlik bilgileri. */
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
