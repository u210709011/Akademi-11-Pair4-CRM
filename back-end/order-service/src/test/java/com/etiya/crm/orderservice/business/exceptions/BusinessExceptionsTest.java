package com.etiya.crm.orderservice.business.exceptions;

import org.junit.jupiter.api.Test;

import com.etiya.crm.orderservice.constants.MessageKeys;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessExceptionsTest {

	@Test
	void accountNotBelongToCustomer_carriesAccountId() {
		AccountNotBelongToCustomerException ex = new AccountNotBelongToCustomerException(5L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.ACCOUNT_NOT_BELONG_TO_CUSTOMER);
		assertThat(ex.getArgs()).containsExactly(5L);
	}

	@Test
	void addressSelectionInvalid_hasNoArgs() {
		AddressSelectionInvalidException ex = new AddressSelectionInvalidException();
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.ADDRESS_SELECTION_INVALID);
		assertThat(ex.getArgs()).isEmpty();
	}

	@Test
	void bsnInterSpecNotFound_carriesShrtCode() {
		BsnInterSpecNotFoundException ex = new BsnInterSpecNotFoundException("ORDER");
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.BSN_INTER_SPEC_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly("ORDER");
	}

	@Test
	void duplicateBasketItem_carriesProdOfrId() {
		DuplicateBasketItemException ex = new DuplicateBasketItemException(10L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.DUPLICATE_BASKET_ITEM);
		assertThat(ex.getArgs()).containsExactly(10L);
	}

	@Test
	void orderNotFound_carriesCustOrdId() {
		OrderNotFoundException ex = new OrderNotFoundException(1L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.ORDER_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(1L);
	}

	@Test
	void addressNotBelongToCustomer_carriesAddressId() {
		AddressNotBelongToCustomerException ex = new AddressNotBelongToCustomerException(2L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.ADDRESS_NOT_BELONG_TO_CUSTOMER);
		assertThat(ex.getArgs()).containsExactly(2L);
	}

	@Test
	void campaignNotAppliedToOffering_carriesCmpgIdAndProdOfrId() {
		CampaignNotAppliedToOfferingException ex = new CampaignNotAppliedToOfferingException(1L, 2L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.CAMPAIGN_NOT_APPLIED_TO_OFFERING);
		assertThat(ex.getArgs()).containsExactly(1L, 2L);
	}

	@Test
	void characteristicValueMismatch_carriesCharValIdAndCharId() {
		CharacteristicValueMismatchException ex = new CharacteristicValueMismatchException(1L, 2L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.CHARACTERISTIC_VALUE_MISMATCH);
		assertThat(ex.getArgs()).containsExactly(1L, 2L);
	}

	@Test
	void offerAlreadyActive_carriesProdOfrIdThenCustAcctId() {
		OfferAlreadyActiveException ex = new OfferAlreadyActiveException(1L, 2L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.OFFER_ALREADY_ACTIVE);
		assertThat(ex.getArgs()).containsExactly(2L, 1L);
	}

	@Test
	void orderItemNotFound_carriesItemIdAndOrderId() {
		OrderItemNotFoundException ex = new OrderItemNotFoundException(1L, 2L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.ORDER_ITEM_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(1L, 2L);
	}

	@Test
	void orderNotEditable_carriesCustOrdId() {
		OrderNotEditableException ex = new OrderNotEditableException(1L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.ORDER_NOT_EDITABLE);
		assertThat(ex.getArgs()).containsExactly(1L);
	}

	@Test
	void serviceAddressMissing_carriesCustOrdId() {
		ServiceAddressMissingException ex = new ServiceAddressMissingException(1L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.SERVICE_ADDRESS_MISSING);
		assertThat(ex.getArgs()).containsExactly(1L);
	}

	@Test
	void characteristicValueMissing_carriesCharId() {
		CharacteristicValueMissingException ex = new CharacteristicValueMissingException(1L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.CHARACTERISTIC_VALUE_MISSING);
		assertThat(ex.getArgs()).containsExactly(1L);
	}

	@Test
	void conflictingBasketItem_carriesBothProdOfrIds() {
		ConflictingBasketItemException ex = new ConflictingBasketItemException(1L, 2L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.CONFLICTING_BASKET_ITEM);
		assertThat(ex.getArgs()).containsExactly(1L, 2L);
	}

	@Test
	void mandatoryCharacteristicMissing_carriesItemIdAndCharId() {
		MandatoryCharacteristicMissingException ex = new MandatoryCharacteristicMissingException(1L, 2L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.MANDATORY_CHARACTERISTIC_MISSING);
		assertThat(ex.getArgs()).containsExactly(1L, 2L);
	}

}
