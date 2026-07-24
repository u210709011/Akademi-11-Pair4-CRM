package com.etiya.crm.lookupservice.business.abstracts;

import com.etiya.crm.shared.contracts.srvcspec.CreateSrvcSpecRequest;
import com.etiya.crm.shared.contracts.srvcspec.UpdateSrvcSpecRequest;
import com.etiya.crm.shared.contracts.srvcspec.SrvcSpecResponse;

import java.util.List;

public interface SrvcSpecService {

    List<SrvcSpecResponse> getAll();

    SrvcSpecResponse getById(Long id);

    SrvcSpecResponse add(CreateSrvcSpecRequest request);

    SrvcSpecResponse update(Long id, UpdateSrvcSpecRequest request);

    void delete(Long id);
}
