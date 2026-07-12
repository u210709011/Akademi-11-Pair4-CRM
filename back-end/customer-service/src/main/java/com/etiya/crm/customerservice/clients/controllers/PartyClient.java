package com.etiya.crm.customerservice.clients.controllers;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.etiya.crm.customerservice.clients.commands.CreateIndividualCommand;
import com.etiya.crm.customerservice.clients.commands.UpdateIndividualCommand;
import com.etiya.crm.customerservice.clients.responses.IndividualResponse;
import com.etiya.crm.customerservice.clients.responses.PartyRoleResponse;

@FeignClient(name = "party-service")
public interface PartyClient {

	@PostMapping("/api/v1/individuals")
	PartyRoleResponse createIndividualWithRole(@RequestBody CreateIndividualCommand command);

	@DeleteMapping("/api/v1/parties/{partyId}")
	void deleteParty(@PathVariable("partyId") Long partyId);

	// ACC-011/012: Nationality ID tekillik kontrolu.
	@GetMapping("/api/v1/individuals/exists")
	boolean existsByNationalId(@RequestParam("nationalId") String nationalId);

	/**
	 * HENUZ party-service TARAFINDA YOK - bkz. CUSTOMER_EDIT_INTEGRATION.md SS1.
	 * Customer entity'si sadece partyRoleId tasidigi (bkz. Customer.partyRoleId),
	 * individual'in kendi id'sini hic bilmedigi icin sorgu/guncelleme partyRoleId
	 * uzerinden yapilir; party-service bunu PartyRole -> Party -> Individual
	 * join'iyle cozer.
	 */
	@GetMapping("/api/v1/individuals/by-party-role/{partyRoleId}")
	IndividualResponse getIndividualByPartyRoleId(@PathVariable("partyRoleId") Long partyRoleId);

	// HENUZ party-service TARAFINDA YOK - bkz. CUSTOMER_EDIT_INTEGRATION.md SS1.
	@PutMapping("/api/v1/individuals/by-party-role/{partyRoleId}")
	IndividualResponse updateIndividual(@PathVariable("partyRoleId") Long partyRoleId,
			@RequestBody UpdateIndividualCommand command);
}
