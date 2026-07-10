package com.etiya.crm.contactinfoservice.api.controllers;

import com.etiya.crm.contactinfoservice.business.abstracts.AddressService;
import com.etiya.crm.contactinfoservice.business.abstracts.ContactMediumService;
import com.etiya.crm.contactinfoservice.business.dtos.requests.AddressCommand;
import com.etiya.crm.contactinfoservice.business.dtos.requests.ContactMediumCommand;
import com.etiya.crm.contactinfoservice.business.dtos.requests.CreateAddressRequest;
import com.etiya.crm.contactinfoservice.business.dtos.requests.CreateContactCommand;
import com.etiya.crm.contactinfoservice.business.dtos.requests.CreateContactMediumRequest;
import com.etiya.crm.contactinfoservice.business.dtos.requests.UpdateContactMediumRequest;
import com.etiya.crm.contactinfoservice.business.dtos.responses.ContactMediumResponse;
import com.etiya.crm.contactinfoservice.constants.DataTypeIds;
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
     * seferde, atomik olarak olusturur. ROW_ID/DATA_TP_ID client'tan gelmez,
     * bu servis custId'den ROW_ID = custId, DATA_TP_ID = CUST(102) olarak set eder.
     */
    @PostMapping
    @Transactional
    public ResponseEntity<Void> createContact(@RequestBody CreateContactCommand command) {
        for (AddressCommand addressCommand : command.addresses()) {
            addressService.add(new CreateAddressRequest(
                    command.custId(),
                    DataTypeIds.CUSTOMER,
                    addressCommand.cityId(),
                    addressCommand.streetName(),
                    addressCommand.buildingName(),
                    addressCommand.addressDesc(),
                    addressCommand.primary()));
        }

        for (ContactMediumCommand contactMediumCommand : command.contactMediums()) {
            contactMediumService.add(new CreateContactMediumRequest(
                    command.custId(),
                    DataTypeIds.CUSTOMER,
                    contactMediumCommand.contactData(),
                    contactMediumCommand.contactMediumTpId()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** Onboarding compensation: customer-service tarafi basarisiz olursa geri alinir. */
    @DeleteMapping("/customer/{custId}")
    public ResponseEntity<Void> deleteByCustomerId(@PathVariable("custId") Long custId) {
        addressService.deactivateAllForRow(custId, DataTypeIds.CUSTOMER);
        contactMediumService.deactivateAllForRow(custId, DataTypeIds.CUSTOMER);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ContactMediumResponse>> getAll(
            @RequestParam(required = false) Long rowId,
            @RequestParam(required = false) Long dataTypeId) {
        if (rowId != null && dataTypeId != null) {
            return ResponseEntity.ok(contactMediumService.getByRowIdAndDataTypeId(rowId, dataTypeId));
        }
        return ResponseEntity.ok(contactMediumService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactMediumResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(contactMediumService.getById(id));
    }

    @PostMapping("/single")
    public ResponseEntity<ContactMediumResponse> add(@Valid @RequestBody CreateContactMediumRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactMediumService.add(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactMediumResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateContactMediumRequest request) {
        return ResponseEntity.ok(contactMediumService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contactMediumService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
