package com.etiya.crm.lookupservice.business.abstracts;

import com.etiya.crm.lookupservice.business.dtos.requests.CreateTypeValueRequest;
import com.etiya.crm.lookupservice.business.dtos.requests.UpdateTypeValueRequest;
import com.etiya.crm.lookupservice.business.dtos.responses.TypeValueResponse;

import java.util.List;

public interface TypeValueService {

    List<TypeValueResponse> getAll();

    TypeValueResponse getById(Long id);

    TypeValueResponse add(CreateTypeValueRequest request);

    TypeValueResponse update(Long id, UpdateTypeValueRequest request);

    void delete(Long id);
}
