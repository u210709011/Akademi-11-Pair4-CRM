package com.etiya.crm.lookupservice.api.controllers;

import com.etiya.crm.lookupservice.business.abstracts.RsrcSpecService;
import com.etiya.crm.shared.contracts.rsrcspec.CreateRsrcSpecRequest;
import com.etiya.crm.shared.contracts.rsrcspec.UpdateRsrcSpecRequest;
import com.etiya.crm.shared.contracts.rsrcspec.RsrcSpecResponse;
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

@Tag(name = "Resource Specs (RSRC_SPEC)", description = "Kaynak spesifikasyonlari CRUD")
@RestController
@RequestMapping("/api/v1/resource-specs")
@RequiredArgsConstructor
public class RsrcSpecController {

    private final RsrcSpecService rsrcSpecService;

    @Operation(summary = "Tum kaynak speclerini listele")
    @GetMapping
    public ResponseEntity<List<RsrcSpecResponse>> getAll() {
        return ResponseEntity.ok(rsrcSpecService.getAll());
    }

    @Operation(summary = "Kaynak specini id ile getir")
    @GetMapping("/{id}")
    public ResponseEntity<RsrcSpecResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(rsrcSpecService.getById(id));
    }

    @Operation(summary = "Yeni kaynak speci ekle", description = "stId var olan bir GNL_ST satirina ait olmali.")
    @PostMapping
    public ResponseEntity<RsrcSpecResponse> add(@Valid @RequestBody CreateRsrcSpecRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rsrcSpecService.add(request));
    }

    @Operation(summary = "Kaynak specini guncelle")
    @PutMapping("/{id}")
    public ResponseEntity<RsrcSpecResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateRsrcSpecRequest request) {
        return ResponseEntity.ok(rsrcSpecService.update(id, request));
    }

    @Operation(summary = "Kaynak specini sil")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rsrcSpecService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
