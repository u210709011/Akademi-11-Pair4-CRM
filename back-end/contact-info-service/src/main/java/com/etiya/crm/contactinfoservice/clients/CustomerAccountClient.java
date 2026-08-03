package com.etiya.crm.contactinfoservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service")
public interface CustomerAccountClient {

	@GetMapping("/api/v1/customers/accounts/exists-by-address/{addressId}")
	boolean existsByAddressId(@PathVariable("addressId") Long addressId);
}
