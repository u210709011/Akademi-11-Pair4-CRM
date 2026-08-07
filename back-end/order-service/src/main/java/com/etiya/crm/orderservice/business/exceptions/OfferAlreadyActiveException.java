package com.etiya.crm.orderservice.business.exceptions;

import com.etiya.crm.orderservice.constants.MessageKeys;

// FR-014 IK-05: hesabin PROCESSING/FINISHED bir siparisinde zaten aktif olan bir teklif
// tekrar sepete eklenemez.
public class OfferAlreadyActiveException extends BusinessException {

	public OfferAlreadyActiveException(Long custAcctId, Long prodOfrId) {
		super(MessageKeys.OFFER_ALREADY_ACTIVE, prodOfrId, custAcctId);
	}
}
