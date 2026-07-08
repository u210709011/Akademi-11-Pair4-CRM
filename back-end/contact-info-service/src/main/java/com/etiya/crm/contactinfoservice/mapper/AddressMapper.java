package com.etiya.crm.contactinfoservice.mapper;

import com.etiya.crm.contactinfoservice.business.dtos.requests.CreateAddressRequest;
import com.etiya.crm.contactinfoservice.business.dtos.requests.UpdateAddressRequest;
import com.etiya.crm.contactinfoservice.business.dtos.responses.AddressResponse;
import com.etiya.crm.contactinfoservice.entities.concretes.Address;

public class AddressMapper {

    private AddressMapper() {
    }

    public static Address toEntity(CreateAddressRequest request) {
        Address address = new Address();
        address.setRowId(request.getRowId());
        address.setDataTypeId(request.getDataTypeId());
        address.setCityId(request.getCityId());
        address.setStreetName(request.getStreetName());
        address.setHouseName(request.getHouseName());
        address.setAddrDesc(request.getAddrDesc());
        address.setPrimary(request.isPrimary());
        return address;
    }

    public static void updateEntity(Address address, UpdateAddressRequest request) {
        address.setCityId(request.getCityId());
        address.setStreetName(request.getStreetName());
        address.setHouseName(request.getHouseName());
        address.setAddrDesc(request.getAddrDesc());
        address.setPrimary(request.isPrimary());
    }

    public static AddressResponse toResponse(Address address) {
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setRowId(address.getRowId());
        response.setDataTypeId(address.getDataTypeId());
        response.setCityId(address.getCityId());
        response.setStreetName(address.getStreetName());
        response.setHouseName(address.getHouseName());
        response.setAddrDesc(address.getAddrDesc());
        response.setPrimary(address.isPrimary());
        response.setCdate(address.getCdate());
        response.setCuser(address.getCuser());
        response.setUdate(address.getUdate());
        response.setUuser(address.getUuser());
        return response;
    }

}
