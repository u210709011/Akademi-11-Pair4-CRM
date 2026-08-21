package com.etiya.crm.customerservice.business.concretes;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.customerservice.business.abstracts.CustomerFinder;
import com.etiya.crm.customerservice.business.abstracts.CustomerLookupResolver;
import com.etiya.crm.customerservice.business.dtos.requests.AddressEditRequest;
import com.etiya.crm.customerservice.business.dtos.requests.AddressInfo;
import com.etiya.crm.customerservice.business.rules.AddressBusinessRules;
import com.etiya.crm.customerservice.clients.controllers.ContactAddressClient;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerAccountRepository;
import com.etiya.crm.shared.contracts.address.AddressResponse;
import com.etiya.crm.shared.contracts.address.CreateAddressRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerAddressServiceImplTest {

	@Mock
	private ContactAddressClient contactAddressClient;

	@Mock
	private CustomerAccountRepository customerAccountRepository;

	@Mock
	private AddressBusinessRules rules;

	@Mock
	private CustomerLookupResolver lookupResolver;

	@Mock
	private CustomerFinder customerFinder;

	@InjectMocks
	private CustomerAddressServiceImpl service;

	@Test
	void getAddresses_verifiesCustomerActive_thenReturnsContactInfoServiceResult() {
		when(lookupResolver.resolveCustomerDataTypeId()).thenReturn(1L);
		List<AddressResponse> addresses = List.of(address(20L, true));
		when(contactAddressClient.getAddressesByCustomer(10L, 1L)).thenReturn(addresses);

		List<AddressResponse> result = service.getAddresses(10L);

		assertThat(result).isEqualTo(addresses);
		verify(customerFinder).getActiveCustomerOrThrow(10L);
	}

	@Test
	void addAddress_createsAddress_afterValidatingLimit() {
		when(lookupResolver.resolveCustomerDataTypeId()).thenReturn(1L);
		when(contactAddressClient.getAddressesByCustomer(10L, 1L)).thenReturn(List.of(address(20L, true)));
		AddressEditRequest request = new AddressEditRequest(5L, "Cad", "No 1", "Ev", false);
		AddressResponse created = address(21L, false);
		when(contactAddressClient.addAddress(any())).thenReturn(created);

		AddressResponse result = service.addAddress(10L, request);

		assertThat(result).isEqualTo(created);
		verify(rules).validateAddressLimit(1);
		ArgumentCaptor<CreateAddressRequest> captor = ArgumentCaptor.forClass(CreateAddressRequest.class);
		verify(contactAddressClient).addAddress(captor.capture());
		assertThat(captor.getValue().rowId()).isEqualTo(10L);
		assertThat(captor.getValue().cityId()).isEqualTo(5L);
	}

	@Test
	void updateAddress_ensuresAddressBelongsToCustomer_thenUpdates() {
		when(lookupResolver.resolveCustomerDataTypeId()).thenReturn(1L);
		List<AddressResponse> existing = List.of(address(20L, true));
		when(contactAddressClient.getAddressesByCustomer(10L, 1L)).thenReturn(existing);
		AddressEditRequest request = new AddressEditRequest(5L, "Cad", "No 1", "Ev", true);
		AddressResponse updated = address(20L, true);
		when(contactAddressClient.updateAddress(any(), any())).thenReturn(updated);

		AddressResponse result = service.updateAddress(10L, 20L, request);

		assertThat(result).isEqualTo(updated);
		verify(rules).ensureAddressBelongsToCustomer(10L, 20L, existing);
	}

	@Test
	void deleteAddress_runsAllGuards_thenDeletes() {
		when(lookupResolver.resolveCustomerDataTypeId()).thenReturn(1L);
		AddressResponse target = address(20L, false);
		List<AddressResponse> existing = List.of(target);
		when(contactAddressClient.getAddressesByCustomer(10L, 1L)).thenReturn(existing);
		when(rules.ensureAddressBelongsToCustomer(10L, 20L, existing)).thenReturn(target);
		when(lookupResolver.resolveDeletedAccountStatusId()).thenReturn(603L);
		when(customerAccountRepository.existsByAddressIdAndAcctStIdNotDeleted(20L, 603L)).thenReturn(false);

		service.deleteAddress(10L, 20L);

		verify(rules).ensureAddressNotPrimary(target);
		verify(rules).ensureAddressNotLinkedToBillingAccount(false);
		verify(contactAddressClient).deleteAddress(20L);
	}

	@Test
	void resolveBillingAddress_createsNewAddress_whenNewAddressGiven() {
		when(lookupResolver.resolveCustomerDataTypeId()).thenReturn(1L);
		when(contactAddressClient.getAddressesByCustomer(10L, 1L)).thenReturn(List.of());
		AddressInfo newAddress = new AddressInfo(5L, "Cad", "No 1", "Ev");
		AddressResponse created = address(22L, false);
		when(contactAddressClient.addAddress(any())).thenReturn(created);

		AddressResponse result = service.resolveBillingAddress(10L, null, newAddress);

		assertThat(result).isEqualTo(created);
		verify(rules).validateAddressLimit(0);
	}

	@Test
	void resolveBillingAddress_resolvesExistingAddress_whenNoNewAddressGiven() {
		when(lookupResolver.resolveCustomerDataTypeId()).thenReturn(1L);
		AddressResponse existing = address(20L, true);
		when(contactAddressClient.getAddressesByCustomer(10L, 1L)).thenReturn(List.of(existing));
		when(rules.ensureAddressBelongsToCustomer(10L, 20L, List.of(existing))).thenReturn(existing);

		AddressResponse result = service.resolveBillingAddress(10L, 20L, null);

		assertThat(result).isEqualTo(existing);
		verify(contactAddressClient, never()).addAddress(any());
	}

	private AddressResponse address(Long id, boolean primary) {
		return new AddressResponse(id, 10L, 1L, 5L, "Cad", "No", "Ev", primary, Instant.now(), "sys", Instant.now(),
				"sys");
	}
}
