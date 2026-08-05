package com.etiya.crm.orderservice.business.abstracts;

import com.etiya.crm.orderservice.business.dtos.requests.BasketItemRequest;
import com.etiya.crm.orderservice.business.dtos.requests.CreateOrderRequest;
import com.etiya.crm.orderservice.business.dtos.requests.OrderConfigurationRequest;
import com.etiya.crm.orderservice.business.dtos.requests.ValidateBasketRequest;
import com.etiya.crm.orderservice.business.dtos.responses.CustOrdItemResponse;
import com.etiya.crm.orderservice.business.dtos.responses.OrderListItemResponse;
import com.etiya.crm.orderservice.business.dtos.responses.OrderSummaryResponse;

import java.util.List;

public interface CustOrdService {

        void validateBasket(ValidateBasketRequest request);
        OrderSummaryResponse createOrder(CreateOrderRequest request);
        OrderSummaryResponse addItem(Long custOrdId, BasketItemRequest request);
        OrderSummaryResponse removeItem(Long custOrdId, Long custOrdItemId);
        OrderSummaryResponse saveConfiguration(Long custOrdId, OrderConfigurationRequest request);
        OrderSummaryResponse finishOrder(Long custOrdId);
        OrderSummaryResponse cancelOrder(Long custOrdId);
        OrderSummaryResponse getById(Long custOrdId);
        List<CustOrdItemResponse> getItemsByCustAcctId(Long custAcctId);
        // tahmini alanlarla eklendi (FR'da kesinlesmis bir order-list sozlesmesi yok, gerekirse revize edilir)
        List<OrderListItemResponse> getOrdersByCustId(Long custId);

}
