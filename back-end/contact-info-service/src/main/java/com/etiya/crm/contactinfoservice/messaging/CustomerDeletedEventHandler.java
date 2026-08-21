package com.etiya.crm.contactinfoservice.messaging;

import java.util.UUID;

import com.etiya.crm.contactinfoservice.business.abstracts.AddressService;
import com.etiya.crm.contactinfoservice.business.abstracts.ContactMediumService;
import com.etiya.crm.contactinfoservice.constants.LogMessages;
import com.etiya.crm.shared.events.KafkaTopics;
import com.etiya.crm.shared.events.customer.CustomerDeletedEvent;
import com.etiya.crm.shared.events.customer.CustomerEventTypes;
import com.etiya.crm.shared.events.inbox.InboxEvent;
import com.etiya.crm.shared.events.inbox.InboxEventRepository;
import com.etiya.crm.shared.events.outbox.OutboxEventPublisher;
import com.etiya.crm.shared.events.saga.SagaEventTypes;
import com.etiya.crm.shared.events.saga.SagaStepNames;
import com.etiya.crm.shared.events.saga.SagaStepResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** CustomerDeletedEvent'i isleyen broker-bagimsiz mantik: idempotency + deactivate/reactivate. */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerDeletedEventHandler {

	private final AddressService addressService;
	private final ContactMediumService contactMediumService;
	private final InboxEventRepository inboxEventRepository;
	private final OutboxEventPublisher outboxEventPublisher;

	@Transactional
	/** Aynı event'in tekrar işlenmesini Inbox kaydıyla engeller. */
	public void handle(CustomerDeletedEvent event) {
		boolean isDelete = CustomerEventTypes.CUSTOMER_DELETED.equals(event.type());
		boolean isCompensate = CustomerEventTypes.CUSTOMER_DELETION_COMPENSATE.equals(event.type());
		if (!isDelete && !isCompensate) {
			return;
		}

		if (inboxEventRepository.existsById(event.eventId())) {
			log.info(LogMessages.CUSTOMER_EVENT_ALREADY_PROCESSED, event.custId());
			return;
		}

		if (isDelete) {
			addressService.deactivateAllForRow(event.custId(), event.dataTypeId());
			contactMediumService.deactivateAllForRow(event.custId(), event.dataTypeId());
			inboxEventRepository.save(InboxEvent.of(event.eventId(), CustomerEventTypes.CUSTOMER_DELETED));
			publishStepResult(event.custId(), true, null);
		} else {
			addressService.reactivateAllForRow(event.custId(), event.dataTypeId());
			contactMediumService.reactivateAllForRow(event.custId(), event.dataTypeId());
			inboxEventRepository.save(InboxEvent.of(event.eventId(), CustomerEventTypes.CUSTOMER_DELETION_COMPENSATE));
		}
	}

	/** Adim sonucunu orchestrator'a bildirir. */
	private void publishStepResult(Long custId, boolean success, String reason) {
		try {
			outboxEventPublisher.publish(KafkaTopics.CUSTOMER_DELETION_SAGA_AGGREGATE_TYPE, custId.toString(),
					SagaEventTypes.STEP_SUCCEEDED,
					new SagaStepResultEvent(UUID.randomUUID(), SagaEventTypes.STEP_SUCCEEDED, custId,
							SagaStepNames.CONTACT_INFO_DEACTIVATION, success, reason));
		} catch (Exception ex) {
			log.error(LogMessages.SAGA_STEP_RESULT_PUBLISH_FAILED, custId, SagaStepNames.CONTACT_INFO_DEACTIVATION, success, ex);
		}
	}
}
