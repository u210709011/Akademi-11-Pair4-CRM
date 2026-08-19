package com.etiya.crm.customerservice.business.abstracts;

import com.etiya.crm.customerservice.business.dtos.requests.IndividualInfo;


/** Kimlik doğrulama sağlayıcısının servis sözleşmesidir. */
public interface IdentityVerificationService {

	/** Verilen kişinin kimliğini doğrular. */
	void verify(IndividualInfo individual);
}
