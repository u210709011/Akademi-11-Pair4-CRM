package com.etiya.crm.partyservice.messaging;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.shared.contracts.messaging.DltAlertNotifier;
import com.etiya.crm.shared.events.customer.CustomerDeletedEvent;
import com.etiya.crm.shared.events.customer.CustomerEventTypes;
import com.etiya.crm.shared.events.outbox.OutboxEventPublisher;
import com.etiya.crm.shared.events.saga.SagaStepResultEvent;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * Bu test sadece adapter'in kendi sorumlulugunu (event'i CustomerDeletedEventHandler'a
 * devretmek) dogrular - is mantigi (deactivate cagrilari) artik CustomerDeletedEventHandlerTest'te.
 */
@ExtendWith(MockitoExtension.class)
class CustomerEventListenerTest {

	@Mock
	private CustomerDeletedEventHandler customerDeletedEventHandler;

	@Mock
	private OutboxEventPublisher outboxEventPublisher;

	@Mock
	private DltAlertNotifier dltAlertNotifier;

	private SimpleMeterRegistry meterRegistry;
	private CustomerEventListener listener;

	@BeforeEach
	void setUp() {
		meterRegistry = new SimpleMeterRegistry();
		listener = new CustomerEventListener(customerDeletedEventHandler, meterRegistry, outboxEventPublisher,
				dltAlertNotifier);
	}

	@Test
	void onMessage_delegatesToHandler() {
		CustomerDeletedEvent event = deletedEvent();

		listener.onMessage(event);

		verify(customerDeletedEventHandler).handle(event);
	}

	@Test
	void onMessageDlt_incrementsDltCounter() {
		CustomerDeletedEvent event = deletedEvent();

		listener.onMessageDlt(event, "SomeException", "boom");

		assertThat(meterRegistry.get("kafka.dlt.events").tag("eventType", event.type()).counter().count())
				.isEqualTo(1.0);
	}

	@Test
	void onMessageDlt_sendsAlert() {
		CustomerDeletedEvent event = deletedEvent();

		listener.onMessageDlt(event, "SomeException", "boom");

		verify(dltAlertNotifier).alert("party-service", "PartyServiceCustomerEventListener", event.type(),
				"SomeException", "boom");
	}

	@Test
	void onMessageDlt_publishesFailedStepResult_forCustomerDeletedType() {
		CustomerDeletedEvent event = deletedEvent();

		listener.onMessageDlt(event, "SomeException", "boom");

		ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
		verify(outboxEventPublisher).publish(any(), any(), any(), payloadCaptor.capture());
		SagaStepResultEvent published = (SagaStepResultEvent) payloadCaptor.getValue();
		assertThat(published.custId()).isEqualTo(10L);
		assertThat(published.success()).isFalse();
	}

	private CustomerDeletedEvent deletedEvent() {
		return new CustomerDeletedEvent(UUID.randomUUID(), CustomerEventTypes.CUSTOMER_DELETED, 10L, 20L, 12L);
	}
}
