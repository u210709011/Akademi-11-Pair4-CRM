package com.etiya.crm.lookupservice.business.abstracts;

import com.etiya.crm.lookupservice.business.dtos.requests.CreateGnlTpRequest;
import com.etiya.crm.lookupservice.business.dtos.requests.UpdateGnlTpRequest;
import com.etiya.crm.lookupservice.business.dtos.responses.GnlTpResponse;

import java.util.List;

public interface GnlTpService {

    List<GnlTpResponse> getAll();

    GnlTpResponse getById(Long id);

    GnlTpResponse add(CreateGnlTpRequest request);

    GnlTpResponse update(Long id, UpdateGnlTpRequest request);

    void delete(Long id);
}
