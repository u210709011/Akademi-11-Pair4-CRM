package com.etiya.crm.customerservice.business.concretes;

import org.springframework.stereotype.Service;

import com.etiya.crm.customerservice.business.abstracts.BillingAccountProductGuard;
import com.etiya.crm.customerservice.clients.controllers.OrderClient;

import lombok.RequiredArgsConstructor;

/**
 * ACC-004: order-service'e sorup pasif hesaba bagli urun olup olmadigini gercekten kontrol eder
 * (bkz. BillingAccountProductGuard). Downstream cagri basarisiz olursa (Feign hatasi/circuit
 * breaker OPEN) burada yutulmaz - GlobalExceptionHandler'in extend ettigi
 * AbstractDownstreamExceptionHandler caller'a 502/503 olarak yansitir; "erisilemedi" durumunu
 * sessizce "urun yok" (false) sayip silmeye izin vermek ACC-004'un onlemeye calistigi veri
 * butunlugu riskini geri getirir.
 */
@Service
@RequiredArgsConstructor
public class OrderServiceBillingAccountProductGuard implements BillingAccountProductGuard {

	private final OrderClient orderClient;

	@Override
	public boolean hasLinkedProducts(Long custAcctId) {
		return !orderClient.getItemsByCustAcctId(custAcctId).isEmpty();
	}
}
