package com.etiya.crm.customerservice.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import com.etiya.crm.customerservice.constants.SwaggerText;

/**
 * Swagger UI: http://localhost:{port}/swagger-ui.html
 * OpenAPI JSON: http://localhost:{port}/v3/api-docs
 *
 * "Authorize" butonu icin: once POST /api/v1/auth/login (gateway) veya
 * infra/http/customer-workflow.http'deki login istegiyle bir accessToken
 * alin, sonra buraya (on eke gerek yok, "Bearer " otomatik eklenir) yapistirin.
 *
 * servers() acikca sabitlenir: forward-headers-strategy'ye ragmen bu ortamda
 * (WSL/Windows agi) springdoc kendi ham adresini (rastgele PORT, 172.x WSL IP'si)
 * kullanmaya devam edebiliyor - "Try it out" tarayicidan erisilemeyen bir
 * adrese gidiyordu. Swagger UI'in ust kisminda "Servers" dropdown'undan
 * ikisi arasinda secim yapilabilir.
 */
@Configuration
public class OpenApiConfig {

	private static final String BEARER_SCHEME = "bearerAuth";

	@Bean
	public OpenAPI customerServiceOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title(SwaggerText.OPENAPI_TITLE)
						.description(SwaggerText.OPENAPI_DESCRIPTION)
						.version("v1"))
				.servers(List.of(
						new Server().url("http://localhost:8080").description(SwaggerText.OPENAPI_GATEWAY_SERVER_DESCRIPTION),
						new Server().url("/").description(SwaggerText.OPENAPI_DIRECT_SERVER_DESCRIPTION)))
				.components(new Components()
						.addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")))
				.addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
	}
}
