package com.etiya.crm.lookupservice.business.abstracts;

import com.etiya.crm.lookupservice.business.dtos.requests.CreateSrvcSpecRequest;
import com.etiya.crm.lookupservice.business.dtos.requests.UpdateSrvcSpecRequest;
import com.etiya.crm.lookupservice.business.dtos.responses.SrvcSpecResponse;

import java.util.List;

public interface SrvcSpecService {

    List<SrvcSpecResponse> getAll();

    SrvcSpecResponse getById(Long id);

    SrvcSpecResponse add(CreateSrvcSpecRequest request);

    SrvcSpecResponse update(Long id, UpdateSrvcSpecRequest request);

    void delete(Long id);
}
