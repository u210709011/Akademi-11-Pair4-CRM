package com.etiya.crm.contactinfoservice.business.abstracts;

import com.etiya.crm.shared.contracts.contactmedium.ContactMediumResponse;
import com.etiya.crm.shared.contracts.contactmedium.CreateContactMediumRequest;
import com.etiya.crm.shared.contracts.contactmedium.UpdateContactMediumRequest;

import java.util.List;

public interface ContactMediumService {

    List<ContactMediumResponse> getAll();

    ContactMediumResponse getById(Long id);

    List<ContactMediumResponse> getByRowIdAndDataTypeId(Long rowId, Long dataTypeId);

    ContactMediumResponse add(CreateContactMediumRequest request);

    ContactMediumResponse update(Long id, UpdateContactMediumRequest request);

    void delete(Long id);

    void deactivateAllForRow(Long rowId, Long dataTypeId);

}
