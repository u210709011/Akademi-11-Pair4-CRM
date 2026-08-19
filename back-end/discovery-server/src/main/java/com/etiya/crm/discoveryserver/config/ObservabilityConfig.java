package com.etiya.crm.discoveryserver.config;

import io.micrometer.observation.ObservationPredicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.observation.ServerRequestObservationContext;

/**
 * Iki ayri gurultu kaynagini observation'dan (hem metrik hem trace) haric tutar:
 * 1) Prometheus'un kendi /actuator/prometheus scrape'i.
 * 2) Diger servislerin buraya attigi periyodik /eureka/** registry/heartbeat
 *    cagrilari - discovery-server bu cagrilarin SUNUCU tarafi oldugu icin
 *    (kendisi bir Eureka client'i degil) sadece server-side filtre yeterli.
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
			return true;
		};
	}
}
