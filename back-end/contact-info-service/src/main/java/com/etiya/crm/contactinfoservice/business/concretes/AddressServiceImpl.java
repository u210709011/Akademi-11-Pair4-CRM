package com.etiya.crm.contactinfoservice.business.concretes;

import com.etiya.crm.contactinfoservice.business.abstracts.AddressService;
import com.etiya.crm.contactinfoservice.business.dtos.requests.CreateAddressRequest;
import com.etiya.crm.contactinfoservice.business.dtos.requests.UpdateAddressRequest;
import com.etiya.crm.contactinfoservice.business.dtos.responses.AddressResponse;
import com.etiya.crm.contactinfoservice.business.rules.AddressBusinessRules;
import com.etiya.crm.contactinfoservice.dataAccess.abstracts.AddressRepository;
import com.etiya.crm.contactinfoservice.entities.concretes.Address;
import com.etiya.crm.contactinfoservice.mapper.AddressMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressBusinessRules addressBusinessRules;

    public AddressServiceImpl(AddressRepository addressRepository, AddressBusinessRules addressBusinessRules) {
        this.addressRepository = addressRepository;
        this.addressBusinessRules = addressBusinessRules;
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
                request.getRowId(), request.getDataTypeId());
        addressBusinessRules.checkAddressLimitNotExceeded(existingAddresses);

        Address address = AddressMapper.toEntity(request);
        if (existingAddresses.isEmpty()) {
            address.setPrimary(true);
        }

        Address saved = addressRepository.save(address);

        if (saved.isPrimary()) {
            addressBusinessRules.unsetOtherPrimaryAddresses(saved.getRowId(), saved.getDataTypeId(), saved.getId());
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
            addressBusinessRules.unsetOtherPrimaryAddresses(saved.getRowId(), saved.getDataTypeId(), saved.getId());
        }

        return AddressMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        Address address = addressBusinessRules.checkIfAddressExists(id);
        addressBusinessRules.checkIfNotPrimary(address);
        address.setActive(false);
        addressRepository.save(address);
    }

    @Override
    public void deactivateAllForRow(Long rowId, Long dataTypeId) {
        List<Address> addresses = addressRepository.findAllByRowIdAndDataTypeIdAndActiveTrue(rowId, dataTypeId);
        addresses.forEach(address -> address.setActive(false));
        addressRepository.saveAll(addresses);
    }

}
