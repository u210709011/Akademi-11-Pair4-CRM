package com.etiya.crm.contactinfoservice.api.controllers;

import com.etiya.crm.contactinfoservice.business.abstracts.AddressService;
import com.etiya.crm.contactinfoservice.business.abstracts.ContactMediumService;
import com.etiya.crm.contactinfoservice.constants.SwaggerText;
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
@Tag(name = SwaggerText.CONTACT_MEDIUM_TAG_NAME, description = SwaggerText.CONTACT_MEDIUM_TAG_DESCRIPTION)
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
    @Operation(summary = SwaggerText.CREATE_CONTACT_SUMMARY,
            description = SwaggerText.CREATE_CONTACT_DESCRIPTION)
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

    @Operation(summary = SwaggerText.DELETE_BY_CUSTOMER_ID_SUMMARY,
            description = SwaggerText.DELETE_BY_CUSTOMER_ID_DESCRIPTION)
    @DeleteMapping("/customer/{custId}")
    public ResponseEntity<Void> deleteByCustomerId(@PathVariable("custId") Long custId,
            @RequestParam("dataTypeId") Long dataTypeId) {
        addressService.deactivateAllForRow(custId, dataTypeId);
        contactMediumService.deactivateAllForRow(custId, dataTypeId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = SwaggerText.GET_ALL_CONTACT_MEDIUMS_SUMMARY,
            description = SwaggerText.GET_ALL_CONTACT_MEDIUMS_DESCRIPTION)
    @GetMapping
    public ResponseEntity<List<ContactMediumResponse>> getAll(
            @Parameter(description = SwaggerText.CONTACT_MEDIUM_ROW_ID_PARAM_DESCRIPTION, example = "1")
            @RequestParam(required = false) Long rowId,
            @Parameter(description = SwaggerText.DATA_TYPE_ID_PARAM_DESCRIPTION, example = "12")
            @RequestParam(required = false) Long dataTypeId) {
        if (rowId != null && dataTypeId != null) {
            return ResponseEntity.ok(contactMediumService.getByRowIdAndDataTypeId(rowId, dataTypeId));
        }
        return ResponseEntity.ok(contactMediumService.getAll());
    }

    @Operation(summary = SwaggerText.GET_CONTACT_MEDIUM_BY_ID_SUMMARY)
    @GetMapping("/{id}")
    public ResponseEntity<ContactMediumResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(contactMediumService.getById(id));
    }

    @Operation(summary = SwaggerText.ADD_CONTACT_MEDIUM_SUMMARY,
            description = SwaggerText.ADD_CONTACT_MEDIUM_DESCRIPTION)
    @PostMapping("/single")
    public ResponseEntity<ContactMediumResponse> add(@Valid @RequestBody CreateContactMediumRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactMediumService.add(request));
    }

    @Operation(summary = SwaggerText.UPDATE_CONTACT_MEDIUM_SUMMARY)
    @PutMapping("/{id}")
    public ResponseEntity<ContactMediumResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateContactMediumRequest request) {
        return ResponseEntity.ok(contactMediumService.update(id, request));
    }

    @Operation(summary = SwaggerText.DELETE_CONTACT_MEDIUM_SUMMARY)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contactMediumService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
