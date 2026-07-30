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

/**
 * CustomerDeletedEvent'i isleyen broker-bagimsiz mantik: idempotency kontrolu +
 * musteriye ait adres/iletisim kayitlarini pasife cekme (AS-002: silme = statu
 * guncellemesi). Kafka'ya ozgu hicbir sey icermez - "CustomerEventListener"
 * (deserialize/parse dahil) bu sinifi cagiran ince bir adapter'dir. Broker
 * degisirse sadece adapter yeniden yazilir, bu sinifa dokunulmaz.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerDeletedEventHandler {

	private final AddressService addressService;
	private final ContactMediumService contactMediumService;
	private final InboxEventRepository inboxEventRepository;

	@Transactional
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
