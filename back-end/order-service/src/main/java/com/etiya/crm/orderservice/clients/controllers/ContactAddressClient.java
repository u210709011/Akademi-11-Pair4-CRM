package com.etiya.crm.orderservice.clients.controllers;
// CreatedAddressRequest and AddressResponse directly reused from shared-contacts

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.etiya.crm.shared.contracts.address.AddressResponse;
import com.etiya.crm.shared.contracts.address.CreateAddressRequest;

@FeignClient(name = "contact-info-service")
public interface ContactAddressClient {

    //to create a new address in contact-info-service
    @PostMapping("/api/v1/addresses")
    AddressResponse createAddress(@RequestBody CreateAddressRequest request);

    // var olan bir addressId secildiginde tam detaylarini (city/street/houseName/addrDesc) cekmek icin
    @GetMapping("/api/v1/addresses/{id}")
    AddressResponse getById(@PathVariable("id") Long id);

}
