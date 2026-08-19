package com.etiya.crm.customerservice.mapper;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.etiya.crm.customerservice.business.dtos.responses.CustomerAccountResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerSearchResponse;
import com.etiya.crm.customerservice.entities.concretes.Customer;
import com.etiya.crm.customerservice.entities.concretes.CustomerAccount;
import com.etiya.crm.customerservice.entities.concretes.CustomerSearchView;

import static org.assertj.core.api.Assertions.assertThat;

/** MapStruct'in ureeittigi CustomerMapperImpl'i (annotation processing sirasinda uretilir) dogrular. */
class CustomerMapperTest {

	private final CustomerMapper mapper = new CustomerMapperImpl();

	@Test
	void toAccountResponse_active_whenAcctStIdIsNull() {
		CustomerAccount account = new CustomerAccount();
		account.setCustAcctId(1L);
		account.setAccountNo("000001");
		account.setAcctStId(null);

		CustomerAccountResponse response = mapper.toResponse(account, 601L);

		assertThat(response.active()).isTrue();
	}

	@Test
	void toAccountResponse_active_whenAcctStIdMatchesActiveStatus() {
		CustomerAccount account = new CustomerAccount();
		account.setCustAcctId(1L);
		account.setAcctStId(601L);

		CustomerAccountResponse response = mapper.toResponse(account, 601L);

		assertThat(response.active()).isTrue();
	}

	@Test
	void toAccountResponse_inactive_whenAcctStIdDiffersFromActiveStatus() {
		CustomerAccount account = new CustomerAccount();
		account.setCustAcctId(1L);
		account.setAcctStId(602L);

		CustomerAccountResponse response = mapper.toResponse(account, 601L);

		assertThat(response.active()).isFalse();
	}

	@Test
	void toSearchResponse_ignoresRoleShrtCode() {
		CustomerSearchView view = new CustomerSearchView();
		view.setCustId(10L);
		view.setFirstName("Ahmet");
		view.setLastName("Yilmaz");
		view.setRole("Musteri");

		CustomerSearchResponse response = mapper.toResponse(view);

		assertThat(response.custId()).isEqualTo(10L);
		assertThat(response.firstName()).isEqualTo("Ahmet");
		assertThat(response.role()).isEqualTo("Musteri");
		assertThat(response.roleShrtCode()).isNull();
	}

	@Test
	void toCustomerResponse_formatsCustNo_andMapsAccounts() {
		Customer customer = new Customer();
		customer.setCustId(42L);
		customer.setPartyRoleId(100L);
		customer.setCustTpId(1L);

		CustomerAccount account = new CustomerAccount();
		account.setCustAcctId(1L);
		account.setAcctStId(601L);

		CustomerResponse response = mapper.toResponse(customer, List.of(account), 601L);

		assertThat(response.custId()).isEqualTo(42L);
		assertThat(response.custNo()).isEqualTo("000042");
		assertThat(response.accounts()).hasSize(1);
		assertThat(response.accounts().get(0).active()).isTrue();
	}
}
