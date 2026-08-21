package com.etiya.crm.orderservice.api.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.etiya.crm.orderservice.business.abstracts.CustOrdService;
import com.etiya.crm.orderservice.business.dtos.requests.BasketItemRequest;
import com.etiya.crm.orderservice.business.dtos.responses.OrderSummaryResponse;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CustOrdItemControllerTest {

	@Mock
	private CustOrdService custOrdService;

	@InjectMocks
	private CustOrdItemController custOrdItemController;

	private static OrderSummaryResponse summary() {
		return new OrderSummaryResponse(1L, 10L, 5L, List.of(), null, BigDecimal.TEN);
	}

	@Test
	void addItem_returns201WithUpdatedOrder() {
		BasketItemRequest request = new BasketItemRequest(100L, null, List.of());
		org.mockito.Mockito.when(custOrdService.addItem(1L, request)).thenReturn(summary());

		ResponseEntity<OrderSummaryResponse> response = custOrdItemController.addItem(1L, request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isEqualTo(summary());
	}

	@Test
	void removeItem_returnsUpdatedOrder() {
		org.mockito.Mockito.when(custOrdService.removeItem(1L, 2L)).thenReturn(summary());

		ResponseEntity<OrderSummaryResponse> response = custOrdItemController.removeItem(1L, 2L);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo(summary());
	}

}
