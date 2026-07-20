package com.etiya.crm.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Bu OpenAPI, sadece gateway'in KENDI controller'ini (AuthController: login/
 * refresh/logout) dokumante eder. Diger servislerin dokumani icin bkz.
 * SwaggerAggregatorConfig + application.yml springdoc.swagger-ui.urls -
 * swagger-ui.html acildiginda ust-sagdaki dropdown'dan hepsi arasinda
 * gecis yapilabilir.
 */
@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI apiGatewayOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("API Gateway - Auth")
						.description("""
								Keycloak (realm: crm) onunde confidential client (crm-client) olarak calisan
								token endpoint'leri. Diger tum servislerin gercek is API'leri icin sagdaki
								dropdown'dan ilgili servisi secin - bu sayfa sadece login/refresh/logout icindir.
								""")
						.version("v1"));
	}
}
