package com.etiya.crm.orderservice.config;

import org.junit.jupiter.api.Test;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {

	private final OpenApiConfig config = new OpenApiConfig();

	@Test
	void definesTwoServers_gatewayAndDirect() {
		OpenAPI openApi = config.orderServiceOpenApi();

		assertThat(openApi.getServers()).hasSize(2);
		assertThat(openApi.getServers().get(0).getUrl()).isEqualTo("http://localhost:8080");
	}

	@Test
	void definesBearerJwtSecurityScheme() {
		OpenAPI openApi = config.orderServiceOpenApi();

		SecurityScheme scheme = openApi.getComponents().getSecuritySchemes().get("bearerAuth");
		assertThat(scheme.getType()).isEqualTo(SecurityScheme.Type.HTTP);
		assertThat(scheme.getBearerFormat()).isEqualTo("JWT");
	}
}
