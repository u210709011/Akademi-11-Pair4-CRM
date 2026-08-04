package com.etiya.crm.orderservice.business.exceptions;

import com.etiya.crm.orderservice.constants.MessageKeys;

public class OrderItemNotFoundException extends BusinessException {

	public OrderItemNotFoundException(Long custOrdItemId, Long custOrdId) {
		super(MessageKeys.ORDER_ITEM_NOT_FOUND, custOrdItemId, custOrdId);
	}
}
