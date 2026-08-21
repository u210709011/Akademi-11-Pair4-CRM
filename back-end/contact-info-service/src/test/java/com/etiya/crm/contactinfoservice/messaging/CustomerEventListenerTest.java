package com.etiya.crm.contactinfoservice.messaging;

import com.etiya.crm.shared.contracts.messaging.DltAlertNotifier;
import com.etiya.crm.shared.events.customer.CustomerDeletedEvent;
import com.etiya.crm.shared.events.outbox.OutboxEventPublisher;
import com.etiya.crm.shared.events.saga.SagaStepResultEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Bu test sadece adapter'in kendi sorumlulugunu (JSON parse + handler'a devir)
 * dogrular - is mantigi (idempotency, deactivate cagrilari) artik
 * CustomerDeletedEventHandlerTest'te.
 */
@ExtendWith(MockitoExtension.class)
class CustomerEventListenerTest {

	@Mock
	private CustomerDeletedEventHandler customerDeletedEventHandler;

	@Mock
	private OutboxEventPublisher outboxEventPublisher;

	@Mock
	private DltAlertNotifier dltAlertNotifier;

	private ObjectMapper objectMapper;
	private SimpleMeterRegistry meterRegistry;
	private CustomerEventListener customerEventListener;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
		meterRegistry = new SimpleMeterRegistry();
		customerEventListener = new CustomerEventListener(customerDeletedEventHandler, objectMapper, meterRegistry,
				outboxEventPublisher, dltAlertNotifier);
	}

	@Test
	void onMessage_parsesPayload_andDelegatesToHandler() {
		ConsumerRecord<String, String> record = recordWithType("CustomerDeleted");

		customerEventListener.onMessage(record);

		ArgumentCaptor<CustomerDeletedEvent> captor = ArgumentCaptor.forClass(CustomerDeletedEvent.class);
		verify(customerDeletedEventHandler).handle(captor.capture());
		assertThat(captor.getValue().custId()).isEqualTo(10L);
		assertThat(captor.getValue().partyRoleId()).isEqualTo(20L);
		assertThat(captor.getValue().dataTypeId()).isEqualTo(12L);
	}

	@Test
	void onMessage_skips_whenPayloadIsNotValidJson() {
		ConsumerRecord<String, String> record = new ConsumerRecord<>("customer-events", 0, 0L, "key", "not-json");

		customerEventListener.onMessage(record);

		verify(customerDeletedEventHandler, never()).handle(any());
	}

	@Test
	void onMessageDlt_incrementsDltCounter() {
		ConsumerRecord<String, String> record = recordWithType("CustomerDeleted");

		customerEventListener.onMessageDlt(record, "SomeException", "boom");

		assertThat(meterRegistry.get("kafka.dlt.events").tag("eventType", "CustomerDeletedEvent").counter().count())
				.isEqualTo(1.0);
	}

	@Test
	void onMessageDlt_sendsAlert() {
		ConsumerRecord<String, String> record = recordWithType("CustomerDeleted");

		customerEventListener.onMessageDlt(record, "SomeException", "boom");

		verify(dltAlertNotifier).alert("contact-info-service", "ContactInfoServiceCustomerEventListener",
				"CustomerDeletedEvent", "SomeException", "boom");
	}

	@Test
	void onMessageDlt_publishesFailedStepResult_whenPayloadParseable() {
		ConsumerRecord<String, String> record = recordWithType("CustomerDeleted");

		customerEventListener.onMessageDlt(record, "SomeException", "boom");

		ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
		verify(outboxEventPublisher).publish(any(), any(), any(), payloadCaptor.capture());
		SagaStepResultEvent published = (SagaStepResultEvent) payloadCaptor.getValue();
		assertThat(published.custId()).isEqualTo(10L);
		assertThat(published.success()).isFalse();
	}

	@Test
	void onMessageDlt_skipsStepResultPublish_whenPayloadNotParseable() {
		ConsumerRecord<String, String> record = new ConsumerRecord<>("customer-events", 0, 0L, "key", "not-json");

		customerEventListener.onMessageDlt(record, "SomeException", "boom");

		verify(outboxEventPublisher, never()).publish(any(), any(), any(), any());
		verify(dltAlertNotifier).alert("contact-info-service", "ContactInfoServiceCustomerEventListener",
				"CustomerDeletedEvent", "SomeException", "boom");
	}

	private ConsumerRecord<String, String> recordWithType(String type) {
		String payload = "{\"eventId\":\"9c1e6e2a-1b2c-4d3e-8f4a-000000000001\",\"type\":\"" + type
				+ "\",\"custId\":10,\"partyRoleId\":20,\"dataTypeId\":12}";
		return new ConsumerRecord<>("customer-events", 0, 0L, "key", payload);
	}
}
