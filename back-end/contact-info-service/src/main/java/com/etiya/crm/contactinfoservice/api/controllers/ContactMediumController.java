package com.etiya.crm.contactinfoservice.api.controllers;

import com.etiya.crm.contactinfoservice.business.abstracts.AddressService;
import com.etiya.crm.contactinfoservice.business.abstracts.ContactMediumService;
import com.etiya.crm.shared.contracts.address.CreateAddressRequest;
import com.etiya.crm.shared.contracts.contactmedium.AddressCommand;
import com.etiya.crm.shared.contracts.contactmedium.ContactMediumCommand;
import com.etiya.crm.shared.contracts.contactmedium.ContactMediumResponse;
import com.etiya.crm.shared.contracts.contactmedium.CreateContactCommand;
import com.etiya.crm.shared.contracts.contactmedium.CreateContactMediumRequest;
import com.etiya.crm.shared.contracts.contactmedium.UpdateContactMediumRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
 * Contact medium (CNTC_MEDIUM: e-posta, telefon, faks) CRUD'u + customer
 * onboarding'i icin composite create/delete. Polimorfik tablo: rowId+dataTypeId,
 * kaydin kime ait oldugunu belirtir.
 */
@Tag(name = "Contact Mediums", description = "Iletisim (CNTC_MEDIUM) CRUD + onboarding composite create/delete")
@RestController
@RequestMapping("/api/v1/contact-mediums")
public class ContactMediumController {

    private final ContactMediumService contactMediumService;
    private final AddressService addressService;

    public ContactMediumController(ContactMediumService contactMediumService, AddressService addressService) {
        this.contactMediumService = contactMediumService;
        this.addressService = addressService;
    }

    /**
     * customer-service'in onboarding sirasinda cagirdigi toplu olusturma
     * uc noktasi: bir musterinin adreslerini ve contact medium'larini tek
     * seferde, atomik olarak olusturur. ROW_ID = custId'den set edilir;
     * DATA_TP_ID hardcode EDILMEZ, caller'dan (command.dataTypeId()) gelir -
     * customer-service bunu kendi LookupCacheService'i ile lookup-service'in
     * TYPE_VALUE tablosundan dinamik cozer (bkz. shared-contracts CreateContactCommand).
     */
    @Operation(summary = "[Onboarding] Musterinin tum adres+contact medium'larini tek transaction'da olustur",
            description = "customer-service'in POST /api/v1/customers/onboarding akisinda cagirdigi "
                    + "composite uc nokta. ROW_ID=custId bu servis tarafindan set edilir; DATA_TP_ID "
                    + "caller'dan (dinamik cozulmus) gelir - caller custId, dataTypeId, adres listesi "
                    + "ve contact medium listesi gonderir. Tekil bir kayit eklemek icin bunun yerine "
                    + "POST /api/v1/addresses veya POST /api/v1/contact-mediums/single kullanin.")
    @PostMapping
    @Transactional
    public ResponseEntity<Void> createContact(@RequestBody CreateContactCommand command) {
        for (AddressCommand addressCommand : command.addresses()) {
            addressService.add(new CreateAddressRequest(
                    command.custId(),
                    command.dataTypeId(),
                    addressCommand.cityId(),
                    addressCommand.streetName(),
                    addressCommand.buildingName(),
                    addressCommand.addressDesc(),
                    addressCommand.primary()));
        }

        for (ContactMediumCommand contactMediumCommand : command.contactMediums()) {
            contactMediumService.add(new CreateContactMediumRequest(
                    command.custId(),
                    command.dataTypeId(),
                    contactMediumCommand.contactData(),
                    contactMediumCommand.contactMediumTpId()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "[Onboarding compensation] Musterinin tum adres+contact medium'larini deaktive et",
            description = "customer-service tarafi (party/customer yazimi) basarisiz olup onboarding "
                    + "geri alinirken cagrilir. dataTypeId caller'dan (dinamik cozulmus) gelir, hardcode "
                    + "edilmez.")
    @DeleteMapping("/customer/{custId}")
    public ResponseEntity<Void> deleteByCustomerId(@PathVariable("custId") Long custId,
            @RequestParam("dataTypeId") Long dataTypeId) {
        addressService.deactivateAllForRow(custId, dataTypeId);
        contactMediumService.deactivateAllForRow(custId, dataTypeId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Contact medium'lari listele",
            description = "rowId VE dataTypeId ikisi de verilirse sadece o sahibe ait kayitlar doner; "
                    + "ikisi de verilmezse TUM kayitlar doner.")
    @GetMapping
    public ResponseEntity<List<ContactMediumResponse>> getAll(
            @Parameter(description = "Kaydin sahibinin id'si (ornegin custId). dataTypeId ile birlikte kullanilir.", example = "1")
            @RequestParam(required = false) Long rowId,
            @Parameter(description = "lookup-service TYPE_VALUE tablosundaki polimorfik tip etiketi (musteri icin CUST=12, dinamik cozulur). rowId ile birlikte kullanilir.", example = "12")
            @RequestParam(required = false) Long dataTypeId) {
        if (rowId != null && dataTypeId != null) {
            return ResponseEntity.ok(contactMediumService.getByRowIdAndDataTypeId(rowId, dataTypeId));
        }
        return ResponseEntity.ok(contactMediumService.getAll());
    }

    @Operation(summary = "Contact medium'u id ile getir")
    @GetMapping("/{id}")
    public ResponseEntity<ContactMediumResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(contactMediumService.getById(id));
    }

    @Operation(summary = "Yeni tekil contact medium ekle",
            description = "root POST composite create'e ait oldugu icin (bkz. yukarida createContact) "
                    + "tekil ekleme bu /single alt path'inden yapilir.")
    @PostMapping("/single")
    public ResponseEntity<ContactMediumResponse> add(@Valid @RequestBody CreateContactMediumRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactMediumService.add(request));
    }

    @Operation(summary = "Var olan contact medium'u guncelle")
    @PutMapping("/{id}")
    public ResponseEntity<ContactMediumResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateContactMediumRequest request) {
        return ResponseEntity.ok(contactMediumService.update(id, request));
    }

    @Operation(summary = "Contact medium'u sil (soft-delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contactMediumService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
