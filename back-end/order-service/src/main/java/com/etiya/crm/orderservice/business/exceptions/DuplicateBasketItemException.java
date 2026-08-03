package com.etiya.crm.orderservice.business.exceptions;

import com.etiya.crm.orderservice.constants.MessageKeys;

/** Ayni prodOfrId+cmpgId kombinasyonu sepette birden fazla kez gonderildi. */
public class DuplicateBasketItemException extends BusinessException {

	public DuplicateBasketItemException(Long prodOfrId) {
		super(MessageKeys.DUPLICATE_BASKET_ITEM, prodOfrId);
	}
}
