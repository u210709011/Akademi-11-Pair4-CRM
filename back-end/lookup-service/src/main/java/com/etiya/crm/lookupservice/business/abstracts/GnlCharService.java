package com.etiya.crm.lookupservice.business.abstracts;

import com.etiya.crm.shared.contracts.gnlchar.CreateGnlCharRequest;
import com.etiya.crm.shared.contracts.gnlchar.UpdateGnlCharRequest;
import com.etiya.crm.shared.contracts.gnlchar.GnlCharResponse;

import java.util.List;

public interface GnlCharService {

    List<GnlCharResponse> getAll();

    GnlCharResponse getById(Long id);

    GnlCharResponse add(CreateGnlCharRequest request);

    GnlCharResponse update(Long id, UpdateGnlCharRequest request);

    void delete(Long id);
}
