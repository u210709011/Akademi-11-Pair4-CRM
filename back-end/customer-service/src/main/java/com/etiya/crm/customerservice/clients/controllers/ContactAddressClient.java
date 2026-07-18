package com.etiya.crm.customerservice.clients.controllers;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.etiya.crm.shared.contracts.address.CreateAddressRequest;
import com.etiya.crm.shared.contracts.contactmedium.CreateContactCommand;
import com.etiya.crm.shared.contracts.contactmedium.CreateContactMediumRequest;
import com.etiya.crm.shared.contracts.address.UpdateAddressRequest;
import com.etiya.crm.shared.contracts.contactmedium.UpdateContactMediumRequest;
import com.etiya.crm.shared.contracts.address.AddressResponse;
import com.etiya.crm.shared.contracts.contactmedium.ContactMediumResponse;

/**
 * contact-info-service ile haberlesme kontrati. Eureka'daki gercek servis adi
 * "contact-info-service"tir (bkz. contact-info-service/application.yml) -
 * onceden yanlislikla "contact-address-service" yazilmisti; bu isim Eureka'da
 * hic kayitli olmadigindan onboarding call'lari servis bulunamadi hatasiyla
 * patliyordu.
 *
 * createContact/deleteByCustomerId artik contact-info-service'te GERCEKTEN VAR
 * (ContactMediumController: composite POST/DELETE, bkz. CUSTOMER_EDIT_INTEGRATION.md
 * SS2 - cozuldu). addContactMedium ise ayni controller'daki TEKIL ekleme
 * endpoint'ine (/single) gider; root POST artik composite create'e ait oldugu
 * icin ikisi ayni path olamaz.
 */
@FeignClient(name = "contact-info-service")
public interface ContactAddressClient {

	@PostMapping("/api/v1/contact-mediums")
	void createContact(@RequestBody CreateContactCommand command);

	// Onboarding compensation: customer-service tarafi basarisiz olursa geri alinir.
	@DeleteMapping("/api/v1/contact-mediums/customer/{custId}")
	void deleteByCustomerId(@PathVariable("custId") Long custId);

	// --- Edit akisi: contact-info-service'in mevcut tekil CRUD endpoint'leri ---

	@GetMapping("/api/v1/addresses")
	List<AddressResponse> getAddressesByCustomer(@RequestParam("rowId") Long rowId,
			@RequestParam("dataTypeId") Long dataTypeId);

	@PostMapping("/api/v1/addresses")
	AddressResponse addAddress(@RequestBody CreateAddressRequest request);

	@PutMapping("/api/v1/addresses/{id}")
	AddressResponse updateAddress(@PathVariable("id") Long id, @RequestBody UpdateAddressRequest request);

	@GetMapping("/api/v1/contact-mediums")
	List<ContactMediumResponse> getContactMediumsByCustomer(@RequestParam("rowId") Long rowId,
			@RequestParam("dataTypeId") Long dataTypeId);

	@PutMapping("/api/v1/contact-mediums/{id}")
	ContactMediumResponse updateContactMedium(@PathVariable("id") Long id,
			@RequestBody UpdateContactMediumRequest request);

	// homePhone/fax edit sirasinda ilk kez ekleniyorsa kullanilir. /single: root
	// POST artik ContactMediumController'da composite createContact'a ait.
	@PostMapping("/api/v1/contact-mediums/single")
	ContactMediumResponse addContactMedium(@RequestBody CreateContactMediumRequest request);
}
