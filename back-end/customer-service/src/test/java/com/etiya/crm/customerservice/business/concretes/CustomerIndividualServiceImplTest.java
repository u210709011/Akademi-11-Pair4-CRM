package com.etiya.crm.customerservice.business.concretes;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.customerservice.business.abstracts.CustomerFinder;
import com.etiya.crm.customerservice.business.abstracts.IdentityVerificationService;
import com.etiya.crm.customerservice.business.dtos.requests.UpdateIndividualInfo;
import com.etiya.crm.customerservice.business.rules.IdentityValidationRules;
import com.etiya.crm.customerservice.clients.controllers.PartyClient;
import com.etiya.crm.customerservice.entities.concretes.Customer;
import com.etiya.crm.shared.contracts.individual.IndividualResponse;
import com.etiya.crm.shared.contracts.individual.UpdateIndividualCommand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerIndividualServiceImplTest {

	@Mock
	private PartyClient partyClient;

	@Mock
	private IdentityValidationRules identityRules;

	@Mock
	private CustomerFinder customerFinder;

	@Mock
	private IdentityVerificationService identityVerificationService;

	@InjectMocks
	private CustomerIndividualServiceImpl service;

	@Test
	void getIndividual_delegatesToPartyClient_withCustomersPartyRoleId() {
		Customer customer = new Customer();
		customer.setPartyRoleId(100L);
		when(customerFinder.getActiveCustomerOrThrow(10L)).thenReturn(customer);
		IndividualResponse response = new IndividualResponse("Ahmet", null, "Yilmaz", null, 1L, null, null,
				"10000000146");
		when(partyClient.getIndividualByPartyRoleId(100L)).thenReturn(response);

		IndividualResponse result = service.getIndividual(10L);

		assertThat(result).isSameAs(response);
	}

	@Test
	void updateIndividual_validatesBirthDate_verifiesIdentity_andForwardsToPartyClient() {
		Customer customer = new Customer();
		customer.setPartyRoleId(100L);
		when(customerFinder.getActiveCustomerOrThrow(10L)).thenReturn(customer);
		LocalDate birthDate = LocalDate.of(1990, 6, 15);
		UpdateIndividualInfo request = new UpdateIndividualInfo("Ahmet", "Can", "Yilmazoglu", 1L, "Ayse", "Mehmet",
				birthDate, "10000000146");
		IndividualResponse response = new IndividualResponse("Ahmet", "Can", "Yilmazoglu", birthDate, 1L, "Ayse",
				"Mehmet", "10000000146");
		when(partyClient.updateIndividual(eq(100L), any(UpdateIndividualCommand.class))).thenReturn(response);

		IndividualResponse result = service.updateIndividual(10L, request);

		assertThat(result).isSameAs(response);
		verify(identityRules).validateBirthDate(birthDate);
		verify(identityVerificationService).verify(any());
		ArgumentCaptor<UpdateIndividualCommand> captor = ArgumentCaptor.forClass(UpdateIndividualCommand.class);
		verify(partyClient).updateIndividual(eq(100L), captor.capture());
		assertThat(captor.getValue().firstName()).isEqualTo("Ahmet");
		assertThat(captor.getValue().nationalId()).isEqualTo("10000000146");
	}
}
