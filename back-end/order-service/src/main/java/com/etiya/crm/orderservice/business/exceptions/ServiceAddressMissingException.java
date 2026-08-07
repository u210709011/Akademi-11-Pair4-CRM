package com.etiya.crm.orderservice.business.exceptions;

import com.etiya.crm.orderservice.constants.MessageKeys;

/** finishOrder cagirildiginda siparisin henuz kaydedilmis bir servis adresi yoksa firlatilir. */
public class ServiceAddressMissingException extends BusinessException {

	public ServiceAddressMissingException(Long custOrdId) {
		super(MessageKeys.SERVICE_ADDRESS_MISSING, custOrdId);
	}
}
