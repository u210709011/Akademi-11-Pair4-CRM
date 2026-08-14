package com.etiya.crm.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import com.etiya.crm.apigateway.constants.SwaggerText;

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
						.title(SwaggerText.OPENAPI_TITLE)
						.description(SwaggerText.OPENAPI_DESCRIPTION)
						.version("v1"));
	}
}
