package com.etiya.crm.contactinfoservice.messaging;

import java.util.UUID;

import com.etiya.crm.contactinfoservice.business.abstracts.AddressService;
import com.etiya.crm.contactinfoservice.business.abstracts.ContactMediumService;
import com.etiya.crm.shared.events.customer.CustomerDeletedEvent;
import com.etiya.crm.shared.events.customer.CustomerEventTypes;
import com.etiya.crm.shared.events.inbox.InboxEvent;
import com.etiya.crm.shared.events.inbox.InboxEventRepository;
import com.etiya.crm.shared.events.outbox.OutboxEventPublisher;
import com.etiya.crm.shared.events.saga.SagaStepResultEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerDeletedEventHandlerTest {

	private static final Long CUSTOMER_DATA_TYPE_ID = 12L;

	@Mock
	private AddressService addressService;

	@Mock
	private ContactMediumService contactMediumService;

	@Mock
	private InboxEventRepository inboxEventRepository;

	@Mock
	private OutboxEventPublisher outboxEventPublisher;

	@InjectMocks
	private CustomerDeletedEventHandler handler;

	@Test
	void handle_deactivatesAddressAndContactMedium_whenCustomerDeletedEventReceived() {
		CustomerDeletedEvent event = customerDeletedEvent();
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(false);

		handler.handle(event);

		verify(addressService).deactivateAllForRow(10L, CUSTOMER_DATA_TYPE_ID);
		verify(contactMediumService).deactivateAllForRow(10L, CUSTOMER_DATA_TYPE_ID);
		verify(inboxEventRepository).save(any(InboxEvent.class));

		ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
		verify(outboxEventPublisher).publish(any(), any(), any(), payloadCaptor.capture());
		SagaStepResultEvent published = (SagaStepResultEvent) payloadCaptor.getValue();
		assertThat(published.custId()).isEqualTo(10L);
		assertThat(published.success()).isTrue();
	}

	@Test
	void handle_reactivatesAddressAndContactMedium_whenCompensateCommand() {
		CustomerDeletedEvent event = new CustomerDeletedEvent(UUID.randomUUID(),
				CustomerEventTypes.CUSTOMER_DELETION_COMPENSATE, 10L, 20L, CUSTOMER_DATA_TYPE_ID);
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(false);

		handler.handle(event);

		verify(addressService).reactivateAllForRow(10L, CUSTOMER_DATA_TYPE_ID);
		verify(contactMediumService).reactivateAllForRow(10L, CUSTOMER_DATA_TYPE_ID);
		verify(inboxEventRepository).save(any(InboxEvent.class));
		verify(outboxEventPublisher, never()).publish(any(), any(), any(), any());
	}

	@Test
	void handle_doesNotThrow_whenStepResultPublishFails() {
		CustomerDeletedEvent event = customerDeletedEvent();
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(false);
		doThrow(new RuntimeException("boom")).when(outboxEventPublisher).publish(any(), any(), any(), any());

		handler.handle(event);

		verify(addressService).deactivateAllForRow(10L, CUSTOMER_DATA_TYPE_ID);
	}

	@Test
	void handle_skips_whenEventAlreadyProcessed() {
		CustomerDeletedEvent event = customerDeletedEvent();
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(true);

		handler.handle(event);

		verify(addressService, never()).deactivateAllForRow(anyLong(), anyLong());
		verify(contactMediumService, never()).deactivateAllForRow(anyLong(), anyLong());
		verify(inboxEventRepository, never()).save(any());
	}

	@Test
	void handle_skips_whenEventTypeIsNotCustomerDeleted() {
		CustomerDeletedEvent event = new CustomerDeletedEvent(UUID.randomUUID(), "CustomerOnboarded", 10L, 20L,
				CUSTOMER_DATA_TYPE_ID);

		handler.handle(event);

		verify(addressService, never()).deactivateAllForRow(anyLong(), anyLong());
		verify(inboxEventRepository, never()).existsById(any());
	}

	private CustomerDeletedEvent customerDeletedEvent() {
		return new CustomerDeletedEvent(UUID.randomUUID(), CustomerEventTypes.CUSTOMER_DELETED, 10L, 20L,
				CUSTOMER_DATA_TYPE_ID);
	}
}
