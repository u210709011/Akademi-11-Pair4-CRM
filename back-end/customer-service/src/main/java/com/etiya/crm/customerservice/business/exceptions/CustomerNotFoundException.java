package com.etiya.crm.customerservice.business.exceptions;

import com.etiya.crm.customerservice.constants.MessageKeys;

public class CustomerNotFoundException extends BusinessException {

	public CustomerNotFoundException(Long custId) {
		super(MessageKeys.CUSTOMER_NOT_FOUND, custId);
	}
}
