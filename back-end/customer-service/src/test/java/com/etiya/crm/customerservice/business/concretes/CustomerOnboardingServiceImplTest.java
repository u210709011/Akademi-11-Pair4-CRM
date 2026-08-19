package com.etiya.crm.customerservice.business.concretes;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.customerservice.business.abstracts.CustomerLookupResolver;
import com.etiya.crm.customerservice.business.abstracts.IdentityVerificationService;
import com.etiya.crm.customerservice.business.abstracts.LookupCacheService;
import com.etiya.crm.customerservice.business.dtos.requests.AddressInfo;
import com.etiya.crm.customerservice.business.dtos.requests.ContactInfo;
import com.etiya.crm.customerservice.business.dtos.requests.IndividualInfo;
import com.etiya.crm.customerservice.business.dtos.requests.OnboardCustomerRequest;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerResponse;
import com.etiya.crm.customerservice.business.dtos.responses.IdentityVerificationResponse;
import com.etiya.crm.customerservice.business.exceptions.DuplicateNationalIdException;
import com.etiya.crm.customerservice.business.exceptions.OnboardingFailedException;
import com.etiya.crm.customerservice.business.rules.AddressBusinessRules;
import com.etiya.crm.customerservice.business.rules.IdentityValidationRules;
import com.etiya.crm.customerservice.clients.controllers.ContactAddressClient;
import com.etiya.crm.customerservice.clients.controllers.PartyClient;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerAccountRepository;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerRepository;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerSearchViewRepository;
import com.etiya.crm.customerservice.entities.concretes.Customer;
import com.etiya.crm.customerservice.entities.concretes.CustomerAccount;
import com.etiya.crm.customerservice.mapper.CustomerMapper;
import com.etiya.crm.shared.contracts.gnltp.GnlTpCodes;
import com.etiya.crm.shared.contracts.individual.PartyRoleResponse;
import com.etiya.crm.shared.events.outbox.OutboxEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerOnboardingServiceImplTest {

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private CustomerAccountRepository customerAccountRepository;

	@Mock
	private CustomerSearchViewRepository customerSearchViewRepository;

	@Mock
	private PartyClient partyClient;

	@Mock
	private ContactAddressClient contactAddressClient;

	@Mock
	private IdentityVerificationService identityVerificationService;

	@Mock
	private IdentityValidationRules identityRules;

	@Mock
	private AddressBusinessRules addressRules;

	@Mock
	private CustomerLookupResolver lookupResolver;

	@Mock
	private CustomerMapper customerMapper;

	@Mock
	private OutboxEventPublisher outboxEventPublisher;

	@Mock
	private AccountNumberGenerator accountNumberGenerator;

	@Mock
	private LookupCacheService lookupCacheService;

	@InjectMocks
	private CustomerOnboardingServiceImpl service;

	private final IndividualInfo individual = new IndividualInfo("Ahmet", null, "Yilmaz",
			LocalDate.of(1990, 6, 15), 1L, null, null, "10000000146");

	@Test
	void verifyIdentity_ok_whenNationalIdNotDuplicate() {
		when(partyClient.existsByNationalId("10000000146")).thenReturn(false);

		IdentityVerificationResponse result = service.verifyIdentity(individual);

		assertThat(result.verified()).isTrue();
		verify(identityRules).validateBirthDate(individual.birthDate());
		verify(identityVerificationService).verify(individual);
	}

	@Test
	void verifyIdentity_throws_whenNationalIdAlreadyExists() {
		when(partyClient.existsByNationalId("10000000146")).thenReturn(true);
		doThrow(new DuplicateNationalIdException()).when(identityRules).ensureUniqueNationalId(true);

		assertThatThrownBy(() -> service.verifyIdentity(individual))
				.isInstanceOf(DuplicateNationalIdException.class);
	}

	@Test
	void onboard_happyPath_publishesEventAndReturnsMappedResponse() {
		stubSuccessfulPartyAndAccountCreation();
		OnboardCustomerRequest request = onboardRequest();
		CustomerResponse mapped = new CustomerResponse(10L, "000010", 100L, 1L, true, List.of());
		when(customerMapper.toResponse(any(Customer.class), any(), eqLong(601L))).thenReturn(mapped);

		CustomerResponse result = service.onboard(request);

		assertThat(result).isSameAs(mapped);
		verify(outboxEventPublisher).publish(any(), eqString("10"), any(), any());
		verify(partyClient, never()).deleteParty(any());
	}

	@Test
	void onboard_compensatesContactAndParty_whenContactCreationFails() {
		stubSuccessfulPartyAndAccountCreation();
		OnboardCustomerRequest request = onboardRequest();
		RuntimeException contactFailure = new RuntimeException("contact-info-service down");
		doThrow(contactFailure).when(contactAddressClient).createContact(any());
		when(lookupResolver.resolveCustomerDataTypeId()).thenReturn(1L);

		assertThatThrownBy(() -> service.onboard(request))
				.isInstanceOf(OnboardingFailedException.class)
				.hasCause(contactFailure);

		verify(contactAddressClient).deleteByCustomerId(eqLong(10L), eqLong(1L));
		verify(partyClient).deleteParty(200L);
		verify(outboxEventPublisher, never()).publish(any(), any(), any(), any());
	}

	@Test
	void onboard_compensatesPartyOnly_whenCustomerCreationFailsBeforeContactStep() {
		PartyRoleResponse partyRole = new PartyRoleResponse(200L, 100L);
		when(partyClient.createIndividualWithRole(any())).thenReturn(partyRole);
		when(lookupResolver.resolveIndividualCustomerTypeId()).thenReturn(1L);
		RuntimeException dbFailure = new RuntimeException("db down");
		when(customerRepository.save(any())).thenThrow(dbFailure);
		OnboardCustomerRequest request = onboardRequest();

		assertThatThrownBy(() -> service.onboard(request))
				.isInstanceOf(OnboardingFailedException.class)
				.hasCause(dbFailure);

		verify(partyClient).deleteParty(200L);
		verify(contactAddressClient, never()).createContact(any());
		verify(contactAddressClient, never()).deleteByCustomerId(any(), any());
	}

	@Test
	void onboard_swallowsContactCompensationFailure_stillThrowsOriginalContactFailure() {
		stubSuccessfulPartyAndAccountCreation();
		OnboardCustomerRequest request = onboardRequest();
		RuntimeException contactFailure = new RuntimeException("contact-info-service down");
		doThrow(contactFailure).when(contactAddressClient).createContact(any());
		when(lookupResolver.resolveCustomerDataTypeId()).thenReturn(1L);
		doThrow(new RuntimeException("contact-info-service still down"))
				.when(contactAddressClient).deleteByCustomerId(any(), any());

		assertThatThrownBy(() -> service.onboard(request))
				.isInstanceOf(OnboardingFailedException.class)
				.hasCause(contactFailure);

		verify(contactAddressClient).deleteByCustomerId(eqLong(10L), eqLong(1L));
		verify(partyClient).deleteParty(200L);
	}

	@Test
	void onboard_includesHomePhoneAndFax_onlyWhenProvided() {
		stubSuccessfulPartyAndAccountCreation();
		when(lookupResolver.resolveContactMediumTypeId(GnlTpCodes.EMAIL)).thenReturn(10L);
		when(lookupResolver.resolveContactMediumTypeId(GnlTpCodes.MOBILE)).thenReturn(11L);
		when(lookupResolver.resolveContactMediumTypeId(GnlTpCodes.LANDLINE)).thenReturn(12L);
		when(lookupResolver.resolveContactMediumTypeId(GnlTpCodes.FAX)).thenReturn(13L);
		CustomerResponse mapped = new CustomerResponse(10L, "000010", 100L, 1L, true, List.of());
		when(customerMapper.toResponse(any(Customer.class), any(), eqLong(601L))).thenReturn(mapped);
		ContactInfo contact = new ContactInfo("a@b.com", "5551234567", "2125550000", "2125559999");
		OnboardCustomerRequest request = new OnboardCustomerRequest(individual,
				List.of(new AddressInfo(5L, "Cad", "No 1", "Ev")), contact);

		service.onboard(request);

		org.mockito.ArgumentCaptor<com.etiya.crm.shared.contracts.contactmedium.CreateContactCommand> captor =
				org.mockito.ArgumentCaptor.forClass(com.etiya.crm.shared.contracts.contactmedium.CreateContactCommand.class);
		verify(contactAddressClient).createContact(captor.capture());
		assertThat(captor.getValue().contactMediums()).hasSize(4);
	}

	@Test
	void onboard_omitsHomePhoneAndFax_whenBlank() {
		stubSuccessfulPartyAndAccountCreation();
		when(lookupResolver.resolveContactMediumTypeId(GnlTpCodes.EMAIL)).thenReturn(10L);
		when(lookupResolver.resolveContactMediumTypeId(GnlTpCodes.MOBILE)).thenReturn(11L);
		CustomerResponse mapped = new CustomerResponse(10L, "000010", 100L, 1L, true, List.of());
		when(customerMapper.toResponse(any(Customer.class), any(), eqLong(601L))).thenReturn(mapped);
		OnboardCustomerRequest request = onboardRequest();

		service.onboard(request);

		org.mockito.ArgumentCaptor<com.etiya.crm.shared.contracts.contactmedium.CreateContactCommand> captor =
				org.mockito.ArgumentCaptor.forClass(com.etiya.crm.shared.contracts.contactmedium.CreateContactCommand.class);
		verify(contactAddressClient).createContact(captor.capture());
		assertThat(captor.getValue().contactMediums()).hasSize(2);
	}

	@Test
	void onboard_swallowsPartyCompensationFailure_stillThrowsOriginalOnboardingFailure() {
		PartyRoleResponse partyRole = new PartyRoleResponse(200L, 100L);
		when(partyClient.createIndividualWithRole(any())).thenReturn(partyRole);
		when(lookupResolver.resolveIndividualCustomerTypeId()).thenReturn(1L);
		RuntimeException dbFailure = new RuntimeException("db down");
		when(customerRepository.save(any())).thenThrow(dbFailure);
		doThrow(new RuntimeException("party-service also down")).when(partyClient).deleteParty(200L);
		OnboardCustomerRequest request = onboardRequest();

		assertThatThrownBy(() -> service.onboard(request))
				.isInstanceOf(OnboardingFailedException.class)
				.hasCause(dbFailure);
	}

	private void stubSuccessfulPartyAndAccountCreation() {
		PartyRoleResponse partyRole = new PartyRoleResponse(200L, 100L);
		when(partyClient.createIndividualWithRole(any())).thenReturn(partyRole);
		when(lookupResolver.resolveIndividualCustomerTypeId()).thenReturn(1L);
		when(customerRepository.save(any())).thenAnswer(invocation -> {
			Customer customer = invocation.getArgument(0);
			customer.setCustId(10L);
			return customer;
		});
		when(lookupResolver.resolveCustomerAccountTypeId()).thenReturn(500L);
		when(lookupResolver.resolveActiveAccountStatusId()).thenReturn(601L);
		when(customerAccountRepository.save(any())).thenAnswer(invocation -> {
			CustomerAccount account = invocation.getArgument(0);
			account.setCustAcctId(42L);
			return account;
		});
		when(accountNumberGenerator.generate(42L)).thenReturn("000042");
		when(lookupResolver.resolveCustomerRoleTypeId()).thenReturn(700L);
		when(lookupCacheService.resolveTypeValue(700L)).thenReturn("Musteri");
		when(lookupResolver.resolveCustomerDataTypeId()).thenReturn(1L);
		when(addressRules.toAddressCommandsWithPrimaryRule(any())).thenReturn(List.of());
	}

	private OnboardCustomerRequest onboardRequest() {
		AddressInfo address = new AddressInfo(5L, "Cad", "No 1", "Ev");
		ContactInfo contact = new ContactInfo("a@b.com", "5551234567", null, null);
		return new OnboardCustomerRequest(individual, List.of(address), contact);
	}

	private static Long eqLong(Long value) {
		return org.mockito.ArgumentMatchers.eq(value);
	}

	private static String eqString(String value) {
		return org.mockito.ArgumentMatchers.eq(value);
	}
}
