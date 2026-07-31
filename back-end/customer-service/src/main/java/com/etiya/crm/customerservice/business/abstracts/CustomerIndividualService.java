package com.etiya.crm.customerservice.business.abstracts;

import com.etiya.crm.customerservice.business.dtos.requests.UpdateIndividualInfo;
import com.etiya.crm.shared.contracts.individual.IndividualResponse;

/** Musterinin kisisel bilgisini (party-service'in sahip oldugu Individual) proxy'ler. */
public interface CustomerIndividualService {

	IndividualResponse getIndividual(Long custId);

	IndividualResponse updateIndividual(Long custId, UpdateIndividualInfo request);
}
