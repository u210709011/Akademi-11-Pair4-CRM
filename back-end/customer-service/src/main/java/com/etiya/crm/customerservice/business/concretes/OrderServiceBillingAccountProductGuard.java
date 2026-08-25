package com.etiya.crm.customerservice.business.concretes;

import org.springframework.stereotype.Service;

import com.etiya.crm.customerservice.business.abstracts.BillingAccountProductGuard;
import com.etiya.crm.customerservice.clients.controllers.OrderClient;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class OrderServiceBillingAccountProductGuard implements BillingAccountProductGuard {

	private final OrderClient orderClient;

	@Override
	public boolean hasLinkedProducts(Long custAcctId) {
		return !orderClient.getItemsByCustAcctId(custAcctId).isEmpty();
	}
}
