package com.etiya.crm.customerservice.business.concretes;

import org.springframework.stereotype.Service;

import com.etiya.crm.customerservice.business.abstracts.IdentityVerificationService;
import com.etiya.crm.customerservice.business.dtos.requests.IndividualInfo;
import com.etiya.crm.customerservice.constants.LogMessages;

import lombok.extern.slf4j.Slf4j;

/**
 * KPS entegrasyonu henuz yapilmadi (tamamen fake). Gercek KPS servisi devreye
 * girdiginde bu sinif bir Feign client (clients/KpsClient) cagiran gercek bir
 * implementasyonla degistirilmeli; IdentityVerificationService arayuzu ve
 * cagiran taraf (CustomerService) degismeden kalir.
 */
@Slf4j
@Service
public class FakeIdentityVerificationServiceImpl implements IdentityVerificationService {

	@Override
	public void verify(IndividualInfo individual) {
		log.debug(LogMessages.FAKE_KPS_VERIFICATION, individual.nationalId());
	}
}
