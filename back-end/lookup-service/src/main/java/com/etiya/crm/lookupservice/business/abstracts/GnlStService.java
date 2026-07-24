package com.etiya.crm.lookupservice.business.abstracts;

import com.etiya.crm.shared.contracts.gnlst.CreateGnlStRequest;
import com.etiya.crm.shared.contracts.gnlst.UpdateGnlStRequest;
import com.etiya.crm.shared.contracts.gnlst.GnlStResponse;

import java.util.List;

public interface GnlStService {

    List<GnlStResponse> getAll();

    List<GnlStResponse> getAllByEntCodeName(String entCodeName);

    GnlStResponse getById(Long id);

    GnlStResponse getByEntCodeNameAndShrtCode(String entCodeName, String shrtCode);

    GnlStResponse add(CreateGnlStRequest request);

    GnlStResponse update(Long id, UpdateGnlStRequest request);

    void delete(Long id);
}
