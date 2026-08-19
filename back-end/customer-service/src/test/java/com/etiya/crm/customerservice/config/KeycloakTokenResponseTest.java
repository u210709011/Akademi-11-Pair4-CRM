package com.etiya.crm.customerservice.config;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakTokenResponseTest {

	@Test
	void carriesAccessTokenAndExpiresIn() {
		KeycloakTokenResponse response = new KeycloakTokenResponse("abc123", 300L);

		assertThat(response.accessToken()).isEqualTo("abc123");
		assertThat(response.expiresIn()).isEqualTo(300L);
	}

	@Test
	void deserializesFromKeycloakSnakeCaseJson_ignoringUnknownFields() throws Exception {
		String json = """
				{"access_token":"abc123","expires_in":300,"token_type":"Bearer","scope":"openid"}
				""";

		KeycloakTokenResponse response = new ObjectMapper().readValue(json, KeycloakTokenResponse.class);

		assertThat(response.accessToken()).isEqualTo("abc123");
		assertThat(response.expiresIn()).isEqualTo(300L);
	}
}
