package com.etiya.crm.customerservice.messaging;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.customerservice.business.abstracts.LookupCacheService;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerSearchViewRepository;
import com.etiya.crm.customerservice.entities.concretes.CustomerSearchView;
import com.etiya.crm.shared.contracts.lookup.DataTypeIds;
import com.etiya.crm.shared.contracts.lookup.LookupCodes;
import com.etiya.crm.shared.contracts.lookup.LookupGroups;
import com.etiya.crm.shared.events.contactmedium.ContactMediumEvent;
import com.etiya.crm.shared.events.contactmedium.ContactMediumEventTypes;
import com.etiya.crm.shared.events.inbox.InboxEventRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactMediumEventListenerTest {

	private static final Long MOBILE_PHONE_TYPE_ID = 4002L;
	private static final Long OTHER_TYPE_ID = 4001L; // e.g. EMAIL

	@Mock
	private InboxEventRepository inboxEventRepository;

	@Mock
	private CustomerSearchViewRepository customerSearchViewRepository;

	@Mock
	private LookupCacheService lookupCacheService;

	@InjectMocks
	private ContactMediumEventListener listener;

	@Test
	void onContactMediumEvent_skipsProcessing_whenEventAlreadyInInbox() {
		ContactMediumEvent event = mobilePhoneEvent(ContactMediumEventTypes.CONTACT_MEDIUM_CREATED, "5551234567");
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(true);

		listener.onContactMediumEvent(event);

		verify(customerSearchViewRepository, never()).findById(any());
	}

	@Test
	void onContactMediumEvent_ignoresNonCustomerDataType() {
		ContactMediumEvent event = new ContactMediumEvent(UUID.randomUUID(),
				ContactMediumEventTypes.CONTACT_MEDIUM_CREATED, 10L, DataTypeIds.PARTY, MOBILE_PHONE_TYPE_ID,
				"5551234567");
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(false);
		when(lookupCacheService.resolveTypeId(LookupGroups.CONTACT_MEDIUM_TYPE, LookupCodes.CONTACT_MEDIUM_MOBILE_PHONE))
				.thenReturn(MOBILE_PHONE_TYPE_ID);

		listener.onContactMediumEvent(event);

		verify(customerSearchViewRepository, never()).findById(any());
	}

	@Test
	void onContactMediumEvent_ignoresNonMobilePhoneMedium() {
		ContactMediumEvent event = new ContactMediumEvent(UUID.randomUUID(),
				ContactMediumEventTypes.CONTACT_MEDIUM_CREATED, 10L, DataTypeIds.CUSTOMER, OTHER_TYPE_ID,
				"ahmet@example.com");
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(false);
		when(lookupCacheService.resolveTypeId(LookupGroups.CONTACT_MEDIUM_TYPE, LookupCodes.CONTACT_MEDIUM_MOBILE_PHONE))
				.thenReturn(MOBILE_PHONE_TYPE_ID);

		listener.onContactMediumEvent(event);

		verify(customerSearchViewRepository, never()).findById(any());
	}

	@Test
	void onContactMediumEvent_syncsGsm_whenMobilePhoneCreated() {
		ContactMediumEvent event = mobilePhoneEvent(ContactMediumEventTypes.CONTACT_MEDIUM_CREATED, "5551234567");
		CustomerSearchView view = new CustomerSearchView();
		view.setCustId(10L);
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(false);
		when(lookupCacheService.resolveTypeId(LookupGroups.CONTACT_MEDIUM_TYPE, LookupCodes.CONTACT_MEDIUM_MOBILE_PHONE))
				.thenReturn(MOBILE_PHONE_TYPE_ID);
		when(customerSearchViewRepository.findById(10L)).thenReturn(Optional.of(view));

		listener.onContactMediumEvent(event);

		assertThat(view.getGsm()).isEqualTo("5551234567");
		verify(customerSearchViewRepository).save(view);
	}

	@Test
	void onContactMediumEvent_syncsGsm_whenMobilePhoneUpdated() {
		ContactMediumEvent event = mobilePhoneEvent(ContactMediumEventTypes.CONTACT_MEDIUM_UPDATED, "5559998877");
		CustomerSearchView view = new CustomerSearchView();
		view.setCustId(10L);
		view.setGsm("5551234567");
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(false);
		when(lookupCacheService.resolveTypeId(LookupGroups.CONTACT_MEDIUM_TYPE, LookupCodes.CONTACT_MEDIUM_MOBILE_PHONE))
				.thenReturn(MOBILE_PHONE_TYPE_ID);
		when(customerSearchViewRepository.findById(10L)).thenReturn(Optional.of(view));

		listener.onContactMediumEvent(event);

		assertThat(view.getGsm()).isEqualTo("5559998877");
	}

	@Test
	void onContactMediumEvent_clearsGsm_whenMobilePhoneDeactivated() {
		ContactMediumEvent event = mobilePhoneEvent(ContactMediumEventTypes.CONTACT_MEDIUM_DEACTIVATED, "5551234567");
		CustomerSearchView view = new CustomerSearchView();
		view.setCustId(10L);
		view.setGsm("5551234567");
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(false);
		when(lookupCacheService.resolveTypeId(LookupGroups.CONTACT_MEDIUM_TYPE, LookupCodes.CONTACT_MEDIUM_MOBILE_PHONE))
				.thenReturn(MOBILE_PHONE_TYPE_ID);
		when(customerSearchViewRepository.findById(10L)).thenReturn(Optional.of(view));

		listener.onContactMediumEvent(event);

		assertThat(view.getGsm()).isNull();
	}

	@Test
	void onContactMediumEvent_skipsSync_whenCustomerSearchViewNotFound() {
		ContactMediumEvent event = mobilePhoneEvent(ContactMediumEventTypes.CONTACT_MEDIUM_CREATED, "5551234567");
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(false);
		when(lookupCacheService.resolveTypeId(LookupGroups.CONTACT_MEDIUM_TYPE, LookupCodes.CONTACT_MEDIUM_MOBILE_PHONE))
				.thenReturn(MOBILE_PHONE_TYPE_ID);
		when(customerSearchViewRepository.findById(10L)).thenReturn(Optional.empty());

		listener.onContactMediumEvent(event);

		verify(customerSearchViewRepository, never()).save(any());
	}

	@Test
	void onContactMediumEvent_throwsLookupServiceUnavailable_whenLookupCacheFails() {
		ContactMediumEvent event = mobilePhoneEvent(ContactMediumEventTypes.CONTACT_MEDIUM_CREATED, "5551234567");
		when(inboxEventRepository.existsById(event.eventId())).thenReturn(false);
		when(lookupCacheService.resolveTypeId(LookupGroups.CONTACT_MEDIUM_TYPE, LookupCodes.CONTACT_MEDIUM_MOBILE_PHONE))
				.thenThrow(new RuntimeException("401 Unauthorized"));

		assertThatThrownBy(() -> listener.onContactMediumEvent(event))
				.isInstanceOf(LookupServiceUnavailableException.class);

		verify(customerSearchViewRepository, never()).findById(any());
	}

	private ContactMediumEvent mobilePhoneEvent(String type, String cntcData) {
		return new ContactMediumEvent(UUID.randomUUID(), type, 10L, DataTypeIds.CUSTOMER, MOBILE_PHONE_TYPE_ID,
				cntcData);
	}
}
