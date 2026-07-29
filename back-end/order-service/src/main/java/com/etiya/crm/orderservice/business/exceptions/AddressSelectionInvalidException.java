package com.etiya.crm.orderservice.business.exceptions;

import com.etiya.crm.orderservice.constants.MessageKeys;

/** Ya var olan bir addressId ya da yeni bir adres verilmeli - ikisi birden ya da hicbiri olamaz */
public class AddressSelectionInvalidException extends BusinessException {

	public AddressSelectionInvalidException() {
		super(MessageKeys.ADDRESS_SELECTION_INVALID);
	}
}
