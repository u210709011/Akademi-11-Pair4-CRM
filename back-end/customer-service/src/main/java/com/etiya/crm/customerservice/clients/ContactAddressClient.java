package com.etiya.crm.customerservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "contact-address-service")
public interface ContactAddressClient {

	@PostMapping("/api/contacts")
	void createContact(@RequestBody CreateContactCommand command);

	// Onboarding compensation: customer-service tarafi basarisiz olursa geri alinir.
	@DeleteMapping("/api/contacts/customer/{custId}")
	void deleteByCustomerId(@PathVariable("custId") Long custId);
}
