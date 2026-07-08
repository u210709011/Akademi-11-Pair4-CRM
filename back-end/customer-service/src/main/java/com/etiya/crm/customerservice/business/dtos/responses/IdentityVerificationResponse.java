package com.etiya.crm.customerservice.business.dtos.responses;

/** POST /onboarding/verify-identity basarili sonucu (ACC-009..013). */
public record IdentityVerificationResponse(boolean verified, String message) {

	public static IdentityVerificationResponse ok() {
		return new IdentityVerificationResponse(true, "Identity verified.");
	}
}
