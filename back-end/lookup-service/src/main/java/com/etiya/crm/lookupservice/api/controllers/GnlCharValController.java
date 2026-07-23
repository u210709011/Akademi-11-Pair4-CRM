package com.etiya.crm.lookupservice.api.controllers;

import com.etiya.crm.lookupservice.business.abstracts.GnlCharValService;
import com.etiya.crm.shared.contracts.gnlcharval.CreateGnlCharValRequest;
import com.etiya.crm.shared.contracts.gnlcharval.UpdateGnlCharValRequest;
import com.etiya.crm.shared.contracts.gnlcharval.GnlCharValResponse;
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

@Tag(name = "Characteristic Values (GNL_CHAR_VAL)", description = "Karakteristik degerleri CRUD")
@RestController
@RequestMapping("/api/v1/characteristic-values")
@RequiredArgsConstructor
public class GnlCharValController {

    private final GnlCharValService gnlCharValService;

    @Operation(summary = "Tum karakteristik degerlerini listele")
    @GetMapping
    public ResponseEntity<List<GnlCharValResponse>> getAll() {
        return ResponseEntity.ok(gnlCharValService.getAll());
    }

    @Operation(summary = "Karakteristik degerini id ile getir")
    @GetMapping("/{id}")
    public ResponseEntity<GnlCharValResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(gnlCharValService.getById(id));
    }

    @Operation(summary = "Yeni karakteristik degeri ekle", description = "charId var olan bir GNL_CHAR'a ait olmali.")
    @PostMapping
    public ResponseEntity<GnlCharValResponse> add(@Valid @RequestBody CreateGnlCharValRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gnlCharValService.add(request));
    }

    @Operation(summary = "Karakteristik degerini guncelle", description = "charId degistirilemez.")
    @PutMapping("/{id}")
    public ResponseEntity<GnlCharValResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateGnlCharValRequest request) {
        return ResponseEntity.ok(gnlCharValService.update(id, request));
    }

    @Operation(summary = "Karakteristik degerini sil (soft-delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gnlCharValService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
