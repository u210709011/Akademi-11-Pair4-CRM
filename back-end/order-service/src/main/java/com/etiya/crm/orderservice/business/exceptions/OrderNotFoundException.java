package com.etiya.crm.orderservice.business.exceptions;

import com.etiya.crm.orderservice.constants.MessageKeys;

public class OrderNotFoundException extends BusinessException {

	public OrderNotFoundException(Long custOrdId) {
		super(MessageKeys.ORDER_NOT_FOUND, custOrdId);
	}
}
