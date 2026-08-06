package com.etiya.crm.customerservice.business.dtos.requests;

import com.etiya.crm.customerservice.constants.MessageKeys;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ACC-001..014: "Create Billing Account" ekrani. addressId (mevcut adres
 * secimi) ile newAddress (yeni adres olusturma) alanlarindan tam olarak biri
 * doldurulmali - bkz. BillingAccountBusinessRules.ensureAddressProvided.
 * accountName ZORUNLUDUR - front-end tipik olarak secilen/eklenen adresin
 * addrDesc'ini ("Home" gibi) bu alana onceden doldurup gonderir, ama backend'de
 * otomatik turetme/fallback YOKTUR, alan bos gelirse 400 doner.
 */
@Schema(description = "POST /api/v1/customers/{custId}/accounts istek govdesi. "
		+ "addressId VEYA newAddress'ten TAM OLARAK BIRI doldurulmali - ikisi birden ya da hicbiri 400 doner.")
public record CreateBillingAccountRequest(

		@Schema(description = "Hesap adi", example = "Home")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@Size(max = 50, message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String accountName,

		@Schema(description = "Hesap aciklamasi", example = "Aylik elektrik/su faturasi icin")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String accountDesc,

		@Schema(description = "Musterinin VAR OLAN bir adresinin id'si (bkz. GET .../addresses). newAddress ile birlikte gonderilmez.",
				example = "null")
		Long addressId,

		@Schema(description = "Hesapla birlikte YENI bir adres olusturulacaksa doldurulur. addressId ile birlikte gonderilmez.")
		@Valid
		AddressInfo newAddress) {
}
