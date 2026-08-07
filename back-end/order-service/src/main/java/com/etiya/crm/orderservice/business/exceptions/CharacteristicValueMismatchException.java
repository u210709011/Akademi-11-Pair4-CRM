package com.etiya.crm.orderservice.business.exceptions;

import com.etiya.crm.orderservice.constants.MessageKeys;

public class CharacteristicValueMismatchException extends BusinessException {

	public CharacteristicValueMismatchException(Long charValId, Long charId) {
		super(MessageKeys.CHARACTERISTIC_VALUE_MISMATCH, charValId, charId);
	}
}
