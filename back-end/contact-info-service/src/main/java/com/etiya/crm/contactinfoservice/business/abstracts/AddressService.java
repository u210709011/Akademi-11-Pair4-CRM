package com.etiya.crm.contactinfoservice.business.abstracts;

import com.etiya.crm.shared.contracts.address.AddressResponse;
import com.etiya.crm.shared.contracts.address.CreateAddressRequest;
import com.etiya.crm.shared.contracts.address.UpdateAddressRequest;

import java.util.List;

public interface AddressService {

    List<AddressResponse> getAll();

    AddressResponse getById(Long id);

    List<AddressResponse> getByRowIdAndDataTypeId(Long rowId, Long dataTypeId);

    AddressResponse add(CreateAddressRequest request);

    AddressResponse update(Long id, UpdateAddressRequest request);

    void delete(Long id);

    void deactivateAllForRow(Long rowId, Long dataTypeId);

}
