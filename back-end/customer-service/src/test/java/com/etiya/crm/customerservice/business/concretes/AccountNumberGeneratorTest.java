package com.etiya.crm.customerservice.business.concretes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.customerservice.business.exceptions.AccountNumberCollisionException;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerAccountRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountNumberGeneratorTest {

	@Mock
	private CustomerAccountRepository customerAccountRepository;

	@InjectMocks
	private AccountNumberGenerator generator;

	@Test
	void generate_returnsZeroPaddedAccountNo_whenNoCollision() {
		when(customerAccountRepository.existsByAccountNo("000042")).thenReturn(false);

		String accountNo = generator.generate(42L);

		assertThat(accountNo).isEqualTo("000042");
	}

	@Test
	void generate_throws_whenAccountNoAlreadyExists() {
		when(customerAccountRepository.existsByAccountNo("000042")).thenReturn(true);

		assertThatThrownBy(() -> generator.generate(42L))
				.isInstanceOf(AccountNumberCollisionException.class);
	}
}
