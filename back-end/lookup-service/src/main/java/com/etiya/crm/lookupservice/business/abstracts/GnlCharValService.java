package com.etiya.crm.lookupservice.business.abstracts;

import com.etiya.crm.lookupservice.business.dtos.requests.CreateGnlCharValRequest;
import com.etiya.crm.lookupservice.business.dtos.requests.UpdateGnlCharValRequest;
import com.etiya.crm.lookupservice.business.dtos.responses.GnlCharValResponse;

import java.util.List;

public interface GnlCharValService {

    List<GnlCharValResponse> getAll();

    GnlCharValResponse getById(Long id);

    GnlCharValResponse add(CreateGnlCharValRequest request);

    GnlCharValResponse update(Long id, UpdateGnlCharValRequest request);

    void delete(Long id);
}
