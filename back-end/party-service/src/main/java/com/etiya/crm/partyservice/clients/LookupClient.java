package com.etiya.crm.partyservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** lookup-service REST kontrati (CUSTOMER_SERVICE_CONTRACTS.md SS4). */
@FeignClient(name = "lookup-service")
public interface LookupClient {

    @GetMapping("/api/v1/lookups/{groupCode}/code/{code}")
    LookupValueResponse getByCode(@PathVariable("groupCode") String groupCode,
                                  @PathVariable("code") String code);
}