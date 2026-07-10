package com.etiya.crm.customerservice.business.exceptions;

import com.etiya.crm.customerservice.constants.MessageKeys;

/** ACC-007: 01/01/1900 oncesi ya da bugunden sonraki tarihler gecersizdir. */
public class InvalidBirthDateException extends BusinessException {

	public InvalidBirthDateException() {
		super(MessageKeys.BIRTH_DATE_INVALID);
	}
}
