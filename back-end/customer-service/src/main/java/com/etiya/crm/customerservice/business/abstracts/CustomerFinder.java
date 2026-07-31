package com.etiya.crm.customerservice.business.abstracts;

import com.etiya.crm.customerservice.entities.concretes.Customer;

/**
 * "Customer aggregate'i var mi ve aktif mi" kontrolunu tek bir yerde tutar -
 * her alt-kaynak servisi (Individual/Address/Contact/BillingAccount) kendi
 * custId'sini bagimsiz olarak dogrulayabilsin diye; boylece hicbiri artik
 * merkezi bir CustomerService facade'ine bagimli olmak zorunda degildir.
 */
public interface CustomerFinder {

	/** custId'ye ait aktif musteri yoksa CustomerNotFoundException firlatir. */
	Customer getActiveCustomerOrThrow(Long custId);
}
