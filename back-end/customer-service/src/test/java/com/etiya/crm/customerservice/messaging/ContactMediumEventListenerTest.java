package com.etiya.crm.customerservice.messaging;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.shared.contracts.messaging.DltAlertNotifier;
import com.etiya.crm.shared.events.contactmedium.ContactMediumEvent;
import com.etiya.crm.shared.events.contactmedium.ContactMediumEventTypes;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Bu test sadece adapter'in kendi sorumlulugunu (event'i ContactMediumEventHandler'a
 * devretmek) dogrular - is mantigi (idempotency, filtreleme, gsm senkronu) artik
 * ContactMediumEventHandlerTest'te.
 */
@ExtendWith(MockitoExtension.class)
class ContactMediumEventListenerTest {

	@Mock
	private ContactMediumEventHandler contactMediumEventHandler;

	@Mock
	private DltAlertNotifier dltAlertNotifier;

	private SimpleMeterRegistry meterRegistry;
	private ContactMediumEventListener listener;

	@BeforeEach
	void setUp() {
		meterRegistry = new SimpleMeterRegistry();
		listener = new ContactMediumEventListener(contactMediumEventHandler, meterRegistry, dltAlertNotifier);
	}

	@Test
	void onContactMediumEvent_delegatesToHandler() {
		ContactMediumEvent event = contactMediumCreatedEvent();

		listener.onContactMediumEvent(event);

		verify(contactMediumEventHandler).handle(event);
	}

	@Test
	void onContactMediumEventDlt_incrementsDltCounter() {
		ContactMediumEvent event = contactMediumCreatedEvent();

		listener.onContactMediumEventDlt(event, "SomeException", "boom");

		assertThat(meterRegistry.get("kafka.dlt.events").tag("eventType", event.type()).counter().count())
				.isEqualTo(1.0);
	}

	@Test
	void onContactMediumEventDlt_sendsAlert() {
		ContactMediumEvent event = contactMediumCreatedEvent();

		listener.onContactMediumEventDlt(event, "SomeException", "boom");

		verify(dltAlertNotifier).alert("customer-service", "ContactMediumEventListener", event.type(),
				"SomeException", "boom");
	}

	private ContactMediumEvent contactMediumCreatedEvent() {
		return new ContactMediumEvent(UUID.randomUUID(), ContactMediumEventTypes.CONTACT_MEDIUM_CREATED, 10L, 12L,
				4002L, "5551234567");
	}
}
