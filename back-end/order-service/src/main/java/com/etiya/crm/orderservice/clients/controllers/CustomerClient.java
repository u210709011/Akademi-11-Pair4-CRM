package com.etiya.crm.orderservice.clients.controllers;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.etiya.crm.orderservice.clients.responses.CustomerAccountResponse;
import com.etiya.crm.orderservice.clients.responses.CustomerResponse;
import com.etiya.crm.orderservice.clients.responses.PageResponse;

@FeignClient(name= "customer-service")
public interface CustomerClient {

    //is customer valid check in submit order
    @GetMapping("/api/v1/customers/{custId}")
    CustomerResponse getById(@PathVariable("custId") Long custId);

    // customer-service artik Page<CustomerAccountResponse> donuyor, varsayilan size=5, FR-009
    @GetMapping("/api/v1/customers/{custId}/accounts")
    PageResponse<CustomerAccountResponse> getAccounts(@PathVariable("custId") Long custId,
            @RequestParam(defaultValue = "1000") int size);

}
