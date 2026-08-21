package com.etiya.crm.customerservice.messaging;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.etiya.crm.customerservice.business.abstracts.CustomerDeletionSagaOrchestrator;
import com.etiya.crm.shared.events.inbox.InboxEvent;
import com.etiya.crm.shared.events.inbox.InboxEventRepository;
import com.etiya.crm.shared.events.saga.SagaStepResultEvent;

import lombok.RequiredArgsConstructor;

/** SagaStepResultEvent'i isleyen broker-bagimsiz mantik: idempotency + orchestrator'a devretme. */
@Component
@RequiredArgsConstructor
public class SagaStepResultEventHandler {

	private final InboxEventRepository inboxEventRepository;
	private final CustomerDeletionSagaOrchestrator orchestrator;

	@Transactional
	public void handle(SagaStepResultEvent event) {
		if (inboxEventRepository.existsById(event.eventId())) {
			return;
		}
		inboxEventRepository.save(InboxEvent.of(event.eventId(), event.type()));
		orchestrator.handleStepResult(event.custId(), event.stepName(), event.success(), event.reason());
	}
}
