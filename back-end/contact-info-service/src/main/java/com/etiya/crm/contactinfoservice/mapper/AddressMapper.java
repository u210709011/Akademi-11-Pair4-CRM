package com.etiya.crm.contactinfoservice.mapper;

import com.etiya.crm.contactinfoservice.entities.concretes.Address;
import com.etiya.crm.shared.contracts.address.AddressResponse;
import com.etiya.crm.shared.contracts.address.CreateAddressRequest;
import com.etiya.crm.shared.contracts.address.UpdateAddressRequest;

public class AddressMapper {

    private AddressMapper() {
    }

    public static Address toEntity(CreateAddressRequest request) {
        Address address = new Address();
        address.setRowId(request.rowId());
        address.setDataTypeId(request.dataTypeId());
        address.setCityId(request.cityId());
        address.setStreetName(request.streetName());
        address.setHouseName(request.houseName());
        address.setAddrDesc(request.addrDesc());
        address.setPrimary(request.primary());
        return address;
    }

    public static void updateEntity(Address address, UpdateAddressRequest request) {
        address.setCityId(request.cityId());
        address.setStreetName(request.streetName());
        address.setHouseName(request.houseName());
        address.setAddrDesc(request.addrDesc());
        address.setPrimary(request.primary());
    }

    public static AddressResponse toResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getRowId(),
                address.getDataTypeId(),
                address.getCityId(),
                address.getStreetName(),
                address.getHouseName(),
                address.getAddrDesc(),
                address.isPrimary(),
                address.getCdate(),
                address.getCuser(),
                address.getUdate(),
                address.getUuser());
    }

}
