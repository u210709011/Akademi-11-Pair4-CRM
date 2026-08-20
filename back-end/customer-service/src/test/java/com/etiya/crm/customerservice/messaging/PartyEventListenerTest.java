package com.etiya.crm.customerservice.messaging;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

	@InjectMocks
	private PartyEventListener listener;

	@Test
	void onPartyEvent_delegatesToHandler() {
		PartyEvent event = new PartyEvent(UUID.randomUUID(), PartyEventTypes.INDIVIDUAL_PARTY_CREATED, 10L, "Ahmet",
				"Can", "Yilmaz", "10000000146", 2001L);

		listener.onPartyEvent(event);

		verify(partyEventHandler).handle(event);
	}

	@Test
	void onPartyEventDlt_incrementsDltCounter() {
		SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
		PartyEventListener dltListener = new PartyEventListener(partyEventHandler, meterRegistry);
		PartyEvent event = new PartyEvent(UUID.randomUUID(), PartyEventTypes.INDIVIDUAL_PARTY_CREATED, 10L, "Ahmet",
				"Can", "Yilmaz", "10000000146", 2001L);

		dltListener.onPartyEventDlt(event, "SomeException", "boom");

		assertThat(meterRegistry.get("kafka.dlt.events").tag("eventType", event.type()).counter().count())
				.isEqualTo(1.0);
	}
}
