package com.etiya.crm.customerservice.business.abstracts;

import java.util.List;

import com.etiya.crm.customerservice.business.dtos.requests.AddressEditRequest;
import com.etiya.crm.customerservice.business.dtos.requests.AddressInfo;
import com.etiya.crm.shared.contracts.address.AddressResponse;

/** Musterinin adreslerini (contact-info-service'in sahip oldugu ADDR) ve buna ozel FR-005 kurallarini yonetir. */
public interface CustomerAddressService {

	List<AddressResponse> getAddresses(Long custId);

	AddressResponse addAddress(Long custId, AddressEditRequest request);

	AddressResponse updateAddress(Long custId, Long addressId, AddressEditRequest request);

	/** FR-005 ACC-008..012: IDOR + primary guard + faturaya bagli adres guard'i. */
	void deleteAddress(Long custId, Long addressId);

	/**
	 * Billing account CRUD'unun ihtiyaci: var olan bir addressId'yi dogrular ya da
	 * newAddress'ten yeni bir adres olusturup id'sini doner. BillingAccountService
	 * bu metoda bagimlidir (interface uzerinden, dogrudan implementasyona degil).
	 */
	Long resolveBillingAddressId(Long custId, Long addressId, AddressInfo newAddress);
}
