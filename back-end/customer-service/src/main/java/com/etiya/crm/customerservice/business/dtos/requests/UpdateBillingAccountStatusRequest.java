package com.etiya.crm.customerservice.business.dtos.requests;

import com.etiya.crm.customerservice.constants.MessageKeys;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * PATCH /api/v1/customers/{custId}/accounts/{accountId}/status istek govdesi. Bu, silme (DELETE,
 * acct_st_id=DEL) ile ayri bir kavramdir: sadece ACTIVE<->PASSIVE gecisini kapsar - DELETED bu uctan
 * hicbir zaman set edilemez, silme sadece DELETE endpoint'inden yapilir (bkz. CustomerAccount.acctStId).
 */
@Schema(description = "Fatura hesabinin aktiflik durumunu degistirir (soft-delete DEGIL).")
public record UpdateBillingAccountStatusRequest(

		@Schema(description = "Yeni hesap durumu", example = "PASSIVE", allowableValues = { ACTIVE, PASSIVE })
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@Pattern(regexp = ACTIVE + "|" + PASSIVE, message = "{" + MessageKeys.BILLING_ACCOUNT_STATUS_INVALID + "}")
		String status) {

	/**
	 * Bu API sozlesmesindeki durum degerleri; lookup-service'in GNL_ST shrt_code'lariyla
	 * (GnlStCodes.ACTIVE = "ACTV") KARISTIRILMAMALI - farkli bir deger uzayi.
	 */
	public static final String ACTIVE = "ACTIVE";
	public static final String PASSIVE = "PASSIVE";
}
