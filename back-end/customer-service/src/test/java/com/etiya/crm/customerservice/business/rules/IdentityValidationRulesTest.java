package com.etiya.crm.customerservice.business.rules;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.etiya.crm.customerservice.business.exceptions.DuplicateNationalIdException;
import com.etiya.crm.customerservice.business.exceptions.InvalidBirthDateException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IdentityValidationRulesTest {

	private final IdentityValidationRules rules = new IdentityValidationRules();

	@Test
	void validateBirthDate_throws_whenBeforeMinDate() {
		assertThatThrownBy(() -> rules.validateBirthDate(LocalDate.of(1899, 12, 31)))
				.isInstanceOf(InvalidBirthDateException.class);
	}

	@Test
	void validateBirthDate_throws_whenInTheFuture() {
		assertThatThrownBy(() -> rules.validateBirthDate(LocalDate.now().plusDays(1)))
				.isInstanceOf(InvalidBirthDateException.class);
	}

	@Test
	void validateBirthDate_ok_whenValid() {
		assertThatCode(() -> rules.validateBirthDate(LocalDate.of(1990, 6, 15))).doesNotThrowAnyException();
	}

	@Test
	void validateBirthDate_ok_atMinDateBoundary() {
		assertThatCode(() -> rules.validateBirthDate(LocalDate.of(1900, 1, 1))).doesNotThrowAnyException();
	}

	@Test
	void ensureUniqueNationalId_throws_whenAlreadyExists() {
		assertThatThrownBy(() -> rules.ensureUniqueNationalId(true))
				.isInstanceOf(DuplicateNationalIdException.class);
	}

	@Test
	void ensureUniqueNationalId_ok_whenNotExists() {
		assertThatCode(() -> rules.ensureUniqueNationalId(false)).doesNotThrowAnyException();
	}
}
