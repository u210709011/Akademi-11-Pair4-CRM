package com.etiya.crm.contactinfoservice.business.abstracts;

import com.etiya.crm.contactinfoservice.business.dtos.requests.CreateContactMediumRequest;
import com.etiya.crm.contactinfoservice.business.dtos.requests.UpdateContactMediumRequest;
import com.etiya.crm.contactinfoservice.business.dtos.responses.ContactMediumResponse;

import java.util.List;

public interface ContactMediumService {

    List<ContactMediumResponse> getAll();

    ContactMediumResponse getById(Long id);

    List<ContactMediumResponse> getByRowIdAndDataTypeId(Long rowId, Long dataTypeId);

    ContactMediumResponse add(CreateContactMediumRequest request);

    ContactMediumResponse update(Long id, UpdateContactMediumRequest request);

    void delete(Long id);

}
