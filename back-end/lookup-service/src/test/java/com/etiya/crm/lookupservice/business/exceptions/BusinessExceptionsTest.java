package com.etiya.crm.lookupservice.business.exceptions;

import org.junit.jupiter.api.Test;

import com.etiya.crm.lookupservice.constants.MessageKeys;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessExceptionsTest {

	@Test
	void entityNotFound_carriesEntityNameAndId() {
		EntityNotFoundException ex = new EntityNotFoundException("GnlChar", 1L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.ENTITY_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly("GnlChar", 1L);
	}

	@Test
	void invalidDateRange_carriesSdateAndEdate() {
		InvalidDateRangeException ex = new InvalidDateRangeException("2026-01-01", "2025-01-01");
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.GNL_CHAR_VAL_INVALID_DATE_RANGE);
		assertThat(ex.getArgs()).containsExactly("2026-01-01", "2025-01-01");
	}

}
