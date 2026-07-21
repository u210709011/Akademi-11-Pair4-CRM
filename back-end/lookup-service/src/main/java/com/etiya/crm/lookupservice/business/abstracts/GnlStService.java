package com.etiya.crm.lookupservice.business.abstracts;

import com.etiya.crm.lookupservice.business.dtos.requests.CreateGnlStRequest;
import com.etiya.crm.lookupservice.business.dtos.requests.UpdateGnlStRequest;
import com.etiya.crm.lookupservice.business.dtos.responses.GnlStResponse;

import java.util.List;

public interface GnlStService {

    List<GnlStResponse> getAll();

    GnlStResponse getById(Long id);

    GnlStResponse add(CreateGnlStRequest request);

    GnlStResponse update(Long id, UpdateGnlStRequest request);

    void delete(Long id);
}
