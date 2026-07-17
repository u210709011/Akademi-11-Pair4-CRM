package com.etiya.crm.customerservice.business.exceptions;

import com.etiya.crm.customerservice.constants.MessageKeys;

/** ACC-009: fatura hesabi icin ne mevcut adres ne de yeni adres verilmemis. */
public class BillingAccountAddressRequiredException extends BusinessException {

	public BillingAccountAddressRequiredException() {
		super(MessageKeys.BILLING_ACCOUNT_ADDRESS_REQUIRED);
	}
}
