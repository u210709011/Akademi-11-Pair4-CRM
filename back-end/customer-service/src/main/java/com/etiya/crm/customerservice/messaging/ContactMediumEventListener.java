package com.etiya.crm.customerservice.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.etiya.crm.customerservice.business.abstracts.LookupCacheService;
import com.etiya.crm.customerservice.constants.LogMessages;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerSearchViewRepository;
import com.etiya.crm.shared.contracts.lookup.DataTypeIds;
import com.etiya.crm.shared.contracts.lookup.LookupCodes;
import com.etiya.crm.shared.contracts.lookup.LookupGroups;
import com.etiya.crm.shared.events.KafkaTopics;
import com.etiya.crm.shared.events.contactmedium.ContactMediumEvent;
import com.etiya.crm.shared.events.contactmedium.ContactMediumEventTypes;
import com.etiya.crm.shared.events.inbox.InboxEvent;
import com.etiya.crm.shared.events.inbox.InboxEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * contact-info-service'in yayinladigi "contact-medium-events" topic'ini
 * dinler ve CUSTOMER_SEARCH_VIEW.gsm'i gunceller. contact-info-service her
 * CNTC_MEDIUM mutasyonunu (rowId/dataTypeId'nin kime ait oldugunu bilmeden,
 * genericttir) yayinlar - dataTypeId=CUST(102) + cntcMediumTypeId=MOBILE_PHONE
 * filtrelemesi burada, tuketici tarafinda yapilir (bkz. PartyEventListener'daki
 * ayni prensip). INBOX ile idempotent calisir; kalici hatalarda 4 deneme
 * sonrasi "contact-medium-events-dlt" topic'ine dusurulur (DLQ).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContactMediumEventListener {

	private final InboxEventRepository inboxEventRepository;
	private final CustomerSearchViewRepository customerSearchViewRepository;
	private final LookupCacheService lookupCacheService;

	@RetryableTopic(
			attempts = "4",
			backoff = @Backoff(delay = 1000, multiplier = 2.0),
			dltTopicSuffix = "-dlt",
			include = Exception.class)
	@KafkaListener(topics = KafkaTopics.CONTACT_MEDIUM_EVENTS, groupId = "customer-service",
			containerFactory = "contactMediumKafkaListenerContainerFactory")
	@Transactional
	public void onContactMediumEvent(ContactMediumEvent event) {
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

	/** DATA_TYPE=CUST sabit kontrattir (bkz. DataTypeIds), MOBILE_PHONE ise lookup-service'ten cozulur. */
	private boolean isCustomerMobilePhone(ContactMediumEvent event) {
		Long mobilePhoneTypeId;
		try {
			mobilePhoneTypeId = lookupCacheService.resolveId(LookupGroups.CONTACT_MEDIUM_TYPE,
					LookupCodes.CONTACT_MEDIUM_MOBILE_PHONE);
		} catch (RuntimeException ex) {
			log.warn(LogMessages.LOOKUP_SERVICE_CALL_FAILED, ex.getMessage());
			throw new LookupServiceUnavailableException(
					"Could not resolve CNTC_MEDIUM_TYPE/MOBILE_PHONE from lookup-service", ex);
		}
		return DataTypeIds.CUSTOMER.equals(event.dataTypeId()) && mobilePhoneTypeId.equals(event.cntcMediumTypeId());
	}

	private void syncGsm(Long custId, String gsm) {
		customerSearchViewRepository.findById(custId).ifPresentOrElse(view -> {
			view.setGsm(gsm);
			customerSearchViewRepository.save(view);
		}, () -> log.warn(LogMessages.CONTACT_MEDIUM_EVENT_CUSTOMER_NOT_FOUND, custId));
	}
}
