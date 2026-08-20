package com.etiya.crm.contactinfoservice.api.controllers;

import com.etiya.crm.contactinfoservice.business.abstracts.AddressService;
import com.etiya.crm.contactinfoservice.constants.SwaggerText;
import com.etiya.crm.shared.contracts.address.AddressResponse;
import com.etiya.crm.shared.contracts.address.CreateAddressRequest;
import com.etiya.crm.shared.contracts.address.UpdateAddressRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

/**
 * Adres (ADDR) CRUD'u. Polimorfik tablo: rowId+dataTypeId, adresin kime ait
 * oldugunu belirtir - bu servis rowId'nin bir customer, party ya da baska
 * bir sey oldugunu bilmez. Ana caller customer-service'tir (bkz.
 * back-end/CUSTOMER_EDIT_INTEGRATION.md).
 */
@Tag(name = SwaggerText.ADDRESS_TAG_NAME, description = SwaggerText.ADDRESS_TAG_DESCRIPTION)
@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @Operation(summary = SwaggerText.GET_ALL_ADDRESSES_SUMMARY,
            description = SwaggerText.GET_ALL_ADDRESSES_DESCRIPTION)
    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAll(
            @Parameter(description = SwaggerText.ADDRESS_ROW_ID_PARAM_DESCRIPTION, example = "1")
            @RequestParam(required = false) Long rowId,
            @Parameter(description = SwaggerText.DATA_TYPE_ID_PARAM_DESCRIPTION, example = "12")
            @RequestParam(required = false) Long dataTypeId) {
        if (rowId != null && dataTypeId != null) {
            return ResponseEntity.ok(addressService.getByRowIdAndDataTypeId(rowId, dataTypeId));
        }
        return ResponseEntity.ok(addressService.getAll());
    }

    @Operation(summary = SwaggerText.GET_ADDRESS_BY_ID_SUMMARY)
    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.getById(id));
    }

    @Operation(summary = SwaggerText.CREATE_ADDRESS_SUMMARY,
            description = SwaggerText.CREATE_ADDRESS_DESCRIPTION)
    @PostMapping
    public ResponseEntity<AddressResponse> add(@Valid @RequestBody CreateAddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.add(request));
    }

    @Operation(summary = SwaggerText.UPDATE_ADDRESS_SUMMARY)
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateAddressRequest request) {
        return ResponseEntity.ok(addressService.update(id, request));
    }

    @Operation(summary = SwaggerText.DELETE_ADDRESS_SUMMARY)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        addressService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
