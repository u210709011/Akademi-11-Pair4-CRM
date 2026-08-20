package com.etiya.crm.customerservice.config;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.observation.ClientRequestObservationContext;
import org.springframework.http.server.observation.ServerRequestObservationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationPredicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Prometheus'un /actuator/prometheus scrape'i ve Eureka client'inin /eureka/**
 * heartbeat cagrilarinin metrik+trace uretiminden haric tutuldugunu dogrular -
 * bkz. sinifin javadoc'u (gercek trafigi bogan gurultu kaynagiydi).
 */
class ObservabilityConfigTest {

	private final ObservationPredicate predicate = new ObservabilityConfig().noiseObservationsExcluded();

	@Test
	void excludesActuatorPaths_onServerSide() {
		boolean result = predicate.test("http.server.requests", serverContext("/actuator/prometheus"));

		assertThat(result).isFalse();
	}

	@Test
	void excludesEurekaPaths_onServerSide() {
		boolean result = predicate.test("http.server.requests", serverContext("/eureka/apps/delta"));

		assertThat(result).isFalse();
	}

	@Test
	void keepsBusinessPaths_onServerSide() {
		boolean result = predicate.test("http.server.requests", serverContext("/api/v1/customers/10"));

		assertThat(result).isTrue();
	}

	@Test
	void excludesEurekaPaths_onClientSide() {
		boolean result = predicate.test("http.client.requests", clientContext("/eureka/apps/customer-service"));

		assertThat(result).isFalse();
	}

	@Test
	void keepsFeignCalls_onClientSide() {
		boolean result = predicate.test("http.client.requests", clientContext("/api/v1/parties/individuals/exists"));

		assertThat(result).isTrue();
	}

	@Test
	void keepsOtherObservationTypes_byDefault() {
		boolean result = predicate.test("some.other.observation", new Observation.Context());

		assertThat(result).isTrue();
	}

	private ServerRequestObservationContext serverContext(String path) {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
		return new ServerRequestObservationContext(request, new MockHttpServletResponse());
	}

	private ClientRequestObservationContext clientContext(String path) {
		ClientHttpRequest request = mock(ClientHttpRequest.class);
		when(request.getURI()).thenReturn(URI.create("http://localhost:8761" + path));
		return new ClientRequestObservationContext(request);
	}
}
