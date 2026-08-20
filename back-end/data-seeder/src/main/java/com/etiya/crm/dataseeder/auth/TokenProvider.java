package com.etiya.crm.dataseeder.auth;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.etiya.crm.dataseeder.SeederProperties;

/**
 * Keycloak'tan CRM_AGENT rolune sahip bir kullanici (salesperson/password) icin token alir -
 * CLAUDE.md'deki dokumante edilmis password-grant curl ile ayni. Servisler gibi M2M client
 * credentials degil, gercek bir sales rep oturumu simule edildigi icin kullanici token'i.
 */
@Component
public class TokenProvider {

	private final SeederProperties props;
	private final RestClient restClient = RestClient.create();

	public TokenProvider(SeederProperties props) {
		this.props = props;
	}

	public String fetchAccessToken() {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("client_id", props.keycloakClientId());
		form.add("client_secret", props.keycloakClientSecret());
		form.add("grant_type", "password");
		form.add("username", props.keycloakUsername());
		form.add("password", props.keycloakPassword());

		@SuppressWarnings("unchecked")
		Map<String, Object> response = restClient.post()
				.uri(props.keycloakTokenUrl())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(form)
				.retrieve()
				.body(Map.class);

		if (response == null || response.get("access_token") == null) {
			throw new IllegalStateException("Keycloak'tan access_token alinamadi: " + response);
		}
		return (String) response.get("access_token");
	}
}
