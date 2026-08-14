package com.etiya.crm.dataseeder.client;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

/** product-service'ten katalogu (offering + ilişki kurallari) ID hardcode etmeden kesfeder. */
@Component
public class ProductClient {

	private final GatewayClient gatewayClient;

	public ProductClient(GatewayClient gatewayClient) {
		this.gatewayClient = gatewayClient;
	}

	public List<ProductOfferingResponse> getAllOfferings() {
		return gatewayClient.get("/api/v1/product-offerings",
				new ParameterizedTypeReference<List<ProductOfferingResponse>>() {
				});
	}

	public List<ProductOfferingRelationResponse> getRelations(Long productOfferingId) {
		return gatewayClient.get("/api/v1/product-offering-relations/by-offering/" + productOfferingId,
				new ParameterizedTypeReference<List<ProductOfferingRelationResponse>>() {
				});
	}

	public record ProductOfferingResponse(Long productOfferingId, String productOfferingNo, Long productSpecId,
			String name, String descr, Long parentOfferingId, Long statusId, BigDecimal totalPrice) {
	}

	public record ProductOfferingRelationResponse(Long productOfferingRelationId, Long productOfferingId1,
			Long productOfferingId2, Long relationTypeId, Boolean mandatory, Boolean exclusive, Integer qty,
			Boolean active) {
	}
}
