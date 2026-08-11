package com.etiya.crm.orderservice.business.exceptions;

import com.etiya.crm.orderservice.constants.MessageKeys;

// FR-014 ACC-012/BR-04: ayni kategoride/hizmette cakisan (EXCL, product-service prod_ofr_rel)
// iki teklif ayni sepette/siparişte birlikte olamaz.
public class ConflictingBasketItemException extends BusinessException {

	public ConflictingBasketItemException(Long prodOfrId1, Long prodOfrId2) {
		super(MessageKeys.CONFLICTING_BASKET_ITEM, prodOfrId1, prodOfrId2);
	}
}
