package com.etiya.crm.customerservice.business.exceptions;

import com.etiya.crm.customerservice.constants.MessageKeys;

/** ACC-004: pasif hesaba bagli aktif urun varsa fatura hesabi silinemez (order-service entegrasyonu bekleniyor). */
public class BillingAccountHasActiveProductsException extends BusinessException {

	public BillingAccountHasActiveProductsException() {
		super(MessageKeys.BILLING_ACCOUNT_HAS_ACTIVE_PRODUCTS);
	}
}
