package com.etiya.crm.customerservice.clients.controllers;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.etiya.crm.customerservice.clients.responses.LookupStValueResponse;
import com.etiya.crm.customerservice.clients.responses.LookupTpValueResponse;

@FeignClient(name = "lookup-service")
public interface LookupClient {

	@GetMapping("/api/gnl_tp/{ent_code_name}/{tp_id}")
	LookupTpValueResponse getTpById(@PathVariable("ent_code_name") String ent_code_name, @PathVariable("tp_id") Long tp_id);

	@GetMapping("/api/gnl_tp/{ent_code_name}/code/{shrt_code}")
	LookupTpValueResponse getTpByCode(@PathVariable("ent_code_name") String ent_code_name, @PathVariable("shrt_code") String shrt_code);


	@GetMapping("/api/gnl_st/{ent_code_name}/{st_id}")
	LookupStValueResponse getStById(@PathVariable("ent_code_name") String ent_code_name, @PathVariable("st_id") Long st_id);

	@GetMapping("/api/gnl_st/{ent_code_name}/code/{shrt_code}")
	LookupStValueResponse getStByCode(@PathVariable("ent_code_name") String ent_code_name, @PathVariable("shrt_code") String shrt_code);
}
