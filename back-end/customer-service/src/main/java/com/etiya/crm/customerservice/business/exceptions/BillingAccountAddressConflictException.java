package com.etiya.crm.customerservice.business.exceptions;

import com.etiya.crm.customerservice.constants.MessageKeys;

/** ACC-009: fatura hesabi icin hem mevcut adres (addressId) hem yeni adres (newAddress) birlikte gonderilemez. */
public class BillingAccountAddressConflictException extends BusinessException {

	public BillingAccountAddressConflictException() {
		super(MessageKeys.BILLING_ACCOUNT_ADDRESS_CONFLICT);
	}
}
