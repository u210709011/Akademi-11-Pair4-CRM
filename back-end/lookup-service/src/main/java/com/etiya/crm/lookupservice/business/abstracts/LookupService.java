package com.etiya.crm.lookupservice.business.abstracts;

import com.etiya.crm.shared.contracts.lookup.LookupValueResponse;

public interface LookupService {

    LookupValueResponse getByValueId(String groupCode, Long valueId);

    LookupValueResponse getByCode(String groupCode, String code);
}
