package com.etiya.crm.customerservice.business.exceptions;

import com.etiya.crm.customerservice.constants.MessageKeys;

/** Musteri basina en fazla 5 adres kurali (bkz. CustomerBusinessRules.MAX_ADDRESS_COUNT). */
public class AddressLimitExceededException extends BusinessException {

	public AddressLimitExceededException() {
		super(MessageKeys.ADDRESS_MAX_EXCEEDED);
	}
}
