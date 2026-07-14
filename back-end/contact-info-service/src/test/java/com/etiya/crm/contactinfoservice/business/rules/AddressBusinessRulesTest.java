package com.etiya.crm.contactinfoservice.business.rules;

import com.etiya.crm.contactinfoservice.business.exceptions.AddressLimitExceededException;
import com.etiya.crm.contactinfoservice.business.exceptions.AddressLinkedToAccountException;
import com.etiya.crm.contactinfoservice.business.exceptions.AddressNotFoundException;
import com.etiya.crm.contactinfoservice.business.exceptions.PrimaryAddressDeletionException;
import com.etiya.crm.contactinfoservice.clients.CustomerAccountClient;
import com.etiya.crm.contactinfoservice.dataAccess.abstracts.AddressRepository;
import com.etiya.crm.contactinfoservice.entities.concretes.Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressBusinessRulesTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CustomerAccountClient customerAccountClient;

    private AddressBusinessRules addressBusinessRules;

    @Test
    void checkIfAddressExists_returnsAddress_whenFound() {
        addressBusinessRules = new AddressBusinessRules(addressRepository, customerAccountClient);
        Address address = new Address();
        address.setId(1L);
        when(addressRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(address));

        Address result = addressBusinessRules.checkIfAddressExists(1L);

        assertThat(result).isEqualTo(address);
    }

    @Test
    void checkIfAddressExists_throws_whenNotFound() {
        addressBusinessRules = new AddressBusinessRules(addressRepository, customerAccountClient);
        when(addressRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressBusinessRules.checkIfAddressExists(1L))
                .isInstanceOf(AddressNotFoundException.class);
    }

    @Test
    void checkAddressLimitNotExceeded_throws_whenFiveAddressesExist() {
        addressBusinessRules = new AddressBusinessRules(addressRepository, customerAccountClient);
        List<Address> fiveAddresses = List.of(new Address(), new Address(), new Address(), new Address(), new Address());

        assertThatThrownBy(() -> addressBusinessRules.checkAddressLimitNotExceeded(fiveAddresses))
                .isInstanceOf(AddressLimitExceededException.class);
    }

    @Test
    void checkAddressLimitNotExceeded_passes_whenUnderLimit() {
        addressBusinessRules = new AddressBusinessRules(addressRepository, customerAccountClient);
        List<Address> fourAddresses = List.of(new Address(), new Address(), new Address(), new Address());

        addressBusinessRules.checkAddressLimitNotExceeded(fourAddresses);
        // no exception thrown
    }

    @Test
    void checkIfNotPrimary_throws_whenAddressIsPrimary() {
        addressBusinessRules = new AddressBusinessRules(addressRepository, customerAccountClient);
        Address primaryAddress = new Address();
        primaryAddress.setPrimary(true);

        assertThatThrownBy(() -> addressBusinessRules.checkIfNotPrimary(primaryAddress))
                .isInstanceOf(PrimaryAddressDeletionException.class);
    }

    @Test
    void checkIfNotPrimary_passes_whenAddressIsNotPrimary() {
        addressBusinessRules = new AddressBusinessRules(addressRepository, customerAccountClient);
        Address nonPrimaryAddress = new Address();
        nonPrimaryAddress.setPrimary(false);

        addressBusinessRules.checkIfNotPrimary(nonPrimaryAddress);
        // no exception thrown
    }

    @Test
    void checkNotLinkedToAccount_throws_whenAddressIsLinkedToAccount() {
        addressBusinessRules = new AddressBusinessRules(addressRepository, customerAccountClient);
        when(customerAccountClient.existsByAddressId(1L)).thenReturn(true);

        assertThatThrownBy(() -> addressBusinessRules.checkNotLinkedToAccount(1L))
                .isInstanceOf(AddressLinkedToAccountException.class);
    }

    @Test
    void checkNotLinkedToAccount_passes_whenAddressIsNotLinkedToAccount() {
        addressBusinessRules = new AddressBusinessRules(addressRepository, customerAccountClient);
        when(customerAccountClient.existsByAddressId(1L)).thenReturn(false);

        addressBusinessRules.checkNotLinkedToAccount(1L);
        // no exception thrown
    }

    @Test
    void unsetOtherPrimaryAddresses_unsetsOnlyOtherPrimaryAddresses() {
        addressBusinessRules = new AddressBusinessRules(addressRepository, customerAccountClient);

        Address other = new Address();
        other.setId(2L);
        other.setPrimary(true);

        Address excluded = new Address();
        excluded.setId(1L);
        excluded.setPrimary(true);

        when(addressRepository.findAllByRowIdAndDataTypeIdAndActiveTrue(10L, 1L))
                .thenReturn(List.of(other, excluded));

        addressBusinessRules.unsetOtherPrimaryAddresses(10L, 1L, 1L);

        assertThat(other.isPrimary()).isFalse();
        assertThat(excluded.isPrimary()).isTrue();
    }

}
