package com.etiya.crm.orderservice.clients.controllers;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.etiya.crm.orderservice.clients.responses.CustomerAccountPageResponse;
import com.etiya.crm.orderservice.clients.responses.CustomerResponse;

@FeignClient(name= "customer-service")
public interface CustomerClient {

    //is customer valid check in submit order
    @GetMapping("/api/v1/customers/{custId}")
    CustomerResponse getById(@PathVariable("custId") Long custId);

    // customer-service Page<CustomerAccountResponse> donuyor (bkz. CustomerAccountController.getAccounts) -
    // Jackson bunu {"content":[...],"pageable":{...},...} olarak serialize ediyor, duz array degil.
    @GetMapping("/api/v1/customers/{custId}/accounts")
    CustomerAccountPageResponse getAccounts(@PathVariable("custId") Long custId,
            @RequestParam(defaultValue = "1000") int size);

}
