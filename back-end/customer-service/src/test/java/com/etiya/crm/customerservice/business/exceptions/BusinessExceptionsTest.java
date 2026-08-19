package com.etiya.crm.customerservice.business.exceptions;

import org.junit.jupiter.api.Test;

import com.etiya.crm.customerservice.constants.MessageKeys;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Butun basit BusinessException alt siniflarinin dogru messageKey/args
 * tasidigini dogrular - actual mesaj metni GlobalExceptionHandlerTest'te.
 */
class BusinessExceptionsTest {

	@Test
	void accountNumberCollision_carriesAccountNo() {
		AccountNumberCollisionException ex = new AccountNumberCollisionException("000042");
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.ACCOUNT_NUMBER_COLLISION);
		assertThat(ex.getArgs()).containsExactly("000042");
	}

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
	void addressNotFound_carriesCustIdAndAddressId() {
		AddressNotFoundException ex = new AddressNotFoundException(10L, 20L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.ADDRESS_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(10L, 20L);
	}

	@Test
	void billingAccountActiveCannotBeDeleted_hasNoArgs() {
		BillingAccountActiveCannotBeDeletedException ex = new BillingAccountActiveCannotBeDeletedException();
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.BILLING_ACCOUNT_ACTIVE_CANNOT_BE_DELETED);
		assertThat(ex.getArgs()).isEmpty();
	}

	@Test
	void billingAccountAddressConflict_hasNoArgs() {
		BillingAccountAddressConflictException ex = new BillingAccountAddressConflictException();
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.BILLING_ACCOUNT_ADDRESS_CONFLICT);
		assertThat(ex.getArgs()).isEmpty();
	}

	@Test
	void billingAccountAddressRequired_hasNoArgs() {
		BillingAccountAddressRequiredException ex = new BillingAccountAddressRequiredException();
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.BILLING_ACCOUNT_ADDRESS_REQUIRED);
		assertThat(ex.getArgs()).isEmpty();
	}

	@Test
	void billingAccountHasActiveProducts_hasNoArgs() {
		BillingAccountHasActiveProductsException ex = new BillingAccountHasActiveProductsException();
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.BILLING_ACCOUNT_HAS_ACTIVE_PRODUCTS);
		assertThat(ex.getArgs()).isEmpty();
	}

	@Test
	void billingAccountNotFound_carriesCustIdAndAccountId() {
		BillingAccountNotFoundException ex = new BillingAccountNotFoundException(10L, 30L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.BILLING_ACCOUNT_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(10L, 30L);
	}

	@Test
	void customerHasActiveBillingAccount_hasNoArgs() {
		CustomerHasActiveBillingAccountException ex = new CustomerHasActiveBillingAccountException();
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.CUSTOMER_HAS_ACTIVE_BILLING_ACCOUNT);
		assertThat(ex.getArgs()).isEmpty();
	}

	@Test
	void customerNotFound_carriesCustId() {
		CustomerNotFoundException ex = new CustomerNotFoundException(10L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.CUSTOMER_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(10L);
	}

	@Test
	void defaultAccountCannotBeChanged_hasNoArgs() {
		DefaultAccountCannotBeChangedException ex = new DefaultAccountCannotBeChangedException();
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.DEFAULT_ACCOUNT_CANNOT_BE_CHANGED);
		assertThat(ex.getArgs()).isEmpty();
	}

	@Test
	void defaultAccountCannotBeDeleted_hasNoArgs() {
		DefaultAccountCannotBeDeletedException ex = new DefaultAccountCannotBeDeletedException();
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.DEFAULT_ACCOUNT_CANNOT_BE_DELETED);
		assertThat(ex.getArgs()).isEmpty();
	}

	@Test
	void duplicateNationalId_hasNoArgs() {
		DuplicateNationalIdException ex = new DuplicateNationalIdException();
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.DUPLICATE_NATIONAL_ID);
		assertThat(ex.getArgs()).isEmpty();
	}

	@Test
	void identityVerificationFailed_hasNoArgs() {
		IdentityVerificationFailedException ex = new IdentityVerificationFailedException();
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.IDENTITY_VERIFICATION_FAILED);
		assertThat(ex.getArgs()).isEmpty();
	}

	@Test
	void invalidBirthDate_hasNoArgs() {
		InvalidBirthDateException ex = new InvalidBirthDateException();
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.BIRTH_DATE_INVALID);
		assertThat(ex.getArgs()).isEmpty();
	}

	@Test
	void onboardingFailed_carriesCause() {
		RuntimeException cause = new RuntimeException("downstream boom");
		OnboardingFailedException ex = new OnboardingFailedException(cause);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.ONBOARDING_FAILED);
		assertThat(ex.getCause()).isSameAs(cause);
		assertThat(ex.getArgs()).isEmpty();
	}

	@Test
	void primaryAddressCannotBeDeleted_hasNoArgs() {
		PrimaryAddressCannotBeDeletedException ex = new PrimaryAddressCannotBeDeletedException();
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.PRIMARY_ADDRESS_CANNOT_BE_DELETED);
		assertThat(ex.getArgs()).isEmpty();
	}

	@Test
	void searchFilterRequired_hasNoArgs() {
		SearchFilterRequiredException ex = new SearchFilterRequiredException();
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.SEARCH_FILTER_REQUIRED);
		assertThat(ex.getArgs()).isEmpty();
	}
}
