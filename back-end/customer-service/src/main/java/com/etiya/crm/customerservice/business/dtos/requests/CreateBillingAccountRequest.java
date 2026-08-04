package com.etiya.crm.customerservice.business.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

/**
 * ACC-001..014: "Create Billing Account" ekrani. addressId (mevcut adres
 * secimi) ile newAddress (yeni adres olusturma) alanlarindan tam olarak biri
 * doldurulmali - bkz. BillingAccountBusinessRules.ensureAddressProvided.
 * Hesap adi (accountName) burada YOKTUR - client'tan alinmaz, secilen/olusturulan
 * adresin addrDesc'inden (orn. "Home") otomatik turetilir (bkz. BillingAccountServiceImpl).
 */
@Schema(description = "POST /api/v1/customers/{custId}/accounts istek govdesi. "
		+ "addressId VEYA newAddress'ten TAM OLARAK BIRI doldurulmali - ikisi birden ya da hicbiri 400 doner. "
		+ "Hesap adi bu adresin addrDesc'inden otomatik turetilir, burada gonderilmez.")
public record CreateBillingAccountRequest(

		@Schema(description = "Hesap aciklamasi (opsiyonel)", example = "Aylik elektrik/su faturasi icin")
		String accountDesc,

		@Schema(description = "Musterinin VAR OLAN bir adresinin id'si (bkz. GET .../addresses). newAddress ile birlikte gonderilmez.",
				example = "null")
		Long addressId,

		@Schema(description = "Hesapla birlikte YENI bir adres olusturulacaksa doldurulur. addressId ile birlikte gonderilmez.")
		@Valid
		AddressInfo newAddress) {
}
