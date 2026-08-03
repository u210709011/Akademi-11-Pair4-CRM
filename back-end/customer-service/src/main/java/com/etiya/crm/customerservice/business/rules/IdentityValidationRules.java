package com.etiya.crm.customerservice.business.rules;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.etiya.crm.customerservice.business.exceptions.DuplicateNationalIdException;
import com.etiya.crm.customerservice.business.exceptions.InvalidBirthDateException;

/** Onboarding ve kisisel bilgi editleme akislarinin ortak kimlik dogrulama kurallari. */
@Component
public class IdentityValidationRules {

	private static final LocalDate MIN_BIRTH_DATE = LocalDate.of(1900, 1, 1);

	/** ACC-007: 01/01/1900 oncesi ya da bugunden sonraki tarihler gecersizdir. */
	public void validateBirthDate(LocalDate birthDate) {
		LocalDate today = LocalDate.now();
		if (birthDate.isBefore(MIN_BIRTH_DATE) || birthDate.isAfter(today)) {
			throw new InvalidBirthDateException();
		}
	}

	/** ACC-011/012: ayni Nationality ID mevcutsa ilerlenemez. */
	public void ensureUniqueNationalId(boolean nationalIdAlreadyExists) {
		if (nationalIdAlreadyExists) {
			throw new DuplicateNationalIdException();
		}
	}
}
