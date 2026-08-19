package com.etiya.crm.orderservice.config;

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

class ObservabilityConfigTest {

	private final ObservationPredicate predicate = new ObservabilityConfig().noiseObservationsExcluded();

	@Test
	void excludesActuatorPaths_onServerSide() {
		assertThat(predicate.test("http.server.requests", serverContext("/actuator/prometheus"))).isFalse();
	}

	@Test
	void excludesEurekaPaths_onServerSide() {
		assertThat(predicate.test("http.server.requests", serverContext("/eureka/apps/delta"))).isFalse();
	}

	@Test
	void keepsBusinessPaths_onServerSide() {
		assertThat(predicate.test("http.server.requests", serverContext("/api/v1/orders/1"))).isTrue();
	}

	@Test
	void excludesEurekaPaths_onClientSide() {
		assertThat(predicate.test("http.client.requests", clientContext("/eureka/apps/order-service"))).isFalse();
	}

	@Test
	void keepsFeignCalls_onClientSide() {
		assertThat(predicate.test("http.client.requests", clientContext("/api/v1/general-types/1"))).isTrue();
	}

	@Test
	void keepsOtherObservationTypes_byDefault() {
		assertThat(predicate.test("some.other.observation", new Observation.Context())).isTrue();
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
