package com.etiya.crm.apigateway.config;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;

import com.etiya.crm.apigateway.auth.CookieBearerTokenConverter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * securityWebFilterChain() sadece ServerHttpSecurity DSL'ini cagirdigi icin (yalnizca gercek bir
 * Spring context icinde calisir) burada test edilmiyor - corsConfigurationSource() ise saf bir
 * bean builder, Spring context gerektirmeden dogrudan cagrilabilir.
 */
@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

	@Mock
	private CookieBearerTokenConverter cookieBearerTokenConverter;

	@Test
	void corsConfigurationSource_allowsConfiguredOriginsAndCredentials() {
		SecurityConfig securityConfig = new SecurityConfig(cookieBearerTokenConverter);
		ReflectionTestUtils.setField(securityConfig, "allowedOrigins", List.of("http://localhost:4200"));

		CorsConfigurationSource source = securityConfig.corsConfigurationSource();
		ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/customers"));
		CorsConfiguration config = source.getCorsConfiguration(exchange);

		assertThat(config).isNotNull();
		assertThat(config.getAllowedOrigins()).containsExactly("http://localhost:4200");
		assertThat(config.getAllowedMethods()).containsExactlyInAnyOrder("GET", "POST", "PUT", "PATCH", "DELETE",
				"OPTIONS");
		assertThat(config.getAllowedHeaders()).containsExactly("*");
		assertThat(config.getAllowCredentials()).isTrue();
	}

}
