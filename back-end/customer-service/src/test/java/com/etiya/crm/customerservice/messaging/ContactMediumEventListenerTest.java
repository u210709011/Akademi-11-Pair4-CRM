package com.etiya.crm.customerservice.messaging;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.shared.events.contactmedium.ContactMediumEvent;
import com.etiya.crm.shared.events.contactmedium.ContactMediumEventTypes;

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

	@InjectMocks
	private ContactMediumEventListener listener;

	@Test
	void onContactMediumEvent_delegatesToHandler() {
		ContactMediumEvent event = new ContactMediumEvent(UUID.randomUUID(),
				ContactMediumEventTypes.CONTACT_MEDIUM_CREATED, 10L, 12L, 4002L, "5551234567");

		listener.onContactMediumEvent(event);

		verify(contactMediumEventHandler).handle(event);
	}
}
