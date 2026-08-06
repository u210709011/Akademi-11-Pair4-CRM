package com.etiya.crm.orderservice.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Swagger UI: http://localhost:{port}/swagger-ui.html
 * OpenAPI JSON: http://localhost:{port}/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

	private static final String BEARER_SCHEME = "bearerAuth";

	@Bean
	public OpenAPI orderServiceOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("Order Service API")
						.description("""
								Offer Selection -> Product Configuration -> Review & Confirm siparis akisi
								(CustOrd/CustOrdItem/BsnInter). Sepet dogrulama, karakteristik/adres autosave,
								finish'te product-service provizyonu ve OrderSubmittedEvent yayini bu serviste.
								""")
						.version("v1"))
				.servers(List.of(
						new Server().url("http://localhost:8080").description("API Gateway uzerinden (onerilen)"),
						new Server().url("/").description("Bu servisin kendi adresi (gateway'i atlar)")))
				.components(new Components()
						.addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")))
				.addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
	}
}
