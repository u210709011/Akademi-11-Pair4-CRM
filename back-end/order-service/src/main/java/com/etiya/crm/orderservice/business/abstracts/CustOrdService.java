package com.etiya.crm.orderservice.business.abstracts;

import com.etiya.crm.orderservice.business.dtos.requests.SubmitOrderRequest;
import com.etiya.crm.orderservice.business.dtos.responses.CustOrdItemResponse;
import com.etiya.crm.orderservice.business.dtos.responses.OrderSummaryResponse;

import java.util.List;

public interface CustOrdService {

        OrderSummaryResponse submitOrder(SubmitOrderRequest request);
        OrderSummaryResponse getById(Long custOrdId);
        List<CustOrdItemResponse> getItemsByCustAcctId(Long custAcctId);

}
