package com.etiya.crm.partyservice.business.abstracts;

import com.etiya.crm.partyservice.business.dtos.requests.CreateIndividualCommand;
import com.etiya.crm.partyservice.business.dtos.responses.PartyRoleResponse;

public interface IndividualService {

    PartyRoleResponse createIndividual(CreateIndividualCommand command);

    boolean existsByNationalId(String nationalId);
}
