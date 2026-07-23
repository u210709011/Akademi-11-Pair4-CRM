package com.etiya.crm.lookupservice.business.abstracts;

import com.etiya.crm.shared.contracts.rsrcspec.CreateRsrcSpecRequest;
import com.etiya.crm.shared.contracts.rsrcspec.UpdateRsrcSpecRequest;
import com.etiya.crm.shared.contracts.rsrcspec.RsrcSpecResponse;

import java.util.List;

public interface RsrcSpecService {

    List<RsrcSpecResponse> getAll();

    RsrcSpecResponse getById(Long id);

    RsrcSpecResponse add(CreateRsrcSpecRequest request);

    RsrcSpecResponse update(Long id, UpdateRsrcSpecRequest request);

    void delete(Long id);
}
