package com.etiya.crm.customerservice.business.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

/**
 * FR-010: "Update Billing Account" ekrani. Guncellenebilir alanlar sadece
 * accountDesc/adres - accountNo ve accountTpId burada YOK, hicbir zaman
 * degistirilemez (bkz. CustomerServiceImpl.updateBillingAccount). accountName de
 * YOKTUR - client'tan alinmaz, adres degistirilirse hesap adi da yeni adresin
 * addrDesc'inden otomatik yeniden turetilir (bkz. BillingAccountServiceImpl).
 * addressId (var olan adres) ile newAddress (yeni adres olusturma) alanlarindan
 * tam olarak biri doldurulmali - CreateBillingAccountRequest ile ayni desen.
 */
@Schema(description = "PUT /api/v1/customers/{custId}/accounts/{accountId} istek govdesi. "
		+ "addressId VEYA newAddress'ten TAM OLARAK BIRI doldurulmali - ikisi birden ya da hicbiri 400 doner. "
		+ "Hesap adi bu adresin addrDesc'inden otomatik turetilir, burada gonderilmez.")
public record UpdateBillingAccountRequest(

		@Schema(description = "Hesap aciklamasi (opsiyonel)", example = "Aylik elektrik/su faturasi icin")
		String accountDesc,

		@Schema(description = "Musterinin VAR OLAN bir adresinin id'si (bkz. GET .../addresses). newAddress ile birlikte gonderilmez.",
				example = "null")
		Long addressId,

		@Schema(description = "Hesapla birlikte YENI bir adres olusturulacaksa doldurulur. addressId ile birlikte gonderilmez.")
		@Valid
		AddressInfo newAddress) {
}
