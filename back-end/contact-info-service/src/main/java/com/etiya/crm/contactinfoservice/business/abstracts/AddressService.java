package com.etiya.crm.contactinfoservice.business.abstracts;

import com.etiya.crm.contactinfoservice.business.dtos.requests.CreateAddressRequest;
import com.etiya.crm.contactinfoservice.business.dtos.requests.UpdateAddressRequest;
import com.etiya.crm.contactinfoservice.business.dtos.responses.AddressResponse;

import java.util.List;

public interface AddressService {

    List<AddressResponse> getAll();

    AddressResponse getById(Long id);

    List<AddressResponse> getByRowIdAndDataTypeId(Long rowId, Long dataTypeId);

    AddressResponse add(CreateAddressRequest request);

    AddressResponse update(Long id, UpdateAddressRequest request);

    void delete(Long id);

}
