package com.etiya.crm.customerservice.business.rules;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.etiya.crm.customerservice.business.dtos.requests.AddressInfo;
import com.etiya.crm.customerservice.business.exceptions.AddressLimitExceededException;
import com.etiya.crm.customerservice.business.exceptions.AddressLinkedToAccountException;
import com.etiya.crm.customerservice.business.exceptions.AddressNotFoundException;
import com.etiya.crm.customerservice.business.exceptions.PrimaryAddressCannotBeDeletedException;
import com.etiya.crm.shared.contracts.address.AddressResponse;
import com.etiya.crm.shared.contracts.contactmedium.AddressCommand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AddressBusinessRulesTest {

	private final AddressBusinessRules rules = new AddressBusinessRules();

	@Test
	void toAddressCommandsWithPrimaryRule_marksOnlyFirstAddressAsPrimary() {
		List<AddressInfo> addresses = List.of(
				new AddressInfo(1L, "Cad 1", "No 1", "Ev"),
				new AddressInfo(2L, "Cad 2", "No 2", "Is"));

		List<AddressCommand> commands = rules.toAddressCommandsWithPrimaryRule(addresses);

		assertThat(commands).hasSize(2);
		assertThat(commands.get(0).primary()).isTrue();
		assertThat(commands.get(1).primary()).isFalse();
		assertThat(commands.get(0).cityId()).isEqualTo(1L);
	}

	@Test
	void validateAddressLimit_throws_whenAtOrAboveMax() {
		assertThatThrownBy(() -> rules.validateAddressLimit(5))
				.isInstanceOf(AddressLimitExceededException.class);
	}

	@Test
	void validateAddressLimit_ok_whenBelowMax() {
		assertThatCode(() -> rules.validateAddressLimit(4)).doesNotThrowAnyException();
	}

	@Test
	void ensureAddressBelongsToCustomer_returnsMatch() {
		AddressResponse match = address(20L, false);
		AddressResponse other = address(21L, false);

		AddressResponse result = rules.ensureAddressBelongsToCustomer(10L, 20L, List.of(other, match));

		assertThat(result).isSameAs(match);
	}

	@Test
	void ensureAddressBelongsToCustomer_throws_whenNotFound() {
		assertThatThrownBy(() -> rules.ensureAddressBelongsToCustomer(10L, 99L, List.of(address(20L, false))))
				.isInstanceOf(AddressNotFoundException.class);
	}

	@Test
	void ensureAddressNotPrimary_throws_whenPrimary() {
		assertThatThrownBy(() -> rules.ensureAddressNotPrimary(address(20L, true)))
				.isInstanceOf(PrimaryAddressCannotBeDeletedException.class);
	}

	@Test
	void ensureAddressNotPrimary_ok_whenNotPrimary() {
		assertThatCode(() -> rules.ensureAddressNotPrimary(address(20L, false))).doesNotThrowAnyException();
	}

	@Test
	void ensureAddressNotLinkedToBillingAccount_throws_whenLinked() {
		assertThatThrownBy(() -> rules.ensureAddressNotLinkedToBillingAccount(true))
				.isInstanceOf(AddressLinkedToAccountException.class);
	}

	@Test
	void ensureAddressNotLinkedToBillingAccount_ok_whenNotLinked() {
		assertThatCode(() -> rules.ensureAddressNotLinkedToBillingAccount(false)).doesNotThrowAnyException();
	}

	private AddressResponse address(Long id, boolean primary) {
		return new AddressResponse(id, 10L, 102L, 5L, "Cad", "No", "Ev", primary, Instant.now(), "sys", Instant.now(),
				"sys");
	}
}
