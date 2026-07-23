package com.etiya.crm.lookupservice.api.controllers;

import com.etiya.crm.lookupservice.business.abstracts.GnlStService;
import com.etiya.crm.shared.contracts.gnlst.CreateGnlStRequest;
import com.etiya.crm.shared.contracts.gnlst.UpdateGnlStRequest;
import com.etiya.crm.shared.contracts.gnlst.GnlStResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "General Statuses (GNL_ST)", description = "Genel durum grubu tanimlari CRUD")
@RestController
@RequestMapping("/api/v1/general-statuses")
@RequiredArgsConstructor
public class GnlStController {

    private final GnlStService gnlStService;

    @Operation(summary = "Durum degerlerini listele",
            description = "entCodeName verilirse sadece o gruba ait degerler doner (orn. CUST_STATUS); "
                    + "verilmezse TUMU doner.")
    @GetMapping
    public ResponseEntity<List<GnlStResponse>> getAll(@RequestParam(required = false) String entCodeName) {
        if (entCodeName != null) {
            return ResponseEntity.ok(gnlStService.getAllByEntCodeName(entCodeName));
        }
        return ResponseEntity.ok(gnlStService.getAll());
    }

    @Operation(summary = "Durum degerini id ile getir")
    @GetMapping("/{id}")
    public ResponseEntity<GnlStResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(gnlStService.getById(id));
    }

    @Operation(summary = "Durum degerini grup+kisa kod ile coz",
            description = "Kod bazli erisim. Ornek: GET /api/v1/general-statuses/resolve/CUST_STATUS/ACTIVE")
    @GetMapping("/resolve/{entCodeName}/{shrtCode}")
    public ResponseEntity<GnlStResponse> getByEntCodeNameAndShrtCode(
            @PathVariable String entCodeName, @PathVariable String shrtCode) {
        return ResponseEntity.ok(gnlStService.getByEntCodeNameAndShrtCode(entCodeName, shrtCode));
    }

    @Operation(summary = "Yeni durum grubu ekle")
    @PostMapping
    public ResponseEntity<GnlStResponse> add(@Valid @RequestBody CreateGnlStRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gnlStService.add(request));
    }

    @Operation(summary = "Durum grubunu guncelle")
    @PutMapping("/{id}")
    public ResponseEntity<GnlStResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateGnlStRequest request) {
        return ResponseEntity.ok(gnlStService.update(id, request));
    }

    @Operation(summary = "Durum grubunu sil (soft-delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gnlStService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
