package com.etiya.crm.configserver.config;

import io.micrometer.observation.ObservationPredicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.observation.ServerRequestObservationContext;

/**
 * Prometheus'un kendi /actuator/prometheus scrape'i hem metrik hem trace
 * uretiyordu - gercek trafigi bogan gurultu olusturuyordu. config-server bir
 * Eureka client'i olmadigi icin (bkz. pom.xml) /eureka filtresine gerek yok.
 */
@Configuration
public class ObservabilityConfig {

	@Bean
	public ObservationPredicate noActuatorObservations() {
		return (name, context) -> {
			if (context instanceof ServerRequestObservationContext serverContext) {
				return !serverContext.getCarrier().getRequestURI().startsWith("/actuator");
			}
			return true;
		};
	}
}
