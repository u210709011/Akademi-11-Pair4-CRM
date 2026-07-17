package com.etiya.crm.customerservice.business.dtos.requests;

import com.etiya.crm.customerservice.constants.MessageKeys;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * ACC-001..014: "Create Billing Account" ekrani. addressId (mevcut adres
 * secimi) ile newAddress (yeni adres olusturma) alanlarindan tam olarak biri
 * doldurulmali - bkz. CustomerBusinessRules.ensureAddressProvided.
 */
public record CreateBillingAccountRequest(

		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String accountName,

		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String accountDesc,

		Long addressId,

		@Valid
		AddressInfo newAddress) {
}
