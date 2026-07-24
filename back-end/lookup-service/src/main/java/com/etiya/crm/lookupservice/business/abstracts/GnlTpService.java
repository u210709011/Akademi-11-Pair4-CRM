package com.etiya.crm.lookupservice.business.abstracts;

import com.etiya.crm.shared.contracts.gnltp.CreateGnlTpRequest;
import com.etiya.crm.shared.contracts.gnltp.UpdateGnlTpRequest;
import com.etiya.crm.shared.contracts.gnltp.GnlTpResponse;

import java.util.List;

public interface GnlTpService {

    List<GnlTpResponse> getAll();

    List<GnlTpResponse> getAllByEntCodeName(String entCodeName);

    GnlTpResponse getById(Long id);

    GnlTpResponse getByEntCodeNameAndShrtCode(String entCodeName, String shrtCode);

    GnlTpResponse add(CreateGnlTpRequest request);

    GnlTpResponse update(Long id, UpdateGnlTpRequest request);

    void delete(Long id);
}
