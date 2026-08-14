package com.etiya.crm.shared.contracts.order;

/** order-service GET /api/v1/orders/by-account yaniti - fatura hesabina bagli siparis kalemleri. */
public record CustOrdItemResponse(

		Long custOrdItemId,
		Long prodId,
		String prodName,
		Long cmpgId,
		String cmpgName,
		Long custAcctId
) {
}
