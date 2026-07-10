package com.etiya.crm.customerservice.business.exceptions;

import com.etiya.crm.customerservice.constants.MessageKeys;

/**
 * Verilen addressId, custId'nin sahip oldugu adresler arasinda bulunamadi.
 * IDOR onlemi: contact-info-service'teki ADDR tablosu polimorfik/paylasimli
 * oldugu icin bu kontrol olmadan bir customer, ID'sini tahmin ederek baska
 * bir customer'in adresini guncelleyebilirdi.
 */
public class AddressNotFoundException extends BusinessException {

	public AddressNotFoundException(Long custId, Long addressId) {
		super(MessageKeys.ADDRESS_NOT_FOUND, custId, addressId);
	}
}
