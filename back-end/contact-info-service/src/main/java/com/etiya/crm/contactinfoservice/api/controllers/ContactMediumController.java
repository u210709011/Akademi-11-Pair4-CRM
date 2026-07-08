package com.etiya.crm.contactinfoservice.api.controllers;

import com.etiya.crm.contactinfoservice.business.abstracts.ContactMediumService;
import com.etiya.crm.contactinfoservice.business.dtos.requests.CreateContactMediumRequest;
import com.etiya.crm.contactinfoservice.business.dtos.requests.UpdateContactMediumRequest;
import com.etiya.crm.contactinfoservice.business.dtos.responses.ContactMediumResponse;
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
@RequestMapping("/api/v1/contact-mediums")
public class ContactMediumController {

    private final ContactMediumService contactMediumService;

    public ContactMediumController(ContactMediumService contactMediumService) {
        this.contactMediumService = contactMediumService;
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

    @PostMapping
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
