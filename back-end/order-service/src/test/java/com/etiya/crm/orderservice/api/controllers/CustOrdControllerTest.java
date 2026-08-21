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
import com.etiya.crm.orderservice.business.dtos.requests.CreateOrderRequest;
import com.etiya.crm.orderservice.business.dtos.requests.OrderConfigurationRequest;
import com.etiya.crm.orderservice.business.dtos.requests.ValidateBasketRequest;
import com.etiya.crm.orderservice.business.dtos.responses.ActiveOfferResponse;
import com.etiya.crm.orderservice.business.dtos.responses.CustOrdItemResponse;
import com.etiya.crm.orderservice.business.dtos.responses.OrderListItemResponse;
import com.etiya.crm.orderservice.business.dtos.responses.OrderSummaryResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustOrdControllerTest {

	@Mock
	private CustOrdService custOrdService;

	@InjectMocks
	private CustOrdController custOrdController;

	private static OrderSummaryResponse summary() {
		return new OrderSummaryResponse(1L, 10L, 5L, List.of(), null, BigDecimal.TEN);
	}

	@Test
	void validateBasket_delegatesAndReturns200() {
		ValidateBasketRequest request = new ValidateBasketRequest(100L, 200L, List.of());

		ResponseEntity<Void> response = custOrdController.validateBasket(request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		verify(custOrdService).validateBasket(request);
	}

	@Test
	void create_returns201WithCreatedOrder() {
		CreateOrderRequest request = new CreateOrderRequest(100L, 200L, List.of());
		when(custOrdService.createOrder(request)).thenReturn(summary());

		ResponseEntity<OrderSummaryResponse> response = custOrdController.create(request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isEqualTo(summary());
	}

	@Test
	void saveConfiguration_returnsUpdatedOrder() {
		OrderConfigurationRequest request = new OrderConfigurationRequest(List.of(), 1L, null);
		when(custOrdService.saveConfiguration(1L, request)).thenReturn(summary());

		ResponseEntity<OrderSummaryResponse> response = custOrdController.saveConfiguration(1L, request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo(summary());
	}

	@Test
	void finish_returnsFinishedOrder() {
		when(custOrdService.finishOrder(1L)).thenReturn(summary());

		ResponseEntity<OrderSummaryResponse> response = custOrdController.finish(1L);

		assertThat(response.getBody()).isEqualTo(summary());
	}

	@Test
	void cancel_returnsCancelledOrder() {
		when(custOrdService.cancelOrder(1L)).thenReturn(summary());

		ResponseEntity<OrderSummaryResponse> response = custOrdController.cancel(1L);

		assertThat(response.getBody()).isEqualTo(summary());
	}

	@Test
	void getById_returnsOrder() {
		when(custOrdService.getById(1L)).thenReturn(summary());

		ResponseEntity<OrderSummaryResponse> response = custOrdController.getById(1L);

		assertThat(response.getBody()).isEqualTo(summary());
	}

	@Test
	void getByCustAcctId_returnsItems() {
		CustOrdItemResponse item = new CustOrdItemResponse(1L, 10L, 100L, "P1", "Product", null, null, null, 200L);
		when(custOrdService.getItemsByCustAcctId(200L)).thenReturn(List.of(item));

		ResponseEntity<List<CustOrdItemResponse>> response = custOrdController.getByCustAcctId(200L);

		assertThat(response.getBody()).containsExactly(item);
	}

	@Test
	void getByCustId_returnsOrderList() {
		OrderListItemResponse listItem = new OrderListItemResponse(1L, 5L, 2, BigDecimal.TEN, null);
		when(custOrdService.getOrdersByCustId(100L)).thenReturn(List.of(listItem));

		ResponseEntity<List<OrderListItemResponse>> response = custOrdController.getByCustId(100L);

		assertThat(response.getBody()).containsExactly(listItem);
	}

	@Test
	void getActiveOffers_returnsActiveOffers() {
		ActiveOfferResponse offer = new ActiveOfferResponse(100L, 1L, 200L);
		when(custOrdService.getActiveOffersByCustAcctId(200L)).thenReturn(List.of(offer));

		ResponseEntity<List<ActiveOfferResponse>> response = custOrdController.getActiveOffers(200L);

		assertThat(response.getBody()).containsExactly(offer);
	}

}
