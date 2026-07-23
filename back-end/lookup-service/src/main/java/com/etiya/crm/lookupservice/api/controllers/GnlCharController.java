package com.etiya.crm.lookupservice.api.controllers;

import com.etiya.crm.lookupservice.business.abstracts.GnlCharService;
import com.etiya.crm.shared.contracts.gnlchar.CreateGnlCharRequest;
import com.etiya.crm.shared.contracts.gnlchar.UpdateGnlCharRequest;
import com.etiya.crm.shared.contracts.gnlchar.GnlCharResponse;
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

@Tag(name = "Characteristics (GNL_CHAR)", description = "Karakteristik tanimlari CRUD")
@RestController
@RequestMapping("/api/v1/characteristics")
@RequiredArgsConstructor
public class GnlCharController {

    private final GnlCharService gnlCharService;

    @Operation(summary = "Tum karakteristikleri listele")
    @GetMapping
    public ResponseEntity<List<GnlCharResponse>> getAll() {
        return ResponseEntity.ok(gnlCharService.getAll());
    }

    @Operation(summary = "Karakteristigi id ile getir")
    @GetMapping("/{id}")
    public ResponseEntity<GnlCharResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(gnlCharService.getById(id));
    }

    @Operation(summary = "Yeni karakteristik ekle")
    @PostMapping
    public ResponseEntity<GnlCharResponse> add(@Valid @RequestBody CreateGnlCharRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gnlCharService.add(request));
    }

    @Operation(summary = "Karakteristigi guncelle", description = "shrtCode degistirilemez.")
    @PutMapping("/{id}")
    public ResponseEntity<GnlCharResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateGnlCharRequest request) {
        return ResponseEntity.ok(gnlCharService.update(id, request));
    }

    @Operation(summary = "Karakteristigi sil (soft-delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gnlCharService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
