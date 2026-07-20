package com.etiya.crm.lookupservice.business.abstracts;

import com.etiya.crm.lookupservice.business.dtos.requests.CreateGnlCharRequest;
import com.etiya.crm.lookupservice.business.dtos.requests.UpdateGnlCharRequest;
import com.etiya.crm.lookupservice.business.dtos.responses.GnlCharResponse;

import java.util.List;

public interface GnlCharService {

    List<GnlCharResponse> getAll();

    GnlCharResponse getById(Long id);

    GnlCharResponse add(CreateGnlCharRequest request);

    GnlCharResponse update(Long id, UpdateGnlCharRequest request);

    void delete(Long id);
}
