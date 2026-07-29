package com.etiya.crm.customerservice.business.concretes;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etiya.crm.customerservice.business.abstracts.CustomerIndividualService;
import com.etiya.crm.customerservice.business.dtos.requests.UpdateIndividualInfo;
import com.etiya.crm.customerservice.business.rules.CustomerBusinessRules;
import com.etiya.crm.customerservice.clients.controllers.PartyClient;
import com.etiya.crm.shared.contracts.individual.IndividualResponse;
import com.etiya.crm.shared.contracts.individual.UpdateIndividualCommand;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerIndividualServiceImpl implements CustomerIndividualService {

	private final PartyClient partyClient;
	private final CustomerBusinessRules rules;

	@Override
	@Transactional(readOnly = true)
	public IndividualResponse getIndividual(Long partyRoleId) {
		return partyClient.getIndividualByPartyRoleId(partyRoleId);
	}

	@Override
	@Transactional(readOnly = true)
	public IndividualResponse updateIndividual(Long partyRoleId, UpdateIndividualInfo request) {
		rules.validateBirthDate(request.birthDate());
		UpdateIndividualCommand command = new UpdateIndividualCommand(request.firstName(), request.middleName(),
				request.lastName(), request.genderId(), request.motherName(), request.fatherName(),
				request.birthDate(), request.nationalId());
		// CustomerSearchView senkronu burada YAPILMAZ: party-service'in yayinlayacagi
		// IndividualUpdated event'i PartyEventListener tarafindan async islenir.
		return partyClient.updateIndividual(partyRoleId, command);
	}
}
