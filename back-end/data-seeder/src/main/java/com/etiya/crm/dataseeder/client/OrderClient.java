package com.etiya.crm.dataseeder.client;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

/** order-service'e sepet dogrulama -> siparis olusturma -> konfigurasyon -> bitirme/iptal akisi. */
@Component
public class OrderClient {

	private final GatewayClient gatewayClient;

	public OrderClient(GatewayClient gatewayClient) {
		this.gatewayClient = gatewayClient;
	}

	public void validateBasket(Long custId, Long custAcctId, List<BasketItemRequest> items) {
		gatewayClient.post("/api/v1/orders/validate-basket", new OrderBasketRequest(custId, custAcctId, items));
	}

	public OrderSummaryResponse create(Long custId, Long custAcctId, List<BasketItemRequest> items) {
		return gatewayClient.post("/api/v1/orders", new OrderBasketRequest(custId, custAcctId, items),
				OrderSummaryResponse.class);
	}

	public OrderSummaryResponse saveConfiguration(Long custOrdId, Long addressId) {
		OrderConfigurationRequest request = new OrderConfigurationRequest(List.of(), addressId, null);
		return gatewayClient.put("/api/v1/orders/" + custOrdId + "/configuration", request, OrderSummaryResponse.class);
	}

	public OrderSummaryResponse finish(Long custOrdId) {
		return gatewayClient.postNoBody("/api/v1/orders/" + custOrdId + "/finish", OrderSummaryResponse.class);
	}

	public OrderSummaryResponse cancel(Long custOrdId) {
		return gatewayClient.postNoBody("/api/v1/orders/" + custOrdId + "/cancel", OrderSummaryResponse.class);
	}

	public record BasketItemRequest(Long prodOfrId, Long cmpgId, List<Object> charVals) {
	}

	public record OrderBasketRequest(Long custId, Long custAcctId, List<BasketItemRequest> items) {
	}

	public record OrderConfigurationRequest(List<Object> items, Long addressId, Object newAddress) {
	}

	public record OrderSummaryResponse(Long custOrdId, Long bsnInterId, Long ordStId, List<Object> items,
			Object serviceAddress, BigDecimal totalAmount) {
	}
}
