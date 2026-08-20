package com.etiya.crm.partyservice.messaging;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.shared.events.customer.CustomerDeletedEvent;
import com.etiya.crm.shared.events.customer.CustomerEventTypes;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Bu test sadece adapter'in kendi sorumlulugunu (event'i CustomerDeletedEventHandler'a
 * devretmek) dogrular - is mantigi (deactivate cagrilari) artik CustomerDeletedEventHandlerTest'te.
 */
@ExtendWith(MockitoExtension.class)
class CustomerEventListenerTest {

	@Mock
	private CustomerDeletedEventHandler customerDeletedEventHandler;

	@InjectMocks
	private CustomerEventListener listener;

	@Test
	void onMessage_delegatesToHandler() {
		CustomerDeletedEvent event = new CustomerDeletedEvent(UUID.randomUUID(), CustomerEventTypes.CUSTOMER_DELETED,
				10L, 20L, 12L);

		listener.onMessage(event);

		verify(customerDeletedEventHandler).handle(event);
	}

	@Test
	void onMessageDlt_incrementsDltCounter() {
		SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
		CustomerEventListener dltListener = new CustomerEventListener(customerDeletedEventHandler, meterRegistry);
		CustomerDeletedEvent event = new CustomerDeletedEvent(UUID.randomUUID(), CustomerEventTypes.CUSTOMER_DELETED,
				10L, 20L, 12L);

		dltListener.onMessageDlt(event, "SomeException", "boom");

		assertThat(meterRegistry.get("kafka.dlt.events").tag("eventType", event.type()).counter().count())
				.isEqualTo(1.0);
	}
}
