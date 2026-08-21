package com.etiya.crm.customerservice.messaging;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.customerservice.business.abstracts.CustomerDeletionSagaOrchestrator;
import com.etiya.crm.shared.events.inbox.InboxEvent;
import com.etiya.crm.shared.events.inbox.InboxEventRepository;
import com.etiya.crm.shared.events.saga.SagaEventTypes;
import com.etiya.crm.shared.events.saga.SagaStepNames;
import com.etiya.crm.shared.events.saga.SagaStepResultEvent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SagaStepResultEventHandlerTest {

	@Mock
	private InboxEventRepository inboxEventRepository;

	@Mock
	private CustomerDeletionSagaOrchestrator orchestrator;

	@InjectMocks
	private SagaStepResultEventHandler handler;

	@Test
	void handle_skipsProcessing_whenEventAlreadyInInbox() {
		SagaStepResultEvent event = stepFailedEvent();
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(true);

		handler.handle(event);

		verify(orchestrator, never()).handleStepResult(any(), any(), anyBoolean(), any());
	}

	@Test
	void handle_savesInboxAndDelegatesToOrchestrator() {
		SagaStepResultEvent event = stepFailedEvent();
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(false);

		handler.handle(event);

		verify(inboxEventRepository).save(any(InboxEvent.class));
		verify(orchestrator).handleStepResult(event.custId(), event.stepName(), event.success(), event.reason());
	}

	private SagaStepResultEvent stepFailedEvent() {
		return new SagaStepResultEvent(UUID.randomUUID(), SagaEventTypes.STEP_FAILED, 10L,
				SagaStepNames.PARTY_DEACTIVATION, false, "boom");
	}
}
