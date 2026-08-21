package com.etiya.crm.orderservice.business.rules;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.etiya.crm.orderservice.business.dtos.requests.AddressInfoRequest;
import com.etiya.crm.orderservice.business.dtos.requests.BasketItemRequest;
import com.etiya.crm.orderservice.business.exceptions.AccountNotBelongToCustomerException;
import com.etiya.crm.orderservice.business.exceptions.AddressNotBelongToCustomerException;
import com.etiya.crm.orderservice.business.exceptions.AddressSelectionInvalidException;
import com.etiya.crm.orderservice.business.exceptions.ConflictingBasketItemException;
import com.etiya.crm.orderservice.business.exceptions.DuplicateBasketItemException;
import com.etiya.crm.orderservice.clients.responses.CustomerAccountResponse;
import com.etiya.crm.orderservice.clients.responses.ProductOfferingRelationResponse;
import com.etiya.crm.orderservice.entities.concretes.CustOrdItem;
import com.etiya.crm.shared.contracts.address.AddressResponse;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BasketValidationRulesTest {

	private final BasketValidationRules rules = new BasketValidationRules();

	@Test
	void ensureAddressProvided_throws_whenBothGiven() {
		assertThatThrownBy(() -> rules.ensureAddressProvided(1L, newAddress()))
				.isInstanceOf(AddressSelectionInvalidException.class);
	}

	@Test
	void ensureAddressProvided_throws_whenNeitherGiven() {
		assertThatThrownBy(() -> rules.ensureAddressProvided(null, null))
				.isInstanceOf(AddressSelectionInvalidException.class);
	}

	@Test
	void ensureAddressProvided_passes_whenOnlyExistingGiven() {
		assertThatCode(() -> rules.ensureAddressProvided(1L, null)).doesNotThrowAnyException();
	}

	@Test
	void ensureAddressProvided_passes_whenOnlyNewGiven() {
		assertThatCode(() -> rules.ensureAddressProvided(null, newAddress())).doesNotThrowAnyException();
	}

	@Test
	void ensureAccountBelongsToCustomer_throws_whenNoMatch() {
		List<CustomerAccountResponse> accounts = List.of(account(1L));

		assertThatThrownBy(() -> rules.ensureAccountBelongsToCustomer(2L, accounts))
				.isInstanceOf(AccountNotBelongToCustomerException.class);
	}

	@Test
	void ensureAccountBelongsToCustomer_passes_whenMatchFound() {
		List<CustomerAccountResponse> accounts = List.of(account(1L));

		assertThatCode(() -> rules.ensureAccountBelongsToCustomer(1L, accounts)).doesNotThrowAnyException();
	}

	@Test
	void ensureNoDuplicateItems_throws_whenSameOfferAndCampaignRepeated() {
		List<BasketItemRequest> items = List.of(item(10L, 1L), item(10L, 1L));

		assertThatThrownBy(() -> rules.ensureNoDuplicateItems(items))
				.isInstanceOf(DuplicateBasketItemException.class);
	}

	@Test
	void ensureNoDuplicateItems_passes_whenAllDistinct() {
		List<BasketItemRequest> items = List.of(item(10L, 1L), item(11L, 1L));

		assertThatCode(() -> rules.ensureNoDuplicateItems(items)).doesNotThrowAnyException();
	}

	@Test
	void ensureItemNotAlreadyInBasket_throws_whenDuplicateFound() {
		CustOrdItem existing = new CustOrdItem();
		existing.setProdOfrId(10L);
		existing.setCmpgId(1L);

		assertThatThrownBy(() -> rules.ensureItemNotAlreadyInBasket(item(10L, 1L), List.of(existing)))
				.isInstanceOf(DuplicateBasketItemException.class);
	}

	@Test
	void ensureItemNotAlreadyInBasket_passes_whenNoDuplicate() {
		CustOrdItem existing = new CustOrdItem();
		existing.setProdOfrId(10L);
		existing.setCmpgId(1L);

		assertThatCode(() -> rules.ensureItemNotAlreadyInBasket(item(11L, 1L), List.of(existing)))
				.doesNotThrowAnyException();
	}

	@Test
	void ensureNoConflictingItems_throws_whenExclusiveActiveRelationExists() {
		List<BasketItemRequest> items = List.of(item(10L, null), item(20L, null));
		List<ProductOfferingRelationResponse> relations = List.of(
				new ProductOfferingRelationResponse(10L, 20L, false, true, true));

		assertThatThrownBy(() -> rules.ensureNoConflictingItems(items, relations))
				.isInstanceOf(ConflictingBasketItemException.class);
	}

	@Test
	void ensureNoConflictingItems_passes_whenRelationIsInactive() {
		List<BasketItemRequest> items = List.of(item(10L, null), item(20L, null));
		List<ProductOfferingRelationResponse> relations = List.of(
				new ProductOfferingRelationResponse(10L, 20L, false, true, false));

		assertThatCode(() -> rules.ensureNoConflictingItems(items, relations)).doesNotThrowAnyException();
	}

	@Test
	void ensureItemNotConflicting_throws_whenNewItemExclusiveWithExisting() {
		CustOrdItem existing = new CustOrdItem();
		existing.setProdOfrId(20L);
		List<ProductOfferingRelationResponse> relations = List.of(
				new ProductOfferingRelationResponse(10L, 20L, false, true, true));

		assertThatThrownBy(() -> rules.ensureItemNotConflicting(item(10L, null), List.of(existing), relations))
				.isInstanceOf(ConflictingBasketItemException.class);
	}

	@Test
	void ensureItemNotConflicting_passes_whenNoExclusiveRelation() {
		CustOrdItem existing = new CustOrdItem();
		existing.setProdOfrId(30L);
		List<ProductOfferingRelationResponse> relations = List.of();

		assertThatCode(() -> rules.ensureItemNotConflicting(item(10L, null), List.of(existing), relations))
				.doesNotThrowAnyException();
	}

	@Test
	void ensureAddressBelongsToCustomer_passes_whenOwnedDirectlyByCustomer() {
		AddressResponse address = addressResponse(1L, 100L, 12L);

		assertThatCode(() -> rules.ensureAddressBelongsToCustomer(address, 100L, 12L, List.of()))
				.doesNotThrowAnyException();
	}

	@Test
	void ensureAddressBelongsToCustomer_passes_whenOwnedByOneOfCustomerAccounts() {
		AddressResponse address = addressResponse(1L, 999L, 12L);
		List<CustomerAccountResponse> accounts = List.of(account(1L));

		assertThatCode(() -> rules.ensureAddressBelongsToCustomer(address, 100L, 12L, accounts))
				.doesNotThrowAnyException();
	}

	@Test
	void ensureAddressBelongsToCustomer_throws_whenNotOwnedByCustomerOrAnyAccount() {
		AddressResponse address = addressResponse(1L, 999L, 12L);
		List<CustomerAccountResponse> accounts = List.of(account(2L));

		assertThatThrownBy(() -> rules.ensureAddressBelongsToCustomer(address, 100L, 12L, accounts))
				.isInstanceOf(AddressNotBelongToCustomerException.class);
	}

	private static BasketItemRequest item(Long prodOfrId, Long cmpgId) {
		return new BasketItemRequest(prodOfrId, cmpgId, List.of());
	}

	private static AddressInfoRequest newAddress() {
		return new AddressInfoRequest(1L, "Street", "12", "Desc");
	}

	private static CustomerAccountResponse account(Long addressId) {
		return new CustomerAccountResponse(1L, "ACC-1", "Account", "Desc", addressId, 1L, 1L, true);
	}

	private static AddressResponse addressResponse(Long id, Long rowId, Long dataTypeId) {
		return new AddressResponse(id, rowId, dataTypeId, 5L, "Street", "12", "Desc", true, null, null, null, null);
	}

}
