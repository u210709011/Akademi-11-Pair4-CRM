package com.etiya.crm.apigateway.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {

	@NotBlank
	private String clientId;

	@NotBlank
	private String clientSecret;

	/** Test amacli 30sn access-token-lifespan override'li client (bkz. crm-realm.json). Config'te
	 * ayarlanmazsa realm'deki varsayilan degerlerle calisir, disaridan config gerektirmez. */
	private String shortLivedClientId = "crm-client-short";

	private String shortLivedClientSecret = "crm-client-short";

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

	public String getShortLivedClientId() {
		return shortLivedClientId;
	}

	public void setShortLivedClientId(String shortLivedClientId) {
		this.shortLivedClientId = shortLivedClientId;
	}

	public String getShortLivedClientSecret() {
		return shortLivedClientSecret;
	}

	public void setShortLivedClientSecret(String shortLivedClientSecret) {
		this.shortLivedClientSecret = shortLivedClientSecret;
	}
}
