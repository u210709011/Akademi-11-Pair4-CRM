package com.etiya.crm.customerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/** api-gateway'deki KeycloakWebClientConfig ile ayni fikir: issuer-uri zaten Keycloak realm base URL'idir. */
@Configuration
public class KeycloakRestClientConfig {

	@Bean
	public RestClient keycloakRestClient(
			@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri) {
		return RestClient.builder().baseUrl(issuerUri).build();
	}
}
