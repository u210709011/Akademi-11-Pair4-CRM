package com.etiya.crm.orderservice.api.controllers;

import com.etiya.crm.orderservice.business.abstracts.CustOrdService;
import com.etiya.crm.orderservice.business.dtos.requests.CreateOrderRequest;
import com.etiya.crm.orderservice.business.dtos.requests.OrderConfigurationRequest;
import com.etiya.crm.orderservice.business.dtos.requests.ValidateBasketRequest;
import com.etiya.crm.orderservice.business.dtos.responses.ActiveOfferResponse;
import com.etiya.crm.orderservice.business.dtos.responses.CustOrdItemResponse;
import com.etiya.crm.orderservice.business.dtos.responses.OrderListItemResponse;
import com.etiya.crm.orderservice.business.dtos.responses.OrderSummaryResponse;
import com.etiya.crm.orderservice.constants.Roles;
import com.etiya.crm.orderservice.constants.SwaggerText;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@PreAuthorize("hasRole('" + Roles.CRM_AGENT + "')")
@Tag(name = SwaggerText.CUST_ORD_TAG_NAME, description = SwaggerText.CUST_ORD_TAG_DESCRIPTION)
public class CustOrdController {

    private final CustOrdService custOrdService;

    // FR-017: Offer Selection ekraninda "Next" - Product Configuration'a gecmeden once sepeti dogrular.
    @Operation(summary = SwaggerText.VALIDATE_BASKET_SUMMARY, description = SwaggerText.VALIDATE_BASKET_DESCRIPTION)
    @PostMapping("/validate-basket")
    public ResponseEntity<Void> validateBasket(@Valid @RequestBody ValidateBasketRequest request) {
        custOrdService.validateBasket(request);
        return ResponseEntity.ok().build();
    }

    // FR-012/FR-013: sepet dogrulandiktan sonra siparisi WAIT durumunda acar (Offer Selection -> Configuration).
    @Operation(summary = SwaggerText.CREATE_ORDER_SUMMARY, description = SwaggerText.CREATE_ORDER_DESCRIPTION)
    @PostMapping
    public ResponseEntity<OrderSummaryResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(custOrdService.createOrder(request));
    }

    // FR-015: Product Configuration ekraninda karakteristik/adres girildikce (autosave) cagrilir.
    @Operation(summary = SwaggerText.SAVE_CONFIGURATION_SUMMARY, description = SwaggerText.SAVE_CONFIGURATION_DESCRIPTION)
    @PutMapping("/{custOrdId}/configuration")
    public ResponseEntity<OrderSummaryResponse> saveConfiguration(@PathVariable Long custOrdId,
            @Valid @RequestBody OrderConfigurationRequest request) {
        return ResponseEntity.ok(custOrdService.saveConfiguration(custOrdId, request));
    }

    // FR-021: Review & Confirm'de Finish - WAIT'ten MIDLWARE'e gecirir ve OrderSubmittedEvent'i yayinlar.
    @Operation(summary = SwaggerText.FINISH_ORDER_SUMMARY, description = SwaggerText.FINISH_ORDER_DESCRIPTION)
    @PostMapping("/{custOrdId}/finish")
    public ResponseEntity<OrderSummaryResponse> finish(@PathVariable Long custOrdId) {
        return ResponseEntity.ok(custOrdService.finishOrder(custOrdId));
    }

    // Review & Confirm'de Cancel - WAIT'teki siparisi REJECTED'e cevirir.
    @Operation(summary = SwaggerText.CANCEL_ORDER_SUMMARY, description = SwaggerText.CANCEL_ORDER_DESCRIPTION)
    @PostMapping("/{custOrdId}/cancel")
    public ResponseEntity<OrderSummaryResponse> cancel(@PathVariable Long custOrdId) {
        return ResponseEntity.ok(custOrdService.cancelOrder(custOrdId));
    }

    @Operation(summary = SwaggerText.GET_ORDER_BY_ID_SUMMARY)
    @GetMapping("/{custOrdId}")
    public ResponseEntity<OrderSummaryResponse> getById(@PathVariable Long custOrdId) {
        return ResponseEntity.ok(custOrdService.getById(custOrdId));
    }

    // ayni path'te (params ile ayirt edilen) iki farkli GET, OpenAPI'de tek operation'a
    // birlesip ozet/parametreleri birbirine kariyordu - Swagger'in dogru gosterebilmesi icin
    // ayri path'lere tasindi (bkz. /active-offers'la ayni pattern, {custOrdId} ile catismiyor).
    @Operation(summary = SwaggerText.GET_ITEMS_BY_ACCOUNT_SUMMARY, description = SwaggerText.GET_ITEMS_BY_ACCOUNT_DESCRIPTION)
    @GetMapping("/by-account")
    public ResponseEntity<List<CustOrdItemResponse>> getByCustAcctId(@RequestParam Long custAcctId) {
        return ResponseEntity.ok(custOrdService.getItemsByCustAcctId(custAcctId));
    }

    // Musteri siparis gecmisi (order list ekrani) - tahmini alanlarla eklendi, gerekirse revize edilir.
    @Operation(summary = SwaggerText.GET_ORDERS_BY_CUSTOMER_SUMMARY)
    @GetMapping("/by-customer")
    public ResponseEntity<List<OrderListItemResponse>> getByCustId(@RequestParam Long custId) {
        return ResponseEntity.ok(custOrdService.getOrdersByCustId(custId));
    }

    // BR-03 "Already Active": Offer Selection'da bu hesap icin zaten aktif olan teklifler.
    @Operation(summary = SwaggerText.GET_ACTIVE_OFFERS_SUMMARY, description = SwaggerText.GET_ACTIVE_OFFERS_DESCRIPTION)
    @GetMapping("/active-offers")
    public ResponseEntity<List<ActiveOfferResponse>> getActiveOffers(@RequestParam Long custAcctId) {
        return ResponseEntity.ok(custOrdService.getActiveOffersByCustAcctId(custAcctId));
    }
}
