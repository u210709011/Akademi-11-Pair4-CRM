package com.etiya.crm.orderservice.business.exceptions;

import com.etiya.crm.orderservice.constants.MessageKeys;

/**
 * FR-015 ACC-009/TC-015-44: finishOrder cagirildiginda, sepetteki bir item'in teklifi mandatory
 * isaretli bir karakteristigi henuz doldurulmamissa firlatilir - bkz.
 * CustOrdManager.ensureMandatoryCharacteristicsProvided.
 */
public class MandatoryCharacteristicMissingException extends BusinessException {

	public MandatoryCharacteristicMissingException(Long custOrdItemId, Long charId) {
		super(MessageKeys.MANDATORY_CHARACTERISTIC_MISSING, custOrdItemId, charId);
	}
}
