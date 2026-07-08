package com.etiya.crm.contactinfoservice.business.rules;

import com.etiya.crm.contactinfoservice.business.exceptions.AddressNotFoundException;
import com.etiya.crm.contactinfoservice.dataAccess.abstracts.AddressRepository;
import com.etiya.crm.contactinfoservice.entities.concretes.Address;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddressBusinessRules {

    private final AddressRepository addressRepository;

    public AddressBusinessRules(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Address checkIfAddressExists(Long id) {
        return addressRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AddressNotFoundException("Address not found with id: " + id));
    }

    public void unsetOtherPrimaryAddresses(Long rowId, Long dataTypeId, Long excludeId) {
        List<Address> addresses = addressRepository.findAllByRowIdAndDataTypeIdAndDeletedFalse(rowId, dataTypeId);
        List<Address> toUpdate = addresses.stream()
                .filter(address -> address.isPrimary() && !address.getId().equals(excludeId))
                .peek(address -> address.setPrimary(false))
                .toList();
        addressRepository.saveAll(toUpdate);
    }

}
