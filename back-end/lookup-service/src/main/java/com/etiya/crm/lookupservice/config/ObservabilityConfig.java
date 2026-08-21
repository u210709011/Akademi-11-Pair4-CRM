package com.etiya.crm.lookupservice.config;

import io.micrometer.observation.ObservationPredicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.observation.ClientRequestObservationContext;
import org.springframework.http.server.observation.ServerRequestObservationContext;

/**
 * Iki ayri gurultu kaynagini observation'dan (hem metrik hem trace) haric tutar:
 * 1) Prometheus'un kendi /actuator/prometheus scrape'i.
 * 2) Eureka client'inin discovery-server'a attigi periyodik registry/heartbeat
 *    cagrilari (/eureka/**) - sunucu-taraf degil CLIENT-taraf (RestTemplate/
 *    RestClient) observation'i, farkli bir context tipi gerektiriyor.
 */
@Configuration
public class ObservabilityConfig {

	@Bean
	public ObservationPredicate noiseObservationsExcluded() {
		return (name, context) -> {
			if (context instanceof ServerRequestObservationContext serverContext) {
				String path = serverContext.getCarrier().getRequestURI();
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
