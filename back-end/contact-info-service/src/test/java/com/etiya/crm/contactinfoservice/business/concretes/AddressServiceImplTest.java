package com.etiya.crm.contactinfoservice.business.concretes;

import com.etiya.crm.shared.contracts.address.CreateAddressRequest;
import com.etiya.crm.shared.contracts.address.UpdateAddressRequest;
import com.etiya.crm.shared.contracts.address.AddressResponse;
import com.etiya.crm.contactinfoservice.business.exceptions.AddressLimitExceededException;
import com.etiya.crm.contactinfoservice.business.exceptions.AddressLinkedToAccountException;
import com.etiya.crm.contactinfoservice.business.exceptions.PrimaryAddressDeletionException;
import com.etiya.crm.contactinfoservice.business.rules.AddressBusinessRules;
import com.etiya.crm.contactinfoservice.clients.CustomerAccountClient;
import com.etiya.crm.contactinfoservice.dataAccess.abstracts.AddressRepository;
import com.etiya.crm.contactinfoservice.entities.concretes.Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressBusinessRules addressBusinessRules;

    @Mock
    private CustomerAccountClient customerAccountClient;

    @InjectMocks
    private AddressServiceImpl addressService;

    @Test
    void getAll_returnsAllActiveAddressesMapped() {
        Address address = new Address();
        address.setId(1L);
        address.setRowId(10L);
        address.setDataTypeId(1L);
        when(addressRepository.findAllByActiveTrue()).thenReturn(List.of(address));

        List<AddressResponse> responses = addressService.getAll();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).id()).isEqualTo(1L);
    }

    @Test
    void getById_returnsMappedAddress() {
        Address address = new Address();
        address.setId(1L);
        when(addressBusinessRules.checkIfAddressExists(1L)).thenReturn(address);

        AddressResponse response = addressService.getById(1L);

        assertThat(response.id()).isEqualTo(1L);
    }

    @Test
    void getByRowIdAndDataTypeId_returnsMappedAddresses() {
        Address address = new Address();
        address.setId(1L);
        address.setRowId(10L);
        address.setDataTypeId(1L);
        when(addressRepository.findAllByRowIdAndDataTypeIdAndActiveTrue(10L, 1L)).thenReturn(List.of(address));

        List<AddressResponse> responses = addressService.getByRowIdAndDataTypeId(10L, 1L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).rowId()).isEqualTo(10L);
    }

    @Test
    void add_setsPrimaryTrue_whenItIsTheFirstAddressForRow() {
        CreateAddressRequest request = new CreateAddressRequest(10L, 1L, 5L, "Street", "12", "Desc", false);
        when(addressRepository.findAllByRowIdAndDataTypeIdAndActiveTrue(10L, 1L)).thenReturn(List.of());
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse response = addressService.add(request);

        assertThat(response.primary()).isTrue();
    }

    @Test
    void add_keepsRequestedPrimaryFlag_whenOtherAddressesExist() {
        CreateAddressRequest request = new CreateAddressRequest(10L, 1L, 5L, "Street", "12", "Desc", false);
        Address existing = new Address();
        existing.setId(99L);
        when(addressRepository.findAllByRowIdAndDataTypeIdAndActiveTrue(10L, 1L)).thenReturn(List.of(existing));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse response = addressService.add(request);

        assertThat(response.primary()).isFalse();
    }

    @Test
    void add_throws_whenLimitExceeded() {
        CreateAddressRequest request = new CreateAddressRequest(10L, 1L, 5L, "Street", "12", "Desc", false);
        List<Address> fiveAddresses = List.of(new Address(), new Address(), new Address(), new Address(), new Address());
        when(addressRepository.findAllByRowIdAndDataTypeIdAndActiveTrue(10L, 1L)).thenReturn(fiveAddresses);
        doThrow(new AddressLimitExceededException())
                .when(addressBusinessRules).checkAddressLimitNotExceeded(fiveAddresses);

        assertThatThrownBy(() -> addressService.add(request))
                .isInstanceOf(AddressLimitExceededException.class);

        verify(addressRepository, never()).save(any());
    }

    @Test
    void add_unsetsOtherPrimaryAddresses_whenNewAddressIsMarkedPrimary() {
        CreateAddressRequest request = new CreateAddressRequest(10L, 1L, 5L, "Street", "12", "Desc", true);
        Address otherPrimary = new Address();
        otherPrimary.setId(2L);
        otherPrimary.setPrimary(true);
        when(addressRepository.findAllByRowIdAndDataTypeIdAndActiveTrue(10L, 1L)).thenReturn(List.of(otherPrimary));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address saved = invocation.getArgument(0);
            saved.setId(3L);
            return saved;
        });

        addressService.add(request);

        assertThat(otherPrimary.isPrimary()).isFalse();
        verify(addressRepository).saveAll(List.of(otherPrimary));
    }

    @Test
    void update_forcesPrimaryTrue_whenItIsTheOnlyActiveAddress() {
        Address address = new Address();
        address.setId(1L);
        address.setRowId(10L);
        address.setDataTypeId(1L);
        address.setPrimary(false);

        when(addressBusinessRules.checkIfAddressExists(1L)).thenReturn(address);
        when(addressRepository.findAllByRowIdAndDataTypeIdAndActiveTrue(10L, 1L)).thenReturn(List.of(address));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateAddressRequest request = new UpdateAddressRequest(5L, "Street", "12", "Desc", false);
        AddressResponse response = addressService.update(1L, request);

        assertThat(response.primary()).isTrue();
    }

    @Test
    void delete_throws_whenAddressIsPrimary() {
        Address address = new Address();
        address.setId(1L);
        address.setPrimary(true);

        when(addressBusinessRules.checkIfAddressExists(1L)).thenReturn(address);
        doThrow(new PrimaryAddressDeletionException())
                .when(addressBusinessRules).checkIfNotPrimary(address);

        assertThatThrownBy(() -> addressService.delete(1L))
                .isInstanceOf(PrimaryAddressDeletionException.class);

        verify(addressRepository, never()).save(any());
    }

    @Test
    void delete_deactivatesAddress_whenNotPrimary() {
        Address address = new Address();
        address.setId(1L);
        address.setPrimary(false);
        address.setActive(true);

        when(addressBusinessRules.checkIfAddressExists(1L)).thenReturn(address);
        when(customerAccountClient.existsByAddressId(1L)).thenReturn(false);

        addressService.delete(1L);

        assertThat(address.isActive()).isFalse();
        verify(addressBusinessRules).ensureNotLinkedToAccount(false);
        verify(addressRepository).save(address);
    }

    @Test
    void delete_throws_whenAddressIsLinkedToAccount() {
        Address address = new Address();
        address.setId(1L);
        address.setPrimary(false);

        when(addressBusinessRules.checkIfAddressExists(1L)).thenReturn(address);
        when(customerAccountClient.existsByAddressId(1L)).thenReturn(true);
        doThrow(new AddressLinkedToAccountException())
                .when(addressBusinessRules).ensureNotLinkedToAccount(true);

        assertThatThrownBy(() -> addressService.delete(1L))
                .isInstanceOf(AddressLinkedToAccountException.class);

        verify(addressRepository, never()).save(any());
    }

    @Test
    void deactivateAllForRow_deactivatesAllActiveAddressesForRow() {
        Address first = new Address();
        first.setActive(true);
        Address second = new Address();
        second.setActive(true);

        when(addressRepository.findAllByRowIdAndDataTypeIdAndActiveTrue(10L, 1L))
                .thenReturn(List.of(first, second));

        addressService.deactivateAllForRow(10L, 1L);

        assertThat(first.isActive()).isFalse();
        assertThat(second.isActive()).isFalse();
        verify(addressRepository).saveAll(List.of(first, second));
    }

}
