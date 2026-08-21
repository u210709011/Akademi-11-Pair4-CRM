package com.etiya.crm.orderservice.business.exceptions;

import com.etiya.crm.orderservice.constants.MessageKeys;

/** charValId (listeden secim) ve val (serbest metin) ikisi de bos - en az biri gerekli. Bkz. B-20. */
public class CharacteristicValueMissingException extends BusinessException {

	public CharacteristicValueMissingException(Long charId) {
		super(MessageKeys.CHARACTERISTIC_VALUE_MISSING, charId);
	}
}
