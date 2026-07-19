package com.etiya.crm.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Tek bir Swagger UI'da (http://localhost:8080/swagger-ui.html) tum
 * servislerin API dokumanini gormek icin: her servis kendi /v3/api-docs'unu
 * kendi springdoc'uyla uretir, bu route'lar sadece o JSON'lari gateway'in
 * kendi origin'i (localhost:8080) altina tasir - boylece Swagger UI'in
 * tarayicidan yaptigi fetch cagrisi ayni-origin kalir, CORS ayari gerekmez.
 *
 * Eureka discovery-locator'in (config-server'daki remote ayarlar) bu
 * route'larla catismasini onlemek icin burada Java ile (property degil)
 * tanimlandi - remote YAML property'leri lokal property'leri golgeleyebilir,
 * ama Java RouteLocator bean'i her zaman eklenir.
 */
@Configuration
public class SwaggerAggregatorConfig {

	@Bean
	public RouteLocator swaggerDocsRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("customer-service-docs", r -> r.path("/v3/api-docs/customer-service")
						.filters(f -> f.rewritePath("/v3/api-docs/customer-service", "/v3/api-docs"))
						.uri("lb://customer-service"))
				.route("contact-info-service-docs", r -> r.path("/v3/api-docs/contact-info-service")
						.filters(f -> f.rewritePath("/v3/api-docs/contact-info-service", "/v3/api-docs"))
						.uri("lb://contact-info-service"))
				.route("lookup-service-docs", r -> r.path("/v3/api-docs/lookup-service")
						.filters(f -> f.rewritePath("/v3/api-docs/lookup-service", "/v3/api-docs"))
						.uri("lb://lookup-service"))
				.build();
	}
}
