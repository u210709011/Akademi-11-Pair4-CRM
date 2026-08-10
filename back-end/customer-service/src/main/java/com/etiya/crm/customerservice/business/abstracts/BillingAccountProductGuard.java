package com.etiya.crm.customerservice.business.abstracts;

/**
 * ACC-004: fatura hesabi silinirken pasif hesaba bagli aktif urun olup olmadigini order-service'e
 * sorar. bkz. OrderServiceBillingAccountProductGuard (order-service GET /api/v1/orders/by-account
 * uzerinden gercek kontrol).
 */
public interface BillingAccountProductGuard {

	boolean hasLinkedProducts(Long custAcctId);
}
