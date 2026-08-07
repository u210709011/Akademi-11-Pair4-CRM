package com.etiya.crm.customerservice.business.concretes;

import org.springframework.stereotype.Service;

import com.etiya.crm.customerservice.business.abstracts.BillingAccountProductGuard;

/** order-service entegrasyonu gelene kadar gecici stub - bkz. BillingAccountProductGuard. */
@Service
public class NoOpBillingAccountProductGuard implements BillingAccountProductGuard {

	@Override
	public boolean hasLinkedProducts(Long custAcctId) {
		return false;
	}
}
