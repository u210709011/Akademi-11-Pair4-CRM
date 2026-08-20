package com.etiya.crm.customerservice.messaging;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.etiya.crm.customerservice.business.abstracts.LookupCacheService;
import com.etiya.crm.customerservice.constants.LogMessages;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerRepository;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerSearchViewRepository;
import com.etiya.crm.customerservice.entities.concretes.Customer;
import com.etiya.crm.customerservice.entities.concretes.CustomerSearchView;
import com.etiya.crm.shared.events.inbox.InboxEvent;
import com.etiya.crm.shared.events.inbox.InboxEventRepository;
import com.etiya.crm.shared.events.party.PartyEvent;
import com.etiya.crm.shared.events.party.PartyEventTypes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Party event'leriyle müşteri arama görünümünü günceller. */
@Slf4j
@Component
@RequiredArgsConstructor
public class PartyEventHandler {

	private final InboxEventRepository inboxEventRepository;
	private final CustomerRepository customerRepository;
	private final CustomerSearchViewRepository customerSearchViewRepository;
	private final LookupCacheService lookupCacheService;

	@Transactional
	/** Event'i bir kez işler ve ilgili read-model'i günceller. */
	public void handle(PartyEvent event) {
		
		if (inboxEventRepository.existsById(event.eventId())) {
			return;
		}
		inboxEventRepository.save(InboxEvent.of(event.eventId(), event.type()));

		if (PartyEventTypes.INDIVIDUAL_PARTY_CREATED.equals(event.type())
				|| PartyEventTypes.INDIVIDUAL_UPDATED.equals(event.type())) {
			syncSearchView(event);
		} else {
			log.warn(LogMessages.UNKNOWN_PARTY_EVENT_TYPE, event.type());
		}
	}

	private void syncSearchView(PartyEvent event) {
		// Party rolü ancak yerel müşteri kaydı varsa arama görünümüne yazılır.
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
		view.setMiddleName(event.middleName());
		view.setLastName(event.lastName());
		view.setTcNo(event.nationalId());
		view.setRole(resolveRole(event.partyRoleTypeId()));
		view.setPartyRoleTypeId(event.partyRoleTypeId());
		view.setDeleted(!customer.isActive());
		customerSearchViewRepository.save(view);
	}

	private String resolveRole(Long partyRoleTypeId) {
		// Rol adı response içinde gösterilmek üzere lookup-service'den çevrilir.
		if (partyRoleTypeId == null) {
			return null;
		}
		try {
			return lookupCacheService.resolveTypeValue(partyRoleTypeId);
		} catch (RuntimeException ex) {
			log.warn(LogMessages.LOOKUP_SERVICE_CALL_FAILED, ex.getMessage());
			throw new LookupServiceUnavailableException(
					"Could not resolve PARTY_ROLE_TYPE/" + partyRoleTypeId + " from lookup-service", ex);
		}
	}
}
