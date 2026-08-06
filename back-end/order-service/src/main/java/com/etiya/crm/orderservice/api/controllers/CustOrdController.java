package com.etiya.crm.orderservice.api.controllers;

import com.etiya.crm.orderservice.business.abstracts.CustOrdService;
import com.etiya.crm.orderservice.business.dtos.requests.CreateOrderRequest;
import com.etiya.crm.orderservice.business.dtos.requests.OrderConfigurationRequest;
import com.etiya.crm.orderservice.business.dtos.requests.ValidateBasketRequest;
import com.etiya.crm.orderservice.business.dtos.responses.ActiveOfferResponse;
import com.etiya.crm.orderservice.business.dtos.responses.CustOrdItemResponse;
import com.etiya.crm.orderservice.business.dtos.responses.OrderListItemResponse;
import com.etiya.crm.orderservice.business.dtos.responses.OrderSummaryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CRM_AGENT')")
public class CustOrdController {

    private final CustOrdService custOrdService;

    // FR-017: Offer Selection ekraninda "Next" - Product Configuration'a gecmeden once sepeti dogrular.
    @PostMapping("/validate-basket")
    public ResponseEntity<Void> validateBasket(@Valid @RequestBody ValidateBasketRequest request) {
        custOrdService.validateBasket(request);
        return ResponseEntity.ok().build();
    }

    // FR-012/FR-013: sepet dogrulandiktan sonra siparisi WAIT durumunda acar (Offer Selection -> Configuration).
    @PostMapping
    public ResponseEntity<OrderSummaryResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(custOrdService.createOrder(request));
    }

    // FR-015: Product Configuration ekraninda karakteristik/adres girildikce (autosave) cagrilir.
    @PutMapping("/{custOrdId}/configuration")
    public ResponseEntity<OrderSummaryResponse> saveConfiguration(@PathVariable Long custOrdId,
            @Valid @RequestBody OrderConfigurationRequest request) {
        return ResponseEntity.ok(custOrdService.saveConfiguration(custOrdId, request));
    }

    // FR-021: Review & Confirm'de Finish - WAIT'ten MIDLWARE'e gecirir ve OrderSubmittedEvent'i yayinlar.
    @PostMapping("/{custOrdId}/finish")
    public ResponseEntity<OrderSummaryResponse> finish(@PathVariable Long custOrdId) {
        return ResponseEntity.ok(custOrdService.finishOrder(custOrdId));
    }

    // Review & Confirm'de Cancel - WAIT'teki siparisi REJECTED'e cevirir.
    @PostMapping("/{custOrdId}/cancel")
    public ResponseEntity<OrderSummaryResponse> cancel(@PathVariable Long custOrdId) {
        return ResponseEntity.ok(custOrdService.cancelOrder(custOrdId));
    }

    @GetMapping("/{custOrdId}")
    public ResponseEntity<OrderSummaryResponse> getById(@PathVariable Long custOrdId) {
        return ResponseEntity.ok(custOrdService.getById(custOrdId));
    }

    @GetMapping(params = "custAcctId")
    public ResponseEntity<List<CustOrdItemResponse>> getByCustAcctId(@RequestParam Long custAcctId) {
        return ResponseEntity.ok(custOrdService.getItemsByCustAcctId(custAcctId));
    }

    // Musteri siparis gecmisi (order list ekrani) - tahmini alanlarla eklendi, gerekirse revize edilir.
    @GetMapping(params = "custId")
    public ResponseEntity<List<OrderListItemResponse>> getByCustId(@RequestParam Long custId) {
        return ResponseEntity.ok(custOrdService.getOrdersByCustId(custId));
    }

    // BR-03 "Already Active": Offer Selection'da bu hesap icin zaten aktif olan teklifler.
    @GetMapping("/active-offers")
    public ResponseEntity<List<ActiveOfferResponse>> getActiveOffers(@RequestParam Long custAcctId) {
        return ResponseEntity.ok(custOrdService.getActiveOffersByCustAcctId(custAcctId));
    }
}
