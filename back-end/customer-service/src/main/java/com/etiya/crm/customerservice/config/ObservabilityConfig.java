package com.etiya.crm.customerservice.config;

import io.micrometer.observation.ObservationPredicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.observation.ClientRequestObservationContext;
import org.springframework.http.server.observation.ServerRequestObservationContext;

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
