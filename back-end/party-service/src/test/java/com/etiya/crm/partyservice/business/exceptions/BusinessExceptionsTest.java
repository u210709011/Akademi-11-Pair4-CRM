package com.etiya.crm.partyservice.business.exceptions;

import org.junit.jupiter.api.Test;

import com.etiya.crm.partyservice.constants.MessageKeys;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessExceptionsTest {

	@Test
	void duplicateNationalId_hasNoArgs() {
		DuplicateNationalIdException ex = new DuplicateNationalIdException();
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.DUPLICATE_NATIONAL_ID);
		assertThat(ex.getArgs()).isEmpty();
	}

	@Test
	void individualNotFound_carriesPartyRoleId() {
		IndividualNotFoundException ex = new IndividualNotFoundException(100L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.INDIVIDUAL_NOT_FOUND_FOR_PARTY_ROLE);
		assertThat(ex.getArgs()).containsExactly(100L);
	}

	@Test
	void lookupValueNotFound_carriesEntCodeNameAndShrtCode() {
		LookupValueNotFoundException ex = new LookupValueNotFoundException("CAM_PARTY_TYPE", "INDV");
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.LOOKUP_VALUE_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly("CAM_PARTY_TYPE", "INDV");
	}

	@Test
	void partyNotFound_carriesPartyId() {
		PartyNotFoundException ex = new PartyNotFoundException(10L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.PARTY_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(10L);
	}

	@Test
	void partyRoleNotFound_carriesPartyRoleId() {
		PartyRoleNotFoundException ex = new PartyRoleNotFoundException(100L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.PARTY_ROLE_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(100L);
	}
}
