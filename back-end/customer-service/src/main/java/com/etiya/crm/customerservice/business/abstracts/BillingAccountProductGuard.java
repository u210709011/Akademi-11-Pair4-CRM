package com.etiya.crm.customerservice.business.abstracts;

/**
 * ACC-004: fatura hesabi silinirken pasif hesaba bagli aktif urun olup olmadigini order-service'e
 * sorar. Bugun order-service entegrasyonu yok, bu yuzden NoOpBillingAccountProductGuard hep "urun
 * yok" (false) doner. order-service Feign client'i hazir oldugunda tek yapilmasi gereken, bu
 * arayuze yeni bir @Primary implementasyon eklemek - BillingAccountServiceImpl ve
 * BillingAccountBusinessRules.ensureNoLinkedProducts hic degismez (bkz. BRAIN SS3 FR-011).
 */
public interface BillingAccountProductGuard {

	boolean hasLinkedProducts(Long custAcctId);
}
