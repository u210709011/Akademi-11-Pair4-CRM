package com.etiya.crm.contactinfoservice.business.concretes;

import com.etiya.crm.contactinfoservice.business.abstracts.AddressService;
import com.etiya.crm.contactinfoservice.business.rules.AddressBusinessRules;
import com.etiya.crm.contactinfoservice.clients.CustomerAccountClient;
import com.etiya.crm.contactinfoservice.dataAccess.abstracts.AddressRepository;
import com.etiya.crm.contactinfoservice.entities.concretes.Address;
import com.etiya.crm.contactinfoservice.mapper.AddressMapper;
import com.etiya.crm.shared.contracts.address.AddressResponse;
import com.etiya.crm.shared.contracts.address.CreateAddressRequest;
import com.etiya.crm.shared.contracts.address.UpdateAddressRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressBusinessRules addressBusinessRules;
    private final CustomerAccountClient customerAccountClient;

    public AddressServiceImpl(AddressRepository addressRepository, AddressBusinessRules addressBusinessRules,
                               CustomerAccountClient customerAccountClient) {
        this.addressRepository = addressRepository;
        this.addressBusinessRules = addressBusinessRules;
        this.customerAccountClient = customerAccountClient;
    }

    @Override
    public List<AddressResponse> getAll() {
        return addressRepository.findAllByActiveTrue().stream()
                .map(AddressMapper::toResponse)
                .toList();
    }

    @Override
    public AddressResponse getById(Long id) {
        Address address = addressBusinessRules.checkIfAddressExists(id);
        return AddressMapper.toResponse(address);
    }

    @Override
    public List<AddressResponse> getByRowIdAndDataTypeId(Long rowId, Long dataTypeId) {
        return addressRepository.findAllByRowIdAndDataTypeIdAndActiveTrue(rowId, dataTypeId).stream()
                .map(AddressMapper::toResponse)
                .toList();
    }

    @Override
    public AddressResponse add(CreateAddressRequest request) {
        List<Address> existingAddresses = addressRepository.findAllByRowIdAndDataTypeIdAndActiveTrue(
                request.rowId(), request.dataTypeId());
        addressBusinessRules.checkAddressLimitNotExceeded(existingAddresses);

        Address address = AddressMapper.toEntity(request);
        if (existingAddresses.isEmpty()) {
            address.setPrimary(true);
        }

        Address saved = addressRepository.save(address);

        if (saved.isPrimary()) {
            unsetOtherPrimaryAddresses(saved.getRowId(), saved.getDataTypeId(), saved.getId());
        }

        return AddressMapper.toResponse(saved);
    }

    @Override
    public AddressResponse update(Long id, UpdateAddressRequest request) {
        Address address = addressBusinessRules.checkIfAddressExists(id);
        List<Address> existingAddresses = addressRepository.findAllByRowIdAndDataTypeIdAndActiveTrue(
                address.getRowId(), address.getDataTypeId());

        AddressMapper.updateEntity(address, request);
        if (existingAddresses.size() == 1) {
            address.setPrimary(true);
        }

        Address saved = addressRepository.save(address);

        if (saved.isPrimary()) {
            unsetOtherPrimaryAddresses(saved.getRowId(), saved.getDataTypeId(), saved.getId());
        }

        return AddressMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        Address address = addressBusinessRules.checkIfAddressExists(id);
        addressBusinessRules.checkIfNotPrimary(address);
        addressBusinessRules.ensureNotLinkedToAccount(customerAccountClient.existsByAddressId(id));
        address.setActive(false);
        addressRepository.save(address);
    }

    @Override
    public void deactivateAllForRow(Long rowId, Long dataTypeId) {
        List<Address> addresses = addressRepository.findAllByRowIdAndDataTypeIdAndActiveTrue(rowId, dataTypeId);
        addresses.forEach(address -> address.setActive(false));
        addressRepository.saveAll(addresses);
    }

    private void unsetOtherPrimaryAddresses(Long rowId, Long dataTypeId, Long excludeId) {
        List<Address> addresses = addressRepository.findAllByRowIdAndDataTypeIdAndActiveTrue(rowId, dataTypeId);
        List<Address> toUpdate = addresses.stream()
                .filter(address -> address.isPrimary() && !address.getId().equals(excludeId))
                .peek(address -> address.setPrimary(false))
                .toList();
        addressRepository.saveAll(toUpdate);
    }

}
