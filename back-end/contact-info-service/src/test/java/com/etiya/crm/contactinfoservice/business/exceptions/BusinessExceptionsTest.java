package com.etiya.crm.contactinfoservice.business.exceptions;

import org.junit.jupiter.api.Test;

import com.etiya.crm.contactinfoservice.constants.MessageKeys;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessExceptionsTest {

	@Test
	void addressLimitExceeded_hasNoArgs() {
		AddressLimitExceededException ex = new AddressLimitExceededException();
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.ADDRESS_MAX_EXCEEDED);
		assertThat(ex.getArgs()).isEmpty();
	}

	@Test
	void addressLinkedToAccount_hasNoArgs() {
		AddressLinkedToAccountException ex = new AddressLinkedToAccountException();
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.ADDRESS_LINKED_TO_BILLING_ACCOUNT);
		assertThat(ex.getArgs()).isEmpty();
	}

	@Test
	void addressNotFound_carriesId() {
		AddressNotFoundException ex = new AddressNotFoundException(10L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.ADDRESS_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(10L);
	}

	@Test
	void contactMediumNotFound_carriesId() {
		ContactMediumNotFoundException ex = new ContactMediumNotFoundException(20L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.CONTACT_MEDIUM_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(20L);
	}

	@Test
	void invalidContactMediumFormat_usesCallerProvidedMessageKey() {
		InvalidContactMediumFormatException ex = new InvalidContactMediumFormatException(
				MessageKeys.CONTACT_MEDIUM_INVALID_EMAIL_FORMAT);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.CONTACT_MEDIUM_INVALID_EMAIL_FORMAT);
		assertThat(ex.getArgs()).isEmpty();
	}

	@Test
	void primaryAddressDeletion_hasNoArgs() {
		PrimaryAddressDeletionException ex = new PrimaryAddressDeletionException();
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.PRIMARY_ADDRESS_CANNOT_BE_DELETED);
		assertThat(ex.getArgs()).isEmpty();
	}

}
