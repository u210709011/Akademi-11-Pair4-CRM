package com.etiya.crm.customerservice.business.exceptions;

import com.etiya.crm.customerservice.constants.MessageKeys;

/** Aynı kimlik numarasıyla ikinci müşteri oluşturulmasını bildirir. */
public class DuplicateNationalIdException extends BusinessException {

	public DuplicateNationalIdException() {
		super(MessageKeys.DUPLICATE_NATIONAL_ID);
	}
}
