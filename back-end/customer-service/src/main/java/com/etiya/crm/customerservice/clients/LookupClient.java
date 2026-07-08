package com.etiya.crm.customerservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "lookup-service")
public interface LookupClient {

	@GetMapping("/api/lookups/{groupCode}/{valueId}")
	LookupValueResponse getById(@PathVariable("groupCode") String groupCode, @PathVariable("valueId") Long valueId);

	@GetMapping("/api/lookups/{groupCode}/code/{code}")
	LookupValueResponse getByCode(@PathVariable("groupCode") String groupCode, @PathVariable("code") String code);
}
