package com.etiya.crm.contactinfoservice.security;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAuditorAwareTest {

	private final JwtAuditorAware auditorAware = new JwtAuditorAware();

	@AfterEach
	void clearContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void returnsSystem_whenNoAuthentication() {
		SecurityContextHolder.clearContext();

		assertThat(auditorAware.getCurrentAuditor()).contains("system");
	}

	@Test
	void returnsPreferredUsername_whenAuthenticatedWithJwt() {
		Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").claim("preferred_username", "ahmet.yilmaz")
				.build();
		TestingAuthenticationToken token = new TestingAuthenticationToken(jwt, null, "ROLE_CRM_AGENT");
		token.setAuthenticated(true);
		SecurityContextHolder.getContext().setAuthentication(token);

		assertThat(auditorAware.getCurrentAuditor()).contains("ahmet.yilmaz");
	}

	@Test
	void returnsSystem_whenJwtHasNoPreferredUsernameClaim() {
		Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").claim("sub", "some-subject").build();
		TestingAuthenticationToken token = new TestingAuthenticationToken(jwt, null, "ROLE_CRM_AGENT");
		token.setAuthenticated(true);
		SecurityContextHolder.getContext().setAuthentication(token);

		assertThat(auditorAware.getCurrentAuditor()).contains("system");
	}

	@Test
	void returnsSystem_whenPrincipalIsNotJwt() {
		TestingAuthenticationToken token = new TestingAuthenticationToken(Map.of("user", "x"), null,
				"ROLE_CRM_AGENT");
		token.setAuthenticated(true);
		SecurityContextHolder.getContext().setAuthentication(token);

		assertThat(auditorAware.getCurrentAuditor()).contains("system");
	}
}
