package com.etiya.crm.customerservice.business.concretes;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.customerservice.business.abstracts.CustomerFinder;
import com.etiya.crm.customerservice.business.abstracts.CustomerLookupResolver;
import com.etiya.crm.customerservice.business.dtos.requests.ContactInfo;
import com.etiya.crm.customerservice.clients.controllers.ContactAddressClient;
import com.etiya.crm.shared.contracts.contactmedium.ContactMediumResponse;
import com.etiya.crm.shared.contracts.gnltp.GnlTpCodes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerContactServiceImplTest {

	private static final Long EMAIL_TYPE = 4001L;
	private static final Long MOBILE_TYPE = 4002L;
	private static final Long HOME_TYPE = 4003L;
	private static final Long FAX_TYPE = 4004L;
	private static final Long DATA_TYPE_ID = 1L;

	@Mock
	private ContactAddressClient contactAddressClient;

	@Mock
	private CustomerLookupResolver lookupResolver;

	@Mock
	private CustomerFinder customerFinder;

	@InjectMocks
	private CustomerContactServiceImpl service;

	@Test
	void getContact_mapsEachMediumByType() {
		when(lookupResolver.resolveCustomerDataTypeId()).thenReturn(DATA_TYPE_ID);
		when(lookupResolver.resolveContactMediumTypeId(GnlTpCodes.EMAIL)).thenReturn(EMAIL_TYPE);
		when(lookupResolver.resolveContactMediumTypeId(GnlTpCodes.MOBILE)).thenReturn(MOBILE_TYPE);
		when(lookupResolver.resolveContactMediumTypeId(GnlTpCodes.LANDLINE)).thenReturn(HOME_TYPE);
		when(lookupResolver.resolveContactMediumTypeId(GnlTpCodes.FAX)).thenReturn(FAX_TYPE);
		List<ContactMediumResponse> mediums = List.of(
				medium(1L, EMAIL_TYPE, "a@b.com"),
				medium(2L, MOBILE_TYPE, "5551234567"));
		when(contactAddressClient.getContactMediumsByCustomer(10L, DATA_TYPE_ID)).thenReturn(mediums);

		ContactInfo result = service.getContact(10L);

		assertThat(result.email()).isEqualTo("a@b.com");
		assertThat(result.mobilePhone()).isEqualTo("5551234567");
		assertThat(result.homePhone()).isNull();
		assertThat(result.fax()).isNull();
	}

	@Test
	void updateContact_updatesExistingRequiredMedium() {
		stubTypeResolution();
		ContactMediumResponse existingEmail = medium(1L, EMAIL_TYPE, "old@b.com");
		ContactMediumResponse existingMobile = medium(2L, MOBILE_TYPE, "5550000000");
		when(contactAddressClient.getContactMediumsByCustomer(10L, DATA_TYPE_ID))
				.thenReturn(List.of(existingEmail, existingMobile));
		when(contactAddressClient.updateContactMedium(any(), any()))
				.thenReturn(medium(1L, EMAIL_TYPE, "new@b.com"))
				.thenReturn(medium(2L, MOBILE_TYPE, "5551234567"));
		ContactInfo request = new ContactInfo("new@b.com", "5551234567", null, null);

		ContactInfo result = service.updateContact(10L, request);

		assertThat(result.email()).isEqualTo("new@b.com");
		assertThat(result.mobilePhone()).isEqualTo("5551234567");
		assertThat(result.homePhone()).isNull();
	}

	@Test
	void updateContact_leavesOptionalMediumUntouched_whenExistingAndNewValueBlank() {
		stubTypeResolution();
		ContactMediumResponse existingEmail = medium(1L, EMAIL_TYPE, "a@b.com");
		ContactMediumResponse existingMobile = medium(2L, MOBILE_TYPE, "5551234567");
		ContactMediumResponse existingHome = medium(3L, HOME_TYPE, "2121234567");
		when(contactAddressClient.getContactMediumsByCustomer(10L, DATA_TYPE_ID))
				.thenReturn(List.of(existingEmail, existingMobile, existingHome));
		when(contactAddressClient.updateContactMedium(any(), any())).thenReturn(existingEmail).thenReturn(
				existingMobile);
		ContactInfo request = new ContactInfo("a@b.com", "5551234567", null, null);

		ContactInfo result = service.updateContact(10L, request);

		assertThat(result.homePhone()).isEqualTo("2121234567");
		verify(contactAddressClient, never()).updateContactMedium(eq(3L), any());
	}

	@Test
	void updateContact_createsOptionalMedium_whenNotExistingAndValueProvided() {
		stubTypeResolution();
		ContactMediumResponse existingEmail = medium(1L, EMAIL_TYPE, "a@b.com");
		ContactMediumResponse existingMobile = medium(2L, MOBILE_TYPE, "5551234567");
		when(contactAddressClient.getContactMediumsByCustomer(10L, DATA_TYPE_ID))
				.thenReturn(List.of(existingEmail, existingMobile));
		when(contactAddressClient.updateContactMedium(any(), any())).thenReturn(existingEmail).thenReturn(
				existingMobile);
		when(contactAddressClient.addContactMedium(any())).thenReturn(medium(4L, FAX_TYPE, "12345678901"));
		ContactInfo request = new ContactInfo("a@b.com", "5551234567", null, "12345678901");

		ContactInfo result = service.updateContact(10L, request);

		assertThat(result.fax()).isEqualTo("12345678901");
	}

	@Test
	void updateContact_returnsNull_forOptionalMedium_whenNotExistingAndValueBlank() {
		stubTypeResolution();
		ContactMediumResponse existingEmail = medium(1L, EMAIL_TYPE, "a@b.com");
		ContactMediumResponse existingMobile = medium(2L, MOBILE_TYPE, "5551234567");
		when(contactAddressClient.getContactMediumsByCustomer(10L, DATA_TYPE_ID))
				.thenReturn(List.of(existingEmail, existingMobile));
		when(contactAddressClient.updateContactMedium(any(), any())).thenReturn(existingEmail).thenReturn(
				existingMobile);
		ContactInfo request = new ContactInfo("a@b.com", "5551234567", null, null);

		ContactInfo result = service.updateContact(10L, request);

		assertThat(result.fax()).isNull();
		verify(contactAddressClient, never()).addContactMedium(any());
	}

	private void stubTypeResolution() {
		when(lookupResolver.resolveCustomerDataTypeId()).thenReturn(DATA_TYPE_ID);
		when(lookupResolver.resolveContactMediumTypeId(GnlTpCodes.EMAIL)).thenReturn(EMAIL_TYPE);
		when(lookupResolver.resolveContactMediumTypeId(GnlTpCodes.MOBILE)).thenReturn(MOBILE_TYPE);
		when(lookupResolver.resolveContactMediumTypeId(GnlTpCodes.LANDLINE)).thenReturn(HOME_TYPE);
		when(lookupResolver.resolveContactMediumTypeId(GnlTpCodes.FAX)).thenReturn(FAX_TYPE);
	}

	private static Long eq(Long value) {
		return org.mockito.ArgumentMatchers.eq(value);
	}

	private ContactMediumResponse medium(Long id, Long typeId, String data) {
		return new ContactMediumResponse(id, 10L, DATA_TYPE_ID, data, typeId, null, null, null, null);
	}
}
