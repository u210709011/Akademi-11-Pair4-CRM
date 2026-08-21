package com.etiya.crm.partyservice.messaging;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.partyservice.business.abstracts.PartyRoleService;
import com.etiya.crm.shared.events.customer.CustomerDeletedEvent;
import com.etiya.crm.shared.events.customer.CustomerEventTypes;
import com.etiya.crm.shared.events.inbox.InboxEvent;
import com.etiya.crm.shared.events.inbox.InboxEventRepository;
import com.etiya.crm.shared.events.outbox.OutboxEventPublisher;
import com.etiya.crm.shared.events.saga.SagaStepResultEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerDeletedEventHandlerTest {

	@Mock
	private PartyRoleService partyRoleService;

	@Mock
	private InboxEventRepository inboxEventRepository;

	@Mock
	private OutboxEventPublisher outboxEventPublisher;

	@InjectMocks
	private CustomerDeletedEventHandler handler;

	@Test
	void handle_skipsProcessing_whenEventAlreadyInInbox() {
		CustomerDeletedEvent event = deletedEvent();
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(true);

		handler.handle(event);

		verify(partyRoleService, never()).deactivatePartyRole(any());
		verify(outboxEventPublisher, never()).publish(any(), any(), any(), any());
	}

	@Test
	void handle_deactivatesRole_savesInbox_andPublishesSuccessStepResult() {
		CustomerDeletedEvent event = deletedEvent();
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(false);

		handler.handle(event);

		verify(partyRoleService).deactivatePartyRole(20L);
		verify(inboxEventRepository).save(any(InboxEvent.class));

		ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
		verify(outboxEventPublisher).publish(any(), any(), any(), payloadCaptor.capture());
		SagaStepResultEvent published = (SagaStepResultEvent) payloadCaptor.getValue();
		assertThat(published.custId()).isEqualTo(10L);
		assertThat(published.success()).isTrue();
	}

	@Test
	void handle_reactivatesRole_whenCompensateCommand() {
		CustomerDeletedEvent event = new CustomerDeletedEvent(UUID.randomUUID(),
				CustomerEventTypes.CUSTOMER_DELETION_COMPENSATE, 10L, 20L, 12L);
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(false);

		handler.handle(event);

		verify(partyRoleService).reactivatePartyRole(20L);
		verify(inboxEventRepository).save(any(InboxEvent.class));
		verify(outboxEventPublisher, never()).publish(any(), any(), any(), any());
	}

	@Test
	void handle_ignoresUnknownEventType_withoutTouchingInbox() {
		CustomerDeletedEvent event = new CustomerDeletedEvent(UUID.randomUUID(), "CustomerOnboarded", 10L, 20L, 12L);

		handler.handle(event);

		verify(inboxEventRepository, never()).existsById(any());
		verify(partyRoleService, never()).deactivatePartyRole(any());
		verify(partyRoleService, never()).reactivatePartyRole(any());
	}

	@Test
	void handle_doesNotThrow_whenStepResultPublishFails() {
		CustomerDeletedEvent event = deletedEvent();
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(false);
		doThrow(new RuntimeException("boom")).when(outboxEventPublisher).publish(any(), any(), any(), any());

		handler.handle(event);

		verify(partyRoleService).deactivatePartyRole(20L);
	}

	private CustomerDeletedEvent deletedEvent() {
		return new CustomerDeletedEvent(UUID.randomUUID(), CustomerEventTypes.CUSTOMER_DELETED, 10L, 20L, 12L);
	}
}
