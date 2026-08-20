package com.etiya.crm.customerservice.business.rules;

import org.junit.jupiter.api.Test;

import com.etiya.crm.customerservice.business.dtos.requests.CustomerSearchRequest;
import com.etiya.crm.customerservice.business.exceptions.SearchFilterRequiredException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** FR-002 ACC-003: /customers/search en az bir filtre gerektirir. */
class CustomerBusinessRulesTest {

	private final CustomerBusinessRules rules = new CustomerBusinessRules();

	@Test
	void ensureAtLeastOneFilterProvided_throws_whenAllFieldsBlank() {
		CustomerSearchRequest request = new CustomerSearchRequest(null, "  ", null, null, null, null);

		assertThatThrownBy(() -> rules.ensureAtLeastOneFilterProvided(request))
				.isInstanceOf(SearchFilterRequiredException.class);
	}

	@Test
	void ensureAtLeastOneFilterProvided_ok_whenFirstNameProvided() {
		CustomerSearchRequest request = new CustomerSearchRequest("Ahmet", null, null, null, null, null);

		assertThatCode(() -> rules.ensureAtLeastOneFilterProvided(request)).doesNotThrowAnyException();
	}

	@Test
	void ensureAtLeastOneFilterProvided_ok_whenOnlyCustIdProvided() {
		CustomerSearchRequest request = new CustomerSearchRequest(null, null, null, null, 10L, null);

		assertThatCode(() -> rules.ensureAtLeastOneFilterProvided(request)).doesNotThrowAnyException();
	}

	@Test
	void ensureAtLeastOneFilterProvided_ok_whenOnlyGsmProvided() {
		CustomerSearchRequest request = new CustomerSearchRequest(null, null, null, null, null, "5551234567");

		assertThatCode(() -> rules.ensureAtLeastOneFilterProvided(request)).doesNotThrowAnyException();
	}
}
