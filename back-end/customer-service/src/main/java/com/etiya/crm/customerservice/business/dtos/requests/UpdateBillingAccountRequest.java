package com.etiya.crm.customerservice.business.dtos.requests;

import com.etiya.crm.customerservice.constants.MessageKeys;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * FR-010: "Update Billing Account" ekrani. Guncellenebilir alanlar sadece
 * accountName/accountDesc/adres - accountNo ve accountTpId burada YOK, hicbir
 * zaman degistirilemez (bkz. CustomerServiceImpl.updateBillingAccount).
 * addressId (var olan adres) ile newAddress (yeni adres olusturma) alanlarindan
 * tam olarak biri doldurulmali - CreateBillingAccountRequest ile ayni desen.
 */
@Schema(description = "PUT /api/v1/customers/{custId}/accounts/{accountId} istek govdesi. "
		+ "addressId VEYA newAddress'ten TAM OLARAK BIRI doldurulmali - ikisi birden ya da hicbiri 400 doner.")
public record UpdateBillingAccountRequest(

		@Schema(description = "Hesap adi", example = "Ev Faturasi")
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
