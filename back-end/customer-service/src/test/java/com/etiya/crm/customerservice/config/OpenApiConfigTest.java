package com.etiya.crm.customerservice.config;

import org.junit.jupiter.api.Test;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {

	private final OpenApiConfig config = new OpenApiConfig();

	@Test
	void definesTwoServers_gatewayAndDirect() {
		OpenAPI openApi = config.customerServiceOpenApi();

		assertThat(openApi.getServers()).hasSize(2);
		assertThat(openApi.getServers().get(0).getUrl()).isEqualTo("http://localhost:8080");
		assertThat(openApi.getServers().get(1).getUrl()).isEqualTo("/");
	}

	@Test
	void definesBearerJwtSecurityScheme() {
		OpenAPI openApi = config.customerServiceOpenApi();

		SecurityScheme scheme = openApi.getComponents().getSecuritySchemes().get("bearerAuth");
		assertThat(scheme.getType()).isEqualTo(SecurityScheme.Type.HTTP);
		assertThat(scheme.getScheme()).isEqualTo("bearer");
		assertThat(scheme.getBearerFormat()).isEqualTo("JWT");
		assertThat(openApi.getSecurity()).hasSize(1);
	}
}
