package com.etiya.crm.customerservice.messaging;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.etiya.crm.customerservice.constants.KafkaTopics;
import com.etiya.crm.customerservice.constants.LogMessages;
import com.etiya.crm.customerservice.constants.PartyEventTypes;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerRepository;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerSearchViewRepository;
import com.etiya.crm.customerservice.dataAccess.abstracts.InboxEventRepository;
import com.etiya.crm.customerservice.entities.concretes.Customer;
import com.etiya.crm.customerservice.entities.concretes.CustomerSearchView;
import com.etiya.crm.customerservice.events.PartyEvent;
import com.etiya.crm.customerservice.inbox.InboxEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * party-service'in party-events topic'ine yayinladigi event'leri dinler ve
 * CUSTOMER_SEARCH_VIEW'i gunceller. Onboarding sirasinda search view zaten
 * dogrudan CustomerService.onboard() icinde yazilir; bu dinleyici sadece
 * onboarding disinda (ör. party-service'te dogrudan yapilan) degisikliklerin
 * senkron kalmasini saglar. INBOX ile idempotent calisir; kalici hatalarda
 * 4 deneme sonrasi "party-events-dlt" topic'ine dusurulur (DLQ).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PartyEventListener {

	private final InboxEventRepository inboxEventRepository;
	private final CustomerRepository customerRepository;
	private final CustomerSearchViewRepository customerSearchViewRepository;

	@RetryableTopic(
			attempts = "4",
			backoff = @Backoff(delay = 1000, multiplier = 2.0),
			dltTopicSuffix = "-dlt",
			include = Exception.class)
	@KafkaListener(topics = KafkaTopics.PARTY_EVENTS, groupId = "customer-service")
	@Transactional
	public void onPartyEvent(PartyEvent event) {
		UUID eventId = UUID.fromString(event.eventId());
		if (inboxEventRepository.existsById(eventId)) {
			return; // idempotency: ayni event tekrar teslim edilirse islenmez.
		}
		inboxEventRepository.save(InboxEvent.of(eventId, event.type()));

		if (PartyEventTypes.INDIVIDUAL_PARTY_CREATED.equals(event.type())
				|| PartyEventTypes.INDIVIDUAL_UPDATED.equals(event.type())) {
			syncSearchView(event);
		} else {
			log.warn(LogMessages.UNKNOWN_PARTY_EVENT_TYPE, event.type());
		}
	}

	private void syncSearchView(PartyEvent event) {
		customerRepository.findByPartyRoleId(event.partyRoleId()).ifPresentOrElse(
				customer -> upsertSearchView(customer, event),
				() -> log.warn(LogMessages.PARTY_EVENT_CUSTOMER_NOT_FOUND, event.partyRoleId()));
	}

	private void upsertSearchView(Customer customer, PartyEvent event) {
		CustomerSearchView view = customerSearchViewRepository.findById(customer.getCustId())
				.orElseGet(CustomerSearchView::new);
		view.setCustId(customer.getCustId());
		view.setPartyRoleId(event.partyRoleId());
		view.setFirstName(event.firstName());
		view.setLastName(event.lastName());
		view.setTcNo(event.nationalId());
		view.setDeleted(!customer.isActive());
		customerSearchViewRepository.save(view);
	}
}
