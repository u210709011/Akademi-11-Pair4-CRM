package com.etiya.crm.apigateway.config;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties.SwaggerUrl;
import org.springdoc.core.properties.SwaggerUiConfigProperties;

import com.etiya.crm.apigateway.config.SwaggerAggregatorConfig.AggregatedService;

/**
 * Swagger UI'in ust-sagdaki servis dropdown'u, springdoc'un otomatik olusturdugu
 * SwaggerUiConfigProperties bean'inin "urls" alanindan besleniyor - normalde bu,
 * config-server'daki springdoc.swagger-ui.urls property'siyle doldurulur (bu repoda
 * gorunmez). Burada bilerek onun yerine geciyoruz: SwaggerAggregatorConfig.AGGREGATED_SERVICES
 * tek gercek kaynak olsun diye, bu BeanPostProcessor o bean tam bagli (property binding
 * bitmis) haldeyken devreye girip "urls" setini AGGREGATED_SERVICES'ten uretilen listeyle
 * DEGISTIRIR (config-server'daki eski/eksik listeyle karisip cift sekme olusmasin diye
 * merge degil, replace).
 *
 * "API Gateway (Auth)" ayrica elle eklenir: bu, AGGREGATED_SERVICES'teki proxy edilen
 * business servislerden farkli - gateway'in KENDI /v3/api-docs'u (login/refresh/logout,
 * bkz. OpenApiConfig.apiGatewayOpenApi), lb:// proxy'sine ihtiyaci yok.
 */
@Component
public class SwaggerUiDropdownCustomizer implements BeanPostProcessor {

	private static final String GATEWAY_AUTH_DOCS_URL = "/v3/api-docs";
	private static final String GATEWAY_AUTH_DOCS_NAME = "API Gateway (Auth)";

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof SwaggerUiConfigProperties properties) {
			Set<SwaggerUrl> urls = new LinkedHashSet<>();

			SwaggerUrl gatewayAuthUrl = new SwaggerUrl();
			gatewayAuthUrl.setName(GATEWAY_AUTH_DOCS_NAME);
			gatewayAuthUrl.setDisplayName(GATEWAY_AUTH_DOCS_NAME);
			gatewayAuthUrl.setUrl(GATEWAY_AUTH_DOCS_URL);
			urls.add(gatewayAuthUrl);

			for (AggregatedService service : SwaggerAggregatorConfig.AGGREGATED_SERVICES) {
				SwaggerUrl url = new SwaggerUrl();
				url.setName(service.id());
				url.setDisplayName(service.displayName());
				url.setUrl("/v3/api-docs/" + service.id());
				urls.add(url);
			}
			properties.setUrls(urls);
		}
		return bean;
	}
}
