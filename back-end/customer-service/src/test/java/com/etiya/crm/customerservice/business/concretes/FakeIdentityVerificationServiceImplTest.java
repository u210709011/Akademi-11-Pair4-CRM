package com.etiya.crm.customerservice.business.concretes;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.etiya.crm.customerservice.business.dtos.requests.IndividualInfo;

import static org.assertj.core.api.Assertions.assertThatCode;

class FakeIdentityVerificationServiceImplTest {

	private final FakeIdentityVerificationServiceImpl service = new FakeIdentityVerificationServiceImpl();

	@Test
	void verify_neverThrows() {
		IndividualInfo individual = new IndividualInfo("Ahmet", null, "Yilmaz", LocalDate.of(1990, 6, 15), 1L, null,
				null, "10000000146");

		assertThatCode(() -> service.verify(individual)).doesNotThrowAnyException();
	}
}
