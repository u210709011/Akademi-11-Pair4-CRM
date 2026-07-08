package com.etiya.crm.customerservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "party-service")
public interface PartyClient {

	@PostMapping("/api/individuals")
	PartyRoleResponse createIndividualWithRole(@RequestBody CreateIndividualCommand command);

	@DeleteMapping("/api/parties/{partyId}")
	void deleteParty(@PathVariable("partyId") Long partyId);

	// ACC-011/012: Nationality ID tekillik kontrolu.
	@GetMapping("/api/individuals/exists")
	boolean existsByNationalId(@RequestParam("nationalId") String nationalId);
}
