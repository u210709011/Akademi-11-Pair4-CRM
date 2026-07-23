package com.etiya.crm.lookupservice.api.controllers;

import com.etiya.crm.lookupservice.business.abstracts.SrvcSpecService;
import com.etiya.crm.shared.contracts.srvcspec.CreateSrvcSpecRequest;
import com.etiya.crm.shared.contracts.srvcspec.UpdateSrvcSpecRequest;
import com.etiya.crm.shared.contracts.srvcspec.SrvcSpecResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Service Specs (SRVC_SPEC)", description = "Servis spesifikasyonlari CRUD")
@RestController
@RequestMapping("/api/v1/service-specs")
@RequiredArgsConstructor
public class SrvcSpecController {

    private final SrvcSpecService srvcSpecService;

    @Operation(summary = "Tum servis speclerini listele")
    @GetMapping
    public ResponseEntity<List<SrvcSpecResponse>> getAll() {
        return ResponseEntity.ok(srvcSpecService.getAll());
    }

    @Operation(summary = "Servis specini id ile getir")
    @GetMapping("/{id}")
    public ResponseEntity<SrvcSpecResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(srvcSpecService.getById(id));
    }

    @Operation(summary = "Yeni servis speci ekle", description = "stId var olan bir GNL_ST satirina ait olmali.")
    @PostMapping
    public ResponseEntity<SrvcSpecResponse> add(@Valid @RequestBody CreateSrvcSpecRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(srvcSpecService.add(request));
    }

    @Operation(summary = "Servis specini guncelle")
    @PutMapping("/{id}")
    public ResponseEntity<SrvcSpecResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateSrvcSpecRequest request) {
        return ResponseEntity.ok(srvcSpecService.update(id, request));
    }

    @Operation(summary = "Servis specini sil")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        srvcSpecService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
