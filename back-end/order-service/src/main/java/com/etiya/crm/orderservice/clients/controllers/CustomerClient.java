package com.etiya.crm.orderservice.clients.controllers;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.etiya.crm.orderservice.clients.responses.CustomerAccountResponse;
import com.etiya.crm.orderservice.clients.responses.CustomerResponse;

@FeignClient(name= "customer-service")
public interface CustomerClient {
    
    //is customer valid check in submit order
    @GetMapping("/api/v1/customers/{custId}")
    CustomerResponse getById(@PathVariable("custId") Long custId);
    
    //is id belong to this customer
    @GetMapping("/api/v1/customers/{custId}/accounts")
    List<CustomerAccountResponse> getAccounts(@PathVariable("custId") Long custId);

}
