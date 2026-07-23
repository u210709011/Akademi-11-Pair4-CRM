package com.etiya.crm.lookupservice.business.abstracts;

import com.etiya.crm.shared.contracts.typevalue.CreateTypeValueRequest;
import com.etiya.crm.shared.contracts.typevalue.UpdateTypeValueRequest;
import com.etiya.crm.shared.contracts.typevalue.TypeValueResponse;

import java.util.List;

public interface TypeValueService {

    List<TypeValueResponse> getAll();

    TypeValueResponse getById(Long id);

    TypeValueResponse add(CreateTypeValueRequest request);

    TypeValueResponse update(Long id, UpdateTypeValueRequest request);

    void delete(Long id);
}
