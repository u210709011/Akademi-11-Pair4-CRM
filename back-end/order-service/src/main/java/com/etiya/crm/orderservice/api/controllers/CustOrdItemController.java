package com.etiya.crm.orderservice.api.controllers;

import com.etiya.crm.orderservice.business.abstracts.CustOrdService;
import com.etiya.crm.orderservice.business.dtos.requests.BasketItemRequest;
import com.etiya.crm.orderservice.business.dtos.responses.OrderSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// WAIT durumundaki siparisin sepetiyle (item ekle/cikar) ilgili endpoint'ler - CustOrdController
// ile ayni aggregate/CustOrdService'i kullanir, sadece dosya bazinda ayrilmistir (bkz. customer-service
// CustomerAccountController pattern'i).
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CRM_AGENT')")
@Tag(name = "Cust Ord Item", description = "WAIT durumundaki siparisin sepetine item ekleme/cikarma")
public class CustOrdItemController {

    private final CustOrdService custOrdService;

    // Offer Selection'da "Add to Basket" - WAIT durumundaki siparise yeni item ekler.
    @Operation(summary = "Sepete item ekler", description = "WAIT durumundaki siparise yeni bir teklif ekler.")
    @PostMapping("/{custOrdId}/items")
    public ResponseEntity<OrderSummaryResponse> addItem(@PathVariable Long custOrdId,
            @Valid @RequestBody BasketItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(custOrdService.addItem(custOrdId, request));
    }

    // Sepetten cop kutusuyla tek item cikarma.
    @Operation(summary = "Sepetten item cikarir", description = "WAIT durumundaki siparisten tek bir item'i siler.")
    @DeleteMapping("/{custOrdId}/items/{custOrdItemId}")
    public ResponseEntity<OrderSummaryResponse> removeItem(@PathVariable Long custOrdId,
            @PathVariable Long custOrdItemId) {
        return ResponseEntity.ok(custOrdService.removeItem(custOrdId, custOrdItemId));
    }
}
