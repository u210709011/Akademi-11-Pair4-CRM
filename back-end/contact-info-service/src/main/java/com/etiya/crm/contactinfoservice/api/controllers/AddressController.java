package com.etiya.crm.contactinfoservice.api.controllers;

import com.etiya.crm.contactinfoservice.business.abstracts.AddressService;
import com.etiya.crm.contactinfoservice.business.dtos.requests.CreateAddressRequest;
import com.etiya.crm.contactinfoservice.business.dtos.requests.UpdateAddressRequest;
import com.etiya.crm.contactinfoservice.business.dtos.responses.AddressResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAll(
            @RequestParam(required = false) Long rowId,
            @RequestParam(required = false) Long dataTypeId) {
        if (rowId != null && dataTypeId != null) {
            return ResponseEntity.ok(addressService.getByRowIdAndDataTypeId(rowId, dataTypeId));
        }
        return ResponseEntity.ok(addressService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.getById(id));
    }

    @PostMapping
    public ResponseEntity<AddressResponse> add(@Valid @RequestBody CreateAddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.add(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateAddressRequest request) {
        return ResponseEntity.ok(addressService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        addressService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
