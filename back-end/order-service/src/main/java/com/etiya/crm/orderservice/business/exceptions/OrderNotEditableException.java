package com.etiya.crm.orderservice.business.exceptions;

import com.etiya.crm.orderservice.constants.MessageKeys;

/** Siparis WAIT durumunda degilse (zaten finish edilmis/reddedilmis) configure/finish edilemez. */
public class OrderNotEditableException extends BusinessException {

	public OrderNotEditableException(Long custOrdId) {
		super(MessageKeys.ORDER_NOT_EDITABLE, custOrdId);
	}
}
