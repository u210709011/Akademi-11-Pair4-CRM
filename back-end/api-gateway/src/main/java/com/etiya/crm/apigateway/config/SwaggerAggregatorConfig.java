package com.etiya.crm.apigateway.config;

import java.util.List;

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
 *
 * AGGREGATED_SERVICES tek gercek kaynak: hem bu route'lari, HEM de Swagger UI'in
 * ust-sagdaki servis dropdown'unu (bkz. SwaggerUiDropdownCustomizer) besler. Once
 * dropdown listesi config-server'daki ayri bir springdoc.swagger-ui.urls
 * property'sine baglıydı - bu repodan gorunmuyordu ve product-service/party-service
 * eklendiginde manuel olarak orada da guncellenmesi unutulmustu (sekme olarak hic
 * gorunmuyorlardi). Simdi tek liste ikisini de surer, config-server'daki o property
 * artik kullanilmiyor (BeanPostProcessor onu her zaman bu listeyle degistirir).
 */
@Configuration
public class SwaggerAggregatorConfig {

	/** id: Eureka servis adi + route/api-docs path segmenti. displayName: Swagger UI dropdown'unda gorunen ad. */
	public record AggregatedService(String id, String displayName) {
	}

	public static final List<AggregatedService> AGGREGATED_SERVICES = List.of(
			new AggregatedService("customer-service", "Customer Service"),
			new AggregatedService("party-service", "Party Service"),
			new AggregatedService("contact-info-service", "Contact Info Service"),
			new AggregatedService("order-service", "Order Service"),
			new AggregatedService("product-service", "Product Service"),
			new AggregatedService("lookup-service", "Lookup Service"));

	@Bean
	public RouteLocator swaggerDocsRouteLocator(RouteLocatorBuilder builder) {
		RouteLocatorBuilder.Builder routes = builder.routes();
		for (AggregatedService service : AGGREGATED_SERVICES) {
			String docsPath = "/v3/api-docs/" + service.id();
			routes.route(service.id() + "-docs", r -> r.path(docsPath)
					.filters(f -> f.rewritePath(docsPath, "/v3/api-docs"))
					.uri("lb://" + service.id()));
		}
		return routes.build();
	}
}
