package com.etiya.crm.customerservice.security;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * jwtAuthenticationConverter() saf bir donusum fonksiyonu - securityFilterChain
 * ise HttpSecurity DSL'i cagirdigi icin (yalnizca gercek bir Spring context
 * icinde calisir) burada test edilmiyor.
 */
class SecurityConfigTest {

	private final SecurityConfig securityConfig = new SecurityConfig();

	@Test
	void convertsRealmRoles_toPrefixedAuthorities() {
		JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverter();
		Jwt jwt = Jwt.withTokenValue("token")
				.header("alg", "none")
				.claim("realm_access", Map.of("roles", List.of("CRM_AGENT", "ADMIN")))
				.build();

		var authorities = converter.convert(jwt).getAuthorities();

		assertThat(authorities).extracting(GrantedAuthority::getAuthority)
				.containsExactlyInAnyOrder("ROLE_CRM_AGENT", "ROLE_ADMIN");
	}

	@Test
	void returnsNoAuthorities_whenRealmAccessClaimMissing() {
		JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverter();
		Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").claim("sub", "user").build();

		var authorities = converter.convert(jwt).getAuthorities();

		assertThat(authorities).isEmpty();
	}

	@Test
	void returnsNoAuthorities_whenRolesClaimIsNotACollection() {
		JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverter();
		Jwt jwt = Jwt.withTokenValue("token").header("alg", "none")
				.claim("realm_access", Map.of("roles", "CRM_AGENT"))
				.build();

		var authorities = converter.convert(jwt).getAuthorities();

		assertThat(authorities).isEmpty();
	}
}
