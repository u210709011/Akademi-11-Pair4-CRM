package com.etiya.crm.customerservice.business.dtos.requests;

import com.etiya.crm.customerservice.constants.MessageKeys;
import com.etiya.crm.customerservice.constants.SwaggerText;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * FR-010: "Update Billing Account" ekrani. Guncellenebilir alanlar accountName/
 * accountDesc/adres - accountNo ve accountTpId burada YOK, hicbir zaman
 * degistirilemez (bkz. CustomerServiceImpl.updateBillingAccount). accountName
 * ZORUNLUDUR - front-end tipik olarak (muhtemelen degisen) adresin addrDesc'ini
 * onceden doldurup gonderir, ama backend'de otomatik turetme/fallback YOKTUR.
 * addressId (var olan adres) ile newAddress (yeni adres olusturma) alanlarindan
 * tam olarak biri doldurulmali - CreateBillingAccountRequest ile ayni desen.
 */
@Schema(description = SwaggerText.UPDATE_BILLING_ACCOUNT_REQUEST_SCHEMA_DESCRIPTION)
public record UpdateBillingAccountRequest(

		@Schema(description = SwaggerText.BILLING_ACCOUNT_ACCOUNT_NAME_DESCRIPTION)
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@Size(max = 50, message = "{" + MessageKeys.FIELD_MAX_LENGTH + "}")
		String accountName,

		@Schema(description = SwaggerText.BILLING_ACCOUNT_ACCOUNT_DESC_DESCRIPTION)
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String accountDesc,

		@Schema(description = SwaggerText.BILLING_ACCOUNT_ADDRESS_ID_DESCRIPTION)
		Long addressId,

		@Schema(description = SwaggerText.BILLING_ACCOUNT_NEW_ADDRESS_DESCRIPTION)
		@Valid
		AddressInfo newAddress) {
}
