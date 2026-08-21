package com.etiya.crm.customerservice.messaging;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.shared.contracts.messaging.DltAlertNotifier;
import com.etiya.crm.shared.events.saga.SagaEventTypes;
import com.etiya.crm.shared.events.saga.SagaStepNames;
import com.etiya.crm.shared.events.saga.SagaStepResultEvent;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SagaStepResultEventListenerTest {

	@Mock
	private SagaStepResultEventHandler sagaStepResultEventHandler;

	@Mock
	private DltAlertNotifier dltAlertNotifier;

	private SimpleMeterRegistry meterRegistry;
	private SagaStepResultEventListener listener;

	@BeforeEach
	void setUp() {
		meterRegistry = new SimpleMeterRegistry();
		listener = new SagaStepResultEventListener(sagaStepResultEventHandler, meterRegistry, dltAlertNotifier);
	}

	@Test
	void onSagaStepResult_delegatesToHandler() {
		SagaStepResultEvent event = stepFailedEvent();

		listener.onSagaStepResult(event);

		verify(sagaStepResultEventHandler).handle(event);
	}

	@Test
	void onSagaStepResultDlt_incrementsDltCounter() {
		SagaStepResultEvent event = stepFailedEvent();

		listener.onSagaStepResultDlt(event, "SomeException", "boom");

		assertThat(meterRegistry.get("kafka.dlt.events").tag("eventType", event.type()).counter().count())
				.isEqualTo(1.0);
	}

	@Test
	void onSagaStepResultDlt_sendsAlert() {
		SagaStepResultEvent event = stepFailedEvent();

		listener.onSagaStepResultDlt(event, "SomeException", "boom");

		verify(dltAlertNotifier).alert("customer-service", "SagaStepResultEventListener", event.type(),
				"SomeException", "boom");
	}

	private SagaStepResultEvent stepFailedEvent() {
		return new SagaStepResultEvent(UUID.randomUUID(), SagaEventTypes.STEP_FAILED, 10L,
				SagaStepNames.PARTY_DEACTIVATION, false, "boom");
	}
}
