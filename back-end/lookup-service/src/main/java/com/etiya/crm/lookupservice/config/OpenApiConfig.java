package com.etiya.crm.lookupservice.config;

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
	public OpenAPI lookupServiceOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("Lookup Service API")
						.description("""
								Genel amacli id-deger tablosu (GROUP_CODE + VALUE_ID + CODE + deger). Amaci
								diger servislerdeki (customer/party/contact-info) kucuk referans tablolarinin
								(gender, city, status, contact medium type, data type ...) coklanmasini onlemek.
								Sadece okunur; deger ekleme/degistirme migration ile yapilir (bkz.
								lookup-service V2__seed_lookup.sql basindaki degismezlik kurallari).
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
