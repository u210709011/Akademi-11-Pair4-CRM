package com.etiya.crm.customerservice.business.dtos.responses;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseDtosTest {

	@Test
	void customerAccountResponse_carriesAllFields() {
		CustomerAccountResponse response = new CustomerAccountResponse(1L, "000001", "Home", "Aylik fatura", 500L,
				5L, 601L, true);

		assertThat(response.custAcctId()).isEqualTo(1L);
		assertThat(response.accountNo()).isEqualTo("000001");
		assertThat(response.accountName()).isEqualTo("Home");
		assertThat(response.accountDesc()).isEqualTo("Aylik fatura");
		assertThat(response.accountTpId()).isEqualTo(500L);
		assertThat(response.addressId()).isEqualTo(5L);
		assertThat(response.acctStId()).isEqualTo(601L);
		assertThat(response.active()).isTrue();
	}

	@Test
	void addressBillingAccountsResponse_carriesCountAndAccounts() {
		CustomerAccountResponse account = new CustomerAccountResponse(1L, "000001", "Home", "Aylik", 500L, 5L, 601L,
				true);

		AddressBillingAccountsResponse response = new AddressBillingAccountsResponse(1L, List.of(account));

		assertThat(response.count()).isEqualTo(1L);
		assertThat(response.accounts()).containsExactly(account);
	}

	@Test
	void customerResponse_carriesAllFields() {
		CustomerAccountResponse account = new CustomerAccountResponse(1L, "000001", "Home", "Aylik", 500L, 5L, 601L,
				true);

		CustomerResponse response = new CustomerResponse(42L, "000042", 100L, 1L, true, List.of(account));

		assertThat(response.custId()).isEqualTo(42L);
		assertThat(response.custNo()).isEqualTo("000042");
		assertThat(response.partyRoleId()).isEqualTo(100L);
		assertThat(response.custTpId()).isEqualTo(1L);
		assertThat(response.active()).isTrue();
		assertThat(response.accounts()).containsExactly(account);
	}

	@Test
	void customerSearchResponse_carriesAllFields() {
		CustomerSearchResponse response = new CustomerSearchResponse(10L, "Ahmet", "Can", "Yilmaz", "10000000146",
				"000042", "Musteri", "CUST", "5551234567", "Aktif");

		assertThat(response.custId()).isEqualTo(10L);
		assertThat(response.firstName()).isEqualTo("Ahmet");
		assertThat(response.role()).isEqualTo("Musteri");
		assertThat(response.roleShrtCode()).isEqualTo("CUST");
		assertThat(response.status()).isEqualTo("Aktif");
	}

	@Test
	void identityVerificationResponse_ok_returnsVerifiedTrue() {
		IdentityVerificationResponse response = IdentityVerificationResponse.ok();

		assertThat(response.verified()).isTrue();
		assertThat(response.message()).isEqualTo("Identity verified.");
	}

	@Test
	void identityVerificationResponse_carriesCustomValues() {
		IdentityVerificationResponse response = new IdentityVerificationResponse(false, "not verified");

		assertThat(response.verified()).isFalse();
		assertThat(response.message()).isEqualTo("not verified");
	}
}
