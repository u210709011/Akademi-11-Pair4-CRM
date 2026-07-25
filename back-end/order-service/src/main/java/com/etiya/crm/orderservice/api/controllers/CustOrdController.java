package com.etiya.crm.orderservice.api.controllers;

import com.etiya.crm.orderservice.business.abstracts.CustOrdService;
import com.etiya.crm.orderservice.business.dtos.requests.SubmitOrderRequest;
import com.etiya.crm.orderservice.business.dtos.responses.CustOrdItemResponse;
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

    @PostMapping("/submit")
    public ResponseEntity<OrderSummaryResponse> submit(@Valid @RequestBody SubmitOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(custOrdService.submitOrder(request));
    }

    @GetMapping("/{custOrdId}")
    public ResponseEntity<OrderSummaryResponse> getById(@PathVariable Long custOrdId) {
        return ResponseEntity.ok(custOrdService.getById(custOrdId));
    }

    @GetMapping(params = "custAcctId")
    public ResponseEntity<List<CustOrdItemResponse>> getByCustAcctId(@RequestParam Long custAcctId) {
        return ResponseEntity.ok(custOrdService.getItemsByCustAcctId(custAcctId));
    }
}
