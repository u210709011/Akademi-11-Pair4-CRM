package com.etiya.crm.orderservice.clients.controllers;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.etiya.crm.orderservice.clients.responses.GnlStResponse;
import com.etiya.crm.orderservice.clients.responses.GnlTpResponse;
import com.etiya.crm.orderservice.clients.responses.TypeValueResponse;

@FeignClient(name = "lookup-service")
public interface LookupClient {

    @GetMapping("/api/v1/general-types")
    List<GnlTpResponse> getAllGeneralTypes(@RequestParam(required = false) String entCodeName);

    @GetMapping("/api/v1/general-types/resolve/{entCodeName}/{shrtCode}")
    GnlTpResponse resolveGeneralType(@PathVariable String entCodeName, @PathVariable String shrtCode);

    @GetMapping("/api/v1/general-statuses")
    List<GnlStResponse> getAllGeneralStatuses(@RequestParam(required = false) String entCodeName);

    @GetMapping("/api/v1/general-statuses/resolve/{entCodeName}/{shrtCode}")
    GnlStResponse resolveGeneralStatus(@PathVariable String entCodeName, @PathVariable String shrtCode);

    @GetMapping("/api/v1/type-values")
    List<TypeValueResponse> getAllTypeValues();

}
