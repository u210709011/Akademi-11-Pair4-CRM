package com.etiya.crm.customerservice.business.exceptions;

import com.etiya.crm.customerservice.constants.MessageKeys;

/** FR-005 ACC-011: birincil olmayan ama bir fatura hesabina bagli adres silinemez. */
public class AddressLinkedToAccountException extends BusinessException {

	public AddressLinkedToAccountException() {
		super(MessageKeys.ADDRESS_LINKED_TO_BILLING_ACCOUNT);
	}
}
