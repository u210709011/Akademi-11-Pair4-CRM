package com.etiya.crm.lookupservice.api.controllers;

import com.etiya.crm.lookupservice.business.abstracts.TypeValueService;
import com.etiya.crm.shared.contracts.typevalue.CreateTypeValueRequest;
import com.etiya.crm.shared.contracts.typevalue.UpdateTypeValueRequest;
import com.etiya.crm.shared.contracts.typevalue.TypeValueResponse;
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

@Tag(name = "Type Values (TYPE_VALUE)",
        description = "Is tablolarina (PROD, PARTY, CUST, CUST_ACCT...) atanan polimorfik tip etiketleri CRUD")
@RestController
@RequestMapping("/api/v1/type-values")
@RequiredArgsConstructor
public class TypeValueController {

    private final TypeValueService typeValueService;

    @Operation(summary = "Tum degerleri listele")
    @GetMapping
    public ResponseEntity<List<TypeValueResponse>> getAll() {
        return ResponseEntity.ok(typeValueService.getAll());
    }

    @Operation(summary = "Degeri id ile getir")
    @GetMapping("/{id}")
    public ResponseEntity<TypeValueResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(typeValueService.getById(id));
    }

    @Operation(summary = "Yeni tip etiketi ekle", description = "tableName benzersiz olmali - her is tablosunun tek bir tip etiketi olur.")
    @PostMapping
    public ResponseEntity<TypeValueResponse> add(@Valid @RequestBody CreateTypeValueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(typeValueService.add(request));
    }

    @Operation(summary = "Degeri guncelle", description = "tableName/fieldName degistirilemez (immutable kimlik).")
    @PutMapping("/{id}")
    public ResponseEntity<TypeValueResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateTypeValueRequest request) {
        return ResponseEntity.ok(typeValueService.update(id, request));
    }

    @Operation(summary = "Degeri sil (hard delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        typeValueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
