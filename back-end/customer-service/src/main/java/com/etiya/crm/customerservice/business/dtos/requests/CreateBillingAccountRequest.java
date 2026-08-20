package com.etiya.crm.customerservice.business.dtos.requests;

import com.etiya.crm.customerservice.constants.MessageKeys;
import com.etiya.crm.customerservice.constants.SwaggerText;

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
@Schema(description = SwaggerText.CREATE_BILLING_ACCOUNT_REQUEST_SCHEMA_DESCRIPTION)
public record CreateBillingAccountRequest(

		@Schema(description = SwaggerText.BILLING_ACCOUNT_ACCOUNT_NAME_DESCRIPTION, example = "Home")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@Size(max = 50, message = "{" + MessageKeys.FIELD_MAX_LENGTH + "}")
		String accountName,

		@Schema(description = SwaggerText.BILLING_ACCOUNT_ACCOUNT_DESC_DESCRIPTION, example = "Aylik elektrik/su faturasi icin")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String accountDesc,

		@Schema(description = SwaggerText.BILLING_ACCOUNT_ADDRESS_ID_DESCRIPTION)
		Long addressId,

		@Schema(description = SwaggerText.BILLING_ACCOUNT_NEW_ADDRESS_DESCRIPTION)
		@Valid
		AddressInfo newAddress) {
}
