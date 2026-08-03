package com.etiya.crm.orderservice.clients.controllers;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.etiya.crm.orderservice.clients.responses.CustomerAccountResponse;
import com.etiya.crm.orderservice.clients.responses.CustomerResponse;

@FeignClient(name= "customer-service")
public interface CustomerClient {

    //is customer valid check in submit order
    @GetMapping("/api/v1/customers/{custId}")
    CustomerResponse getById(@PathVariable("custId") Long custId);

    // customer-service Page<CustomerAccountResponse> donuyor ama @EnableSpringDataWebSupport
    // VIA_DTO acik olmadigi icin Jackson bunu duz JSON array olarak serialize ediyor (content
    // sarmalayicisi yok) - o yuzden burada da PageResponse<> degil dogrudan List bekleniyor.
    @GetMapping("/api/v1/customers/{custId}/accounts")
    List<CustomerAccountResponse> getAccounts(@PathVariable("custId") Long custId,
            @RequestParam(defaultValue = "1000") int size);

}
