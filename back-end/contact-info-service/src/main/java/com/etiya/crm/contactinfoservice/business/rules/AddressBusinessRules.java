package com.etiya.crm.contactinfoservice.business.rules;

import com.etiya.crm.contactinfoservice.business.exceptions.AddressLimitExceededException;
import com.etiya.crm.contactinfoservice.business.exceptions.AddressNotFoundException;
import com.etiya.crm.contactinfoservice.business.exceptions.PrimaryAddressDeletionException;
import com.etiya.crm.contactinfoservice.dataAccess.abstracts.AddressRepository;
import com.etiya.crm.contactinfoservice.entities.concretes.Address;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddressBusinessRules {

    private static final int MAX_ADDRESS_COUNT = 5;

    private final AddressRepository addressRepository;

    public AddressBusinessRules(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Address checkIfAddressExists(Long id) {
        return addressRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new AddressNotFoundException("Address not found with id: " + id));
    }

    public void checkAddressLimitNotExceeded(List<Address> existingAddresses) {
        if (existingAddresses.size() >= MAX_ADDRESS_COUNT) {
            throw new AddressLimitExceededException("You can add up to 5 addresses.");
        }
    }

    public void checkIfNotPrimary(Address address) {
        if (address.isPrimary()) {
            throw new PrimaryAddressDeletionException("Primary address cannot be deleted.");
        }
    }

    public void unsetOtherPrimaryAddresses(Long rowId, Long dataTypeId, Long excludeId) {
        List<Address> addresses = addressRepository.findAllByRowIdAndDataTypeIdAndActiveTrue(rowId, dataTypeId);
        List<Address> toUpdate = addresses.stream()
                .filter(address -> address.isPrimary() && !address.getId().equals(excludeId))
                .peek(address -> address.setPrimary(false))
                .toList();
        addressRepository.saveAll(toUpdate);
    }

}
