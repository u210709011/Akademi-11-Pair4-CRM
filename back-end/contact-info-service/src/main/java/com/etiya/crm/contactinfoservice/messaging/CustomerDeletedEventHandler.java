package com.etiya.crm.contactinfoservice.messaging;

import com.etiya.crm.contactinfoservice.business.abstracts.AddressService;
import com.etiya.crm.contactinfoservice.business.abstracts.ContactMediumService;
import com.etiya.crm.contactinfoservice.constants.LogMessages;
import com.etiya.crm.shared.events.customer.CustomerDeletedEvent;
import com.etiya.crm.shared.events.customer.CustomerEventTypes;
import com.etiya.crm.shared.events.inbox.InboxEvent;
import com.etiya.crm.shared.events.inbox.InboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
/** Müşteri silme event'i geldiğinde iletişim verilerini pasifleştirir. */
public class CustomerDeletedEventHandler {

	private final AddressService addressService;
	private final ContactMediumService contactMediumService;
	private final InboxEventRepository inboxEventRepository;

	@Transactional
	/** Aynı event'in tekrar işlenmesini Inbox kaydıyla engeller. */
	public void handle(CustomerDeletedEvent event) {
		if (!CustomerEventTypes.CUSTOMER_DELETED.equals(event.type())) {
			return;
		}

		if (inboxEventRepository.existsById(event.eventId())) {
			log.info(LogMessages.CUSTOMER_EVENT_ALREADY_PROCESSED, event.custId());
			return;
		}

		addressService.deactivateAllForRow(event.custId(), event.dataTypeId());
		contactMediumService.deactivateAllForRow(event.custId(), event.dataTypeId());
		inboxEventRepository.save(InboxEvent.of(event.eventId(), CustomerEventTypes.CUSTOMER_DELETED));
	}
}
