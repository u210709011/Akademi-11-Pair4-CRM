package com.etiya.crm.partyservice.business.abstracts;

import com.etiya.crm.shared.contracts.individual.CreateIndividualCommand;
import com.etiya.crm.shared.contracts.individual.IndividualResponse;
import com.etiya.crm.shared.contracts.individual.PartyRoleResponse;
import com.etiya.crm.shared.contracts.individual.UpdateIndividualCommand;

public interface IndividualService {

    PartyRoleResponse createIndividual(CreateIndividualCommand command);

    boolean existsByNationalId(String nationalId);

    /** customer-service'in edit ekrani icin: partyRoleId -> PartyRole -> Party -> Individual. */
    IndividualResponse getByPartyRoleId(Long partyRoleId);

    /** nationalId/birthDate editlenmez. IndividualUpdated event'i yayinlar (customer-service CustomerSearchView'i senkronlar). */
    IndividualResponse updateByPartyRoleId(Long partyRoleId, UpdateIndividualCommand command);
}
