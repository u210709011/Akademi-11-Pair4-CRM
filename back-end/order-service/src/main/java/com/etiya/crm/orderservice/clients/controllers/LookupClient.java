package com.etiya.crm.orderservice.clients.controllers;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.etiya.crm.orderservice.clients.responses.GnlStResponse;
import com.etiya.crm.orderservice.clients.responses.GnlTpResponse;
import com.etiya.crm.orderservice.clients.responses.TypeValueResponse;

@FeignClient(name = "lookup-service")
public interface LookupClient {
    
    @GetMapping("/api/v1/general-types")
    List<GnlTpResponse> getAllGeneralTypes();
    
    @GetMapping("/api/v1/general-statuses")
    List<GnlStResponse> getAllGeneralStatuses();
    
    @GetMapping("/api/v1/type-values")
    List<TypeValueResponse> getAllTypeValues();

}
