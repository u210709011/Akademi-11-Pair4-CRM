package com.etiya.crm.partyservice.business.rules;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.partyservice.business.exceptions.DuplicateNationalIdException;
import com.etiya.crm.partyservice.dataAccess.abstracts.IndividualRepository;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndividualBusinessRulesTest {

	@Mock
	private IndividualRepository individualRepository;

	@InjectMocks
	private IndividualBusinessRules rules;

	@Test
	void checkNationalIdNotDuplicate_throws_whenAlreadyExists() {
		when(individualRepository.existsByNationalIdAndActiveTrue("10000000146")).thenReturn(true);

		assertThatThrownBy(() -> rules.checkNationalIdNotDuplicate("10000000146"))
				.isInstanceOf(DuplicateNationalIdException.class);
	}

	@Test
	void checkNationalIdNotDuplicate_ok_whenNotExists() {
		when(individualRepository.existsByNationalIdAndActiveTrue("10000000146")).thenReturn(false);

		assertThatCode(() -> rules.checkNationalIdNotDuplicate("10000000146")).doesNotThrowAnyException();
	}

	@Test
	void checkNationalIdNotDuplicateForUpdate_throws_whenAnotherIndividualHasSameNationalId() {
		when(individualRepository.existsByNationalIdAndActiveTrueAndIndividualIdNot("10000000146", 5L))
				.thenReturn(true);

		assertThatThrownBy(() -> rules.checkNationalIdNotDuplicateForUpdate("10000000146", 5L))
				.isInstanceOf(DuplicateNationalIdException.class);
	}

	@Test
	void checkNationalIdNotDuplicateForUpdate_ok_whenNoConflict() {
		when(individualRepository.existsByNationalIdAndActiveTrueAndIndividualIdNot("10000000146", 5L))
				.thenReturn(false);

		assertThatCode(() -> rules.checkNationalIdNotDuplicateForUpdate("10000000146", 5L))
				.doesNotThrowAnyException();
	}
}
