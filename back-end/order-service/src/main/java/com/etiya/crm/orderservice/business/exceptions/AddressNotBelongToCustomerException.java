package com.etiya.crm.orderservice.business.exceptions;

import com.etiya.crm.orderservice.constants.MessageKeys;

public class AddressNotBelongToCustomerException extends BusinessException {

	public AddressNotBelongToCustomerException(Long addressId) {
		super(MessageKeys.ADDRESS_NOT_BELONG_TO_CUSTOMER, addressId);
	}
}
