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

import java.time.LocalDateTime;
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
        return addressRepository.findAllByDeletedFalse().stream()
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
        return addressRepository.findAllByRowIdAndDataTypeIdAndDeletedFalse(rowId, dataTypeId).stream()
                .map(AddressMapper::toResponse)
                .toList();
    }

    @Override
    public AddressResponse add(CreateAddressRequest request) {
        Address address = AddressMapper.toEntity(request);
        address.setCdate(LocalDateTime.now());
        Address saved = addressRepository.save(address);

        if (saved.isPrimary()) {
            addressBusinessRules.unsetOtherPrimaryAddresses(saved.getRowId(), saved.getDataTypeId(), saved.getId());
        }

        return AddressMapper.toResponse(saved);
    }

    @Override
    public AddressResponse update(Long id, UpdateAddressRequest request) {
        Address address = addressBusinessRules.checkIfAddressExists(id);
        AddressMapper.updateEntity(address, request);
        address.setUdate(LocalDateTime.now());
        Address saved = addressRepository.save(address);

        if (saved.isPrimary()) {
            addressBusinessRules.unsetOtherPrimaryAddresses(saved.getRowId(), saved.getDataTypeId(), saved.getId());
        }

        return AddressMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        Address address = addressBusinessRules.checkIfAddressExists(id);
        address.setDeleted(true);
        addressRepository.save(address);
    }

}
