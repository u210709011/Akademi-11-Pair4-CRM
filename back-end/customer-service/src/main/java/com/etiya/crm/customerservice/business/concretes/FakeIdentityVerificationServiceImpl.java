package com.etiya.crm.customerservice.business.concretes;

import org.springframework.stereotype.Service;

import com.etiya.crm.customerservice.business.abstracts.IdentityVerificationService;
import com.etiya.crm.customerservice.business.dtos.requests.IndividualInfo;
import com.etiya.crm.customerservice.constants.LogMessages;

import lombok.extern.slf4j.Slf4j;

/** KPS entegrasyonunu simüle eden sahte bir implementasyondur. */
@Slf4j
@Service
public class FakeIdentityVerificationServiceImpl implements IdentityVerificationService {

	@Override
	public void verify(IndividualInfo individual) {
		log.debug(LogMessages.FAKE_KPS_VERIFICATION, individual.nationalId());
	}
}
