package com.etiya.crm.contactinfoservice.api.controllers;

import com.etiya.crm.contactinfoservice.business.abstracts.AddressService;
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
@Tag(name = "Addresses", description = "Adres (ADDR) CRUD - polimorfik, rowId+dataTypeId ile sahiplenilir")
@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @Operation(summary = "Adresleri listele",
            description = "rowId VE dataTypeId ikisi de verilirse sadece o sahibe ait adresler doner "
                    + "(ornegin bir musterinin tum adresleri); ikisi de verilmezse TUM adresler doner.")
    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAll(
            @Parameter(description = "Adresin sahibinin id'si (ornegin custId). dataTypeId ile birlikte kullanilir.", example = "1")
            @RequestParam(required = false) Long rowId,
            @Parameter(description = "lookup-service DATA_TYPE grubundaki deger id'si (musteri icin 102). rowId ile birlikte kullanilir.", example = "102")
            @RequestParam(required = false) Long dataTypeId) {
        if (rowId != null && dataTypeId != null) {
            return ResponseEntity.ok(addressService.getByRowIdAndDataTypeId(rowId, dataTypeId));
        }
        return ResponseEntity.ok(addressService.getAll());
    }

    @Operation(summary = "Adresi id ile getir")
    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.getById(id));
    }

    @Operation(summary = "Yeni adres ekle",
            description = "primary=true gonderilirse ayni rowId+dataTypeId'ye ait diger adreslerin "
                    + "primary'si otomatik false yapilir (tek primary kurali).")
    @PostMapping
    public ResponseEntity<AddressResponse> add(@Valid @RequestBody CreateAddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.add(request));
    }

    @Operation(summary = "Var olan adresi guncelle (primary yapma dahil)")
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateAddressRequest request) {
        return ResponseEntity.ok(addressService.update(id, request));
    }

    @Operation(summary = "Adresi sil (soft-delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        addressService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
