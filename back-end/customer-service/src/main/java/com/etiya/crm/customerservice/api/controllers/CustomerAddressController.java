package com.etiya.crm.customerservice.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.etiya.crm.customerservice.business.abstracts.CustomerAddressService;
import com.etiya.crm.customerservice.business.dtos.requests.AddressEditRequest;
import com.etiya.crm.customerservice.constants.Roles;
import com.etiya.crm.shared.contracts.address.AddressResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Musterinin adresleri (contact-info-service'e proxy) uc noktalari. */
@Tag(name = "Customers", description = "Musteri adres yonetimi")
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('" + Roles.CRM_AGENT + "')")
public class CustomerAddressController {

	private final CustomerAddressService addressService;

	@Operation(summary = "Musterinin tum adreslerini listele", description = "contact-info-service'e proxy (max 5 adet).")
	@GetMapping("/{custId}/addresses")
	public ResponseEntity<List<AddressResponse>> getAddresses(@PathVariable Long custId) {
		return ResponseEntity.ok(addressService.getAddresses(custId));
	}

	@Operation(summary = "Musteriye yeni adres ekle",
			description = "contact-info-service'e proxy. Musteri basina en fazla 5 adres kurali burada "
					+ "uygulanir (6. eklemede 409 doner) - contact-info-service bu kurali bilmez, genericttir.")
	@PostMapping("/{custId}/addresses")
	public ResponseEntity<AddressResponse> addAddress(@PathVariable Long custId,
			@Valid @RequestBody AddressEditRequest request) {
		AddressResponse response = addressService.addAddress(custId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "Var olan bir adresi guncelle (primary yapma dahil)",
			description = "contact-info-service'e proxy. addressId'nin gercekten bu custId'ye ait olup "
					+ "olmadigi kontrol edilir (IDOR korumasi) - baska musterinin adresi 404 doner.")
	@PutMapping("/{custId}/addresses/{addressId}")
	public ResponseEntity<AddressResponse> updateAddress(@PathVariable Long custId,
			@Parameter(description = "Guncellenecek adresin id'si (GET .../addresses cevabindaki 'id')") @PathVariable Long addressId,
			@Valid @RequestBody AddressEditRequest request) {
		return ResponseEntity.ok(addressService.updateAddress(custId, addressId, request));
	}

	@Operation(summary = "Adresi sil",
			description = "contact-info-service'e proxy. Birincil adres silinemez (409). Bir fatura "
					+ "hesabina bagli (billing) adres de silinemez (409) - once ilgili hesabin adresi "
					+ "degistirilmelidir. IDOR: baska musterinin adresi 404 doner.")
	@DeleteMapping("/{custId}/addresses/{addressId}")
	public ResponseEntity<Void> deleteAddress(@PathVariable Long custId,
			@Parameter(description = "Silinecek adresin id'si (GET .../addresses cevabindaki 'id')") @PathVariable Long addressId) {
		addressService.deleteAddress(custId, addressId);
		return ResponseEntity.noContent().build();
	}
}
