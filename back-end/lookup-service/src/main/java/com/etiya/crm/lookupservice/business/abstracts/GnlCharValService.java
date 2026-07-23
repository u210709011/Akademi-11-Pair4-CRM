package com.etiya.crm.lookupservice.business.abstracts;

import com.etiya.crm.shared.contracts.gnlcharval.CreateGnlCharValRequest;
import com.etiya.crm.shared.contracts.gnlcharval.UpdateGnlCharValRequest;
import com.etiya.crm.shared.contracts.gnlcharval.GnlCharValResponse;

import java.util.List;

public interface GnlCharValService {

    List<GnlCharValResponse> getAll();

    GnlCharValResponse getById(Long id);

    GnlCharValResponse add(CreateGnlCharValRequest request);

    GnlCharValResponse update(Long id, UpdateGnlCharValRequest request);

    void delete(Long id);
}
