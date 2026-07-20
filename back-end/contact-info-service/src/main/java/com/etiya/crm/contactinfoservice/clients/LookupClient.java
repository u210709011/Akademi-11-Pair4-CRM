package com.etiya.crm.contactinfoservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "lookup-service")
public interface LookupClient {

	@GetMapping("/api/v1/lookups/{groupCode}/{valueId}")
	LookupValueResponse getById(@PathVariable("groupCode") String groupCode, @PathVariable("valueId") Long valueId);
}
