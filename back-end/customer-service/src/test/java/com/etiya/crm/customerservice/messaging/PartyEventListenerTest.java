package com.etiya.crm.customerservice.messaging;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.customerservice.business.abstracts.LookupCacheService;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerRepository;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerSearchViewRepository;
import com.etiya.crm.customerservice.entities.concretes.Customer;
import com.etiya.crm.customerservice.entities.concretes.CustomerSearchView;
import com.etiya.crm.shared.events.inbox.InboxEvent;
import com.etiya.crm.shared.events.party.PartyEvent;
import com.etiya.crm.shared.events.party.PartyEventTypes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartyEventListenerTest {

	@Mock
	private com.etiya.crm.shared.events.inbox.InboxEventRepository inboxEventRepository;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private CustomerSearchViewRepository customerSearchViewRepository;

	@Mock
	private LookupCacheService lookupCacheService;

	@InjectMocks
	private PartyEventListener listener;

	@Test
	void onPartyEvent_skipsProcessing_whenEventAlreadyInInbox() {
		PartyEvent event = individualCreatedEvent();
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(true);

		listener.onPartyEvent(event);

		verify(customerRepository, never()).findByPartyRoleId(any());
		verify(customerSearchViewRepository, never()).save(any());
	}

	@Test
	void onPartyEvent_skipsSync_whenCustomerNotFound() {
		PartyEvent event = individualCreatedEvent();
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(false);
		when(customerRepository.findByPartyRoleId(event.partyRoleId())).thenReturn(Optional.empty());

		listener.onPartyEvent(event);

		// InboxEvent'te equals() override yok (Lombok @Getter/@Setter tek basina uretmez),
		// bu yuzden yeni bir InboxEvent.of(...) ile referans karsilastirmasi hicbir zaman
		// eslesmez - any(InboxEvent.class) ile "bir kayit islendi olarak saklandi" dogrulanir.
		verify(inboxEventRepository).save(any(InboxEvent.class));
		verify(customerSearchViewRepository, never()).save(any());
	}

	@Test
	void onPartyEvent_ignoresUnknownEventType() {
		PartyEvent event = new PartyEvent(UUID.randomUUID(), "SomethingElse", 10L, "Ahmet", "Can", "Yilmaz",
				"10000000146", 2001L);
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(false);

		listener.onPartyEvent(event);

		verify(customerRepository, never()).findByPartyRoleId(any());
	}

	@Test
	void onPartyEvent_upsertsNewSearchView_withRoleResolved() {
		PartyEvent event = individualCreatedEvent();
		Customer customer = activeCustomer(42L, event.partyRoleId());
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(false);
		when(customerRepository.findByPartyRoleId(event.partyRoleId())).thenReturn(Optional.of(customer));
		when(customerSearchViewRepository.findById(42L)).thenReturn(Optional.empty());
		when(lookupCacheService.resolveTypeValue(event.partyRoleTypeId()))
				.thenReturn("Musteri");

		listener.onPartyEvent(event);

		ArgumentCaptor<CustomerSearchView> captor = ArgumentCaptor.forClass(CustomerSearchView.class);
		verify(customerSearchViewRepository).save(captor.capture());
		CustomerSearchView saved = captor.getValue();
		assertThat(saved.getCustId()).isEqualTo(42L);
		assertThat(saved.getPartyRoleId()).isEqualTo(event.partyRoleId());
		assertThat(saved.getFirstName()).isEqualTo("Ahmet");
		assertThat(saved.getMiddleName()).isEqualTo("Can");
		assertThat(saved.getLastName()).isEqualTo("Yilmaz");
		assertThat(saved.getTcNo()).isEqualTo("10000000146");
		assertThat(saved.getRole()).isEqualTo("Musteri");
		assertThat(saved.isDeleted()).isFalse();
	}

	@Test
	void onPartyEvent_updatesExistingSearchView_inPlace() {
		PartyEvent event = individualCreatedEvent();
		Customer customer = activeCustomer(42L, event.partyRoleId());
		CustomerSearchView existing = new CustomerSearchView();
		existing.setCustId(42L);
		existing.setFirstName("Old");
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(false);
		when(customerRepository.findByPartyRoleId(event.partyRoleId())).thenReturn(Optional.of(customer));
		when(customerSearchViewRepository.findById(42L)).thenReturn(Optional.of(existing));
		when(lookupCacheService.resolveTypeValue(event.partyRoleTypeId()))
				.thenReturn("Musteri");

		listener.onPartyEvent(event);

		verify(customerSearchViewRepository).save(existing);
		assertThat(existing.getFirstName()).isEqualTo("Ahmet");
	}

	@Test
	void onPartyEvent_marksSearchViewDeleted_whenCustomerInactive() {
		PartyEvent event = individualCreatedEvent();
		Customer inactiveCustomer = activeCustomer(42L, event.partyRoleId());
		inactiveCustomer.setActive(false);
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(false);
		when(customerRepository.findByPartyRoleId(event.partyRoleId())).thenReturn(Optional.of(inactiveCustomer));
		when(customerSearchViewRepository.findById(42L)).thenReturn(Optional.empty());
		when(lookupCacheService.resolveTypeValue(event.partyRoleTypeId()))
				.thenReturn("Musteri");

		listener.onPartyEvent(event);

		ArgumentCaptor<CustomerSearchView> captor = ArgumentCaptor.forClass(CustomerSearchView.class);
		verify(customerSearchViewRepository).save(captor.capture());
		assertThat(captor.getValue().isDeleted()).isTrue();
	}

	@Test
	void onPartyEvent_leavesRoleNull_whenPartyRoleTypeIdMissing() {
		PartyEvent event = new PartyEvent(UUID.randomUUID(), PartyEventTypes.INDIVIDUAL_UPDATED, 10L, "Ahmet", "Can",
				"Yilmaz", "10000000146", null);
		Customer customer = activeCustomer(42L, event.partyRoleId());
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(false);
		when(customerRepository.findByPartyRoleId(event.partyRoleId())).thenReturn(Optional.of(customer));
		when(customerSearchViewRepository.findById(42L)).thenReturn(Optional.empty());

		listener.onPartyEvent(event);

		ArgumentCaptor<CustomerSearchView> captor = ArgumentCaptor.forClass(CustomerSearchView.class);
		verify(customerSearchViewRepository).save(captor.capture());
		assertThat(captor.getValue().getRole()).isNull();
		verify(lookupCacheService, never()).resolveTypeValue(any());
	}

	@Test
	void onPartyEvent_throwsLookupServiceUnavailable_whenLookupCacheFails() {
		PartyEvent event = individualCreatedEvent();
		Customer customer = activeCustomer(42L, event.partyRoleId());
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(false);
		when(customerRepository.findByPartyRoleId(event.partyRoleId())).thenReturn(Optional.of(customer));
		when(customerSearchViewRepository.findById(42L)).thenReturn(Optional.empty());
		when(lookupCacheService.resolveTypeValue(event.partyRoleTypeId()))
				.thenThrow(new RuntimeException("401 Unauthorized"));

		assertThatThrownBy(() -> listener.onPartyEvent(event))
				.isInstanceOf(LookupServiceUnavailableException.class);

		verify(customerSearchViewRepository, never()).save(any());
	}

	private PartyEvent individualCreatedEvent() {
		return new PartyEvent(UUID.randomUUID(), PartyEventTypes.INDIVIDUAL_PARTY_CREATED, 10L, "Ahmet", "Can",
				"Yilmaz", "10000000146", 2001L);
	}

	private Customer activeCustomer(Long custId, Long partyRoleId) {
		Customer customer = new Customer();
		customer.setCustId(custId);
		customer.setPartyRoleId(partyRoleId);
		customer.setActive(true);
		return customer;
	}
}
