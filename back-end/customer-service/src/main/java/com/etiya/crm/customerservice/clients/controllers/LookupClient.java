package com.etiya.crm.customerservice.clients.controllers;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.etiya.crm.customerservice.clients.responses.LookupValueResponse;

@FeignClient(name = "lookup-service")
public interface LookupClient {

	@GetMapping("/api/v1/lookups/{groupCode}/{valueId}")
	LookupValueResponse getById(@PathVariable("groupCode") String groupCode, @PathVariable("valueId") Long valueId);

	@GetMapping("/api/v1/lookups/{groupCode}/code/{code}")
	LookupValueResponse getByCode(@PathVariable("groupCode") String groupCode, @PathVariable("code") String code);
}
