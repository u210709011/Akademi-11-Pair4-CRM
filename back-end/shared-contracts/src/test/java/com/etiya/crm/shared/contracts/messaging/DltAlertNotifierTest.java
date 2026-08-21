package com.etiya.crm.shared.contracts.messaging;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class DltAlertNotifierTest {

	private HttpServer server;

	@AfterEach
	void tearDown() {
		if (server != null) {
			server.stop(0);
		}
	}

	@Test
	void alert_doesNotThrow_whenWebhookUrlNotConfigured() {
		DltAlertNotifier notifier = new DltAlertNotifier();

		assertThatCode(() -> notifier.alert("customer-service", "PartyEventListener", "PartyEvent",
				"RuntimeException", "boom")).doesNotThrowAnyException();
	}

	@Test
	void alert_postsToWebhook_whenUrlConfigured() throws IOException, InterruptedException {
		AtomicReference<String> capturedBody = new AtomicReference<>();
		CountDownLatch latch = new CountDownLatch(1);
		server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
		server.createContext("/webhook", exchange -> {
			capturedBody.set(new String(exchange.getRequestBody().readAllBytes()));
			exchange.sendResponseHeaders(200, -1);
			exchange.close();
			latch.countDown();
		});
		server.start();

		DltAlertNotifier notifier = new DltAlertNotifier();
		ReflectionTestUtils.setField(notifier, "webhookUrl",
				"http://localhost:" + server.getAddress().getPort() + "/webhook");

		notifier.alert("customer-service", "PartyEventListener", "PartyEvent", "RuntimeException", "boom");

		assertThat(latch.await(2, TimeUnit.SECONDS)).isTrue();
		assertThat(capturedBody.get()).contains("customer-service", "PartyEventListener", "PartyEvent");
	}

	@Test
	void alert_doesNotThrow_whenWebhookCallFails() {
		DltAlertNotifier notifier = new DltAlertNotifier();
		ReflectionTestUtils.setField(notifier, "webhookUrl", "http://localhost:1/unreachable");

		assertThatCode(() -> notifier.alert("customer-service", "PartyEventListener", "PartyEvent",
				"RuntimeException", "boom")).doesNotThrowAnyException();
	}
}
