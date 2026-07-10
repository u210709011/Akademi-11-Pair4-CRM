package com.etiya.crm.customerservice.business.exceptions;

import com.etiya.crm.customerservice.constants.MessageKeys;

/** ACC-012: ayni Nationality ID mevcutsa kullanici ilerleyememeli. */
public class DuplicateNationalIdException extends BusinessException {

	public DuplicateNationalIdException() {
		super(MessageKeys.DUPLICATE_NATIONAL_ID);
	}
}
