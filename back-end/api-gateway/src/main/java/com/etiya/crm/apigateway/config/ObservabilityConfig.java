package com.etiya.crm.apigateway.config;

import io.micrometer.observation.ObservationPredicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.observation.ClientRequestObservationContext;
import org.springframework.http.server.reactive.observation.ServerRequestObservationContext;

/**
 * Iki ayri gurultu kaynagini observation'dan (hem metrik hem trace) haric tutar:
 * 1) Prometheus'un kendi /actuator/prometheus scrape'i.
 * 2) Eureka client'inin discovery-server'a attigi periyodik registry/heartbeat
 *    cagrilari (/eureka/**) - Eureka client'i reaktif olmayan (blocking) bir
 *    HTTP istemcisi kullandigi icin gateway'in kendi reaktif server context'inden
 *    farkli, ayni customer-service'teki gibi bloklayan ClientRequestObservationContext
 *    tipini kullanir.
 * Gateway reaktif (WebFlux) oldugu icin sunucu-taraf kontrolde customer-service'teki
 * servlet tabanli ServerRequestObservationContext yerine bu paketin reactive
 * varyanti kullanilir.
 */
@Configuration
public class ObservabilityConfig {

	@Bean
	public ObservationPredicate noiseObservationsExcluded() {
		return (name, context) -> {
			if (context instanceof ServerRequestObservationContext serverContext) {
				String path = serverContext.getCarrier().getURI().getPath();
				return !path.startsWith("/actuator") && !path.startsWith("/eureka");
			}
			if (context instanceof ClientRequestObservationContext clientContext) {
				String path = clientContext.getCarrier().getURI().getPath();
				return !path.startsWith("/eureka");
			}
			return true;
		};
	}
}
