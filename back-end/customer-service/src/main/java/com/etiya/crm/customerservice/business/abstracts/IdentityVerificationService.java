package com.etiya.crm.customerservice.business.abstracts;

import com.etiya.crm.customerservice.business.dtos.requests.IndividualInfo;

/**
 * ACC-009/010: KPS (Kimlik Paylasimi Sistemi) uzerinden kimlik dogrulama.
 * Gercek KPS entegrasyonu henuz yok; implementasyon fake'tir.
 */
public interface IdentityVerificationService {

	/** Dogrulama basarisizsa IdentityVerificationFailedException firlatir. */
	void verify(IndividualInfo individual);
}
