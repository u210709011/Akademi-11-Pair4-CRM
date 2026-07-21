package com.etiya.crm.lookupservice.business.abstracts;

import com.etiya.crm.lookupservice.business.dtos.requests.CreateRsrcSpecRequest;
import com.etiya.crm.lookupservice.business.dtos.requests.UpdateRsrcSpecRequest;
import com.etiya.crm.lookupservice.business.dtos.responses.RsrcSpecResponse;

import java.util.List;

public interface RsrcSpecService {

    List<RsrcSpecResponse> getAll();

    RsrcSpecResponse getById(Long id);

    RsrcSpecResponse add(CreateRsrcSpecRequest request);

    RsrcSpecResponse update(Long id, UpdateRsrcSpecRequest request);

    void delete(Long id);
}
