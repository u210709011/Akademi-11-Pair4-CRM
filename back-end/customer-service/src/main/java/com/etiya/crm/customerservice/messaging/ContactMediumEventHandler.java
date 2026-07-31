package com.etiya.crm.customerservice.messaging;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.etiya.crm.customerservice.business.abstracts.LookupCacheService;
import com.etiya.crm.customerservice.constants.LogMessages;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerSearchViewRepository;
import com.etiya.crm.shared.contracts.gnltp.GnlTpCodes;
import com.etiya.crm.shared.contracts.gnltp.GnlTpGroups;
import com.etiya.crm.shared.contracts.typevalue.TypeValueTables;
import com.etiya.crm.shared.events.contactmedium.ContactMediumEvent;
import com.etiya.crm.shared.events.contactmedium.ContactMediumEventTypes;
import com.etiya.crm.shared.events.inbox.InboxEvent;
import com.etiya.crm.shared.events.inbox.InboxEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ContactMediumEvent'i isleyen broker-bagimsiz mantik: idempotency kontrolu +
 * CUSTOMER_SEARCH_VIEW.gsm senkronu. Kafka'ya ozgu hicbir sey icermez -
 * "ContactMediumEventListener" bu sinifi cagiran ince bir adapter'dir. Broker
 * degisirse (orn. RabbitMQ) sadece adapter yeniden yazilir, bu sinifa dokunulmaz.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContactMediumEventHandler {

	private final InboxEventRepository inboxEventRepository;
	private final CustomerSearchViewRepository customerSearchViewRepository;
	private final LookupCacheService lookupCacheService;

	@Transactional
	public void handle(ContactMediumEvent event) {
		if (inboxEventRepository.existsById(event.eventId())) {
			return; // idempotency: ayni event tekrar teslim edilirse islenmez.
		}
		inboxEventRepository.save(InboxEvent.of(event.eventId(), event.type()));

		if (!isCustomerMobilePhone(event)) {
			return;
		}

		if (ContactMediumEventTypes.CONTACT_MEDIUM_CREATED.equals(event.type())
				|| ContactMediumEventTypes.CONTACT_MEDIUM_UPDATED.equals(event.type())) {
			syncGsm(event.rowId(), event.cntcData());
		} else if (ContactMediumEventTypes.CONTACT_MEDIUM_DEACTIVATED.equals(event.type())) {
			syncGsm(event.rowId(), null);
		} else {
			log.warn(LogMessages.UNKNOWN_CONTACT_MEDIUM_EVENT_TYPE, event.type());
		}
	}

	/** Hicbir lookup ID'si hardcode edilmez - hem CUST'un data-type id'si hem MOBILE_PHONE'un
	 * medium-type id'si lookup-service'ten cozulur (ikisi de Caffeine'de cache'lidir). */
	private boolean isCustomerMobilePhone(ContactMediumEvent event) {
		Long customerDataTypeId;
		Long mobilePhoneTypeId;
		try {
			customerDataTypeId = lookupCacheService.resolveDataTypeId(TypeValueTables.CUSTOMER);
			mobilePhoneTypeId = lookupCacheService.resolveTypeId(GnlTpGroups.CONTACT_MEDIUM, GnlTpCodes.MOBILE);
		} catch (RuntimeException ex) {
			log.warn(LogMessages.LOOKUP_SERVICE_CALL_FAILED, ex.getMessage());
			throw new LookupServiceUnavailableException(
					"Could not resolve CUST data-type or CNTC_MEDIUM_TYPE/MOBILE_PHONE from lookup-service", ex);
		}
		return customerDataTypeId.equals(event.dataTypeId()) && mobilePhoneTypeId.equals(event.cntcMediumTypeId());
	}

	private void syncGsm(Long custId, String gsm) {
		customerSearchViewRepository.findById(custId).ifPresentOrElse(view -> {
			view.setGsm(gsm);
			customerSearchViewRepository.save(view);
		}, () -> log.warn(LogMessages.CONTACT_MEDIUM_EVENT_CUSTOMER_NOT_FOUND, custId));
	}
}
