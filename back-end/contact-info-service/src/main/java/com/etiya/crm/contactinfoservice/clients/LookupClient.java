package com.etiya.crm.contactinfoservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.etiya.crm.shared.contracts.gnltp.GnlTpResponse;

@FeignClient(name = "lookup-service")
public interface LookupClient {

	@GetMapping("/api/v1/general-types/{id}")
	GnlTpResponse getTypeById(@PathVariable("id") Long id);
}
