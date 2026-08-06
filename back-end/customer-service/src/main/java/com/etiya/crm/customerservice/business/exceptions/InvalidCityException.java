package com.etiya.crm.customerservice.business.exceptions;

import com.etiya.crm.customerservice.constants.MessageKeys;

/** B-07/B-14: adres olusturma/guncellemede cityId lookup-service CITY grubunda bulunamadi. */
public class InvalidCityException extends BusinessException {

	public InvalidCityException(Long cityId) {
		super(MessageKeys.CITY_INVALID, cityId);
	}
}
