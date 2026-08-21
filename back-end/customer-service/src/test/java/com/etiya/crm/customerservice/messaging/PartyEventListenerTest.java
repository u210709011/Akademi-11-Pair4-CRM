package com.etiya.crm.customerservice.messaging;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.shared.contracts.messaging.DltAlertNotifier;
import com.etiya.crm.shared.events.party.PartyEvent;
import com.etiya.crm.shared.events.party.PartyEventTypes;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Bu test sadece adapter'in kendi sorumlulugunu (event'i PartyEventHandler'a
 * devretmek) dogrular - is mantigi (idempotency, search view senkronu) artik
 * PartyEventHandlerTest'te.
 */
@ExtendWith(MockitoExtension.class)
class PartyEventListenerTest {

	@Mock
	private PartyEventHandler partyEventHandler;

	@Mock
	private DltAlertNotifier dltAlertNotifier;

	private SimpleMeterRegistry meterRegistry;
	private PartyEventListener listener;

	@BeforeEach
	void setUp() {
		meterRegistry = new SimpleMeterRegistry();
		listener = new PartyEventListener(partyEventHandler, meterRegistry, dltAlertNotifier);
	}

	@Test
	void onPartyEvent_delegatesToHandler() {
		PartyEvent event = individualCreatedEvent();

		listener.onPartyEvent(event);

		verify(partyEventHandler).handle(event);
	}

	@Test
	void onPartyEventDlt_incrementsDltCounter() {
		PartyEvent event = individualCreatedEvent();

		listener.onPartyEventDlt(event, "SomeException", "boom");

		assertThat(meterRegistry.get("kafka.dlt.events").tag("eventType", event.type()).counter().count())
				.isEqualTo(1.0);
	}

	@Test
	void onPartyEventDlt_sendsAlert() {
		PartyEvent event = individualCreatedEvent();

		listener.onPartyEventDlt(event, "SomeException", "boom");

		verify(dltAlertNotifier).alert("customer-service", "PartyEventListener", event.type(), "SomeException",
				"boom");
	}

	private PartyEvent individualCreatedEvent() {
		return new PartyEvent(UUID.randomUUID(), PartyEventTypes.INDIVIDUAL_PARTY_CREATED, 10L, "Ahmet", "Can",
				"Yilmaz", "10000000146", 2001L);
	}
}
