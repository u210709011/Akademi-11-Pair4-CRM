package com.etiya.crm.partyservice.messaging;

import com.etiya.crm.partyservice.business.abstracts.PartyRoleService;
import com.etiya.crm.partyservice.constants.LogMessages;
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
 * partyRoleId'yi pasiflestirme. Kafka'ya (ya da baska bir mesajlasma araci)
 * ozgu hicbir sey icermez - "CustomerEventListener" bu sinifi cagiran ince bir
 * adapter'dir. Broker degisirse (orn. RabbitMQ) sadece adapter yeniden yazilir,
 * bu sinifa dokunulmaz.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerDeletedEventHandler {

	private final PartyRoleService partyRoleService;
	private final InboxEventRepository inboxEventRepository;

	@Transactional
	public void handle(CustomerDeletedEvent event) {
		if (!CustomerEventTypes.CUSTOMER_DELETED.equals(event.type())) {
			return;
		}

		if (inboxEventRepository.existsById(event.eventId())) {
			log.info(LogMessages.CUSTOMER_EVENT_ALREADY_PROCESSED, event.partyRoleId());
			return;
		}

		partyRoleService.deactivatePartyRole(event.partyRoleId());
		inboxEventRepository.save(InboxEvent.of(event.eventId(), event.type()));
	}
}
