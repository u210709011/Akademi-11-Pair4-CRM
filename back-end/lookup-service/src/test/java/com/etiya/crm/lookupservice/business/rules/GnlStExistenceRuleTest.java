package com.etiya.crm.lookupservice.business.rules;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlStRepository;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GnlStExistenceRuleTest {

	@Mock
	private GnlStRepository gnlStRepository;

	@InjectMocks
	private GnlStExistenceRule rule;

	@Test
	void ensureExists_passes_whenStIdExists() {
		when(gnlStRepository.existsById(1L)).thenReturn(true);

		assertThatCode(() -> rule.ensureExists(1L)).doesNotThrowAnyException();
	}

	@Test
	void ensureExists_throws_whenStIdDoesNotExist() {
		when(gnlStRepository.existsById(1L)).thenReturn(false);

		assertThatThrownBy(() -> rule.ensureExists(1L)).isInstanceOf(EntityNotFoundException.class);
	}

}
