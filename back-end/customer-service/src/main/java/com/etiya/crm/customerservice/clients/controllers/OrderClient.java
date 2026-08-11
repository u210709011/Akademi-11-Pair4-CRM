package com.etiya.crm.customerservice.clients.controllers;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.etiya.crm.shared.contracts.order.CustOrdItemResponse;

/** order-service ile haberlesme kontrati - ACC-004 urun guard'i (bkz. BillingAccountProductGuard) icin. */
@FeignClient(name = "order-service")
public interface OrderClient {

	@GetMapping("/api/v1/orders/by-account")
	List<CustOrdItemResponse> getItemsByCustAcctId(@RequestParam("custAcctId") Long custAcctId);
}
