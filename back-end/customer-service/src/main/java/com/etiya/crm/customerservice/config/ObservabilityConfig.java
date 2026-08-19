package com.etiya.crm.customerservice.config;

import io.micrometer.observation.ObservationPredicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.observation.ClientRequestObservationContext;
import org.springframework.http.server.observation.ServerRequestObservationContext;

/**
 * Iki ayri gurultu kaynagini observation'dan (hem metrik hem trace) haric tutar:
 * 1) Prometheus'un kendi /actuator/prometheus scrape'i (5sn'de bir, her servis) -
 *    Grafana'daki duz cizgiler, Tempo'daki sonsuz "actuator/prometheus" trace'leri.
 * 2) Eureka client'inin discovery-server'a attigi periyodik registry/heartbeat
 *    cagrilari (/eureka/**, ~30sn'de bir) - bu servis-taraf degil CLIENT-taraf
 *    (RestTemplate/RestClient) observation'i, yukaridakinden FARKLI bir context
 *    tipi gerektiriyor. Ikisi de kalici arka plan trafigi, is mantigiyla ilgisi
 *    yok, ama Tempo'daki gercek istek trace'lerini bogacak sikligada.
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
