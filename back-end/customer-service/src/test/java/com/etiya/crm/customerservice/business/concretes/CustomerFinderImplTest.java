package com.etiya.crm.customerservice.business.concretes;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.customerservice.business.exceptions.CustomerNotFoundException;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerRepository;
import com.etiya.crm.customerservice.entities.concretes.Customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerFinderImplTest {

	@Mock
	private CustomerRepository customerRepository;

	@InjectMocks
	private CustomerFinderImpl finder;

	@Test
	void getActiveCustomerOrThrow_returnsCustomer_whenFound() {
		Customer customer = new Customer();
		customer.setCustId(10L);
		when(customerRepository.findByCustIdAndActiveTrue(10L)).thenReturn(Optional.of(customer));

		Customer result = finder.getActiveCustomerOrThrow(10L);

		assertThat(result).isSameAs(customer);
	}

	@Test
	void getActiveCustomerOrThrow_throws_whenNotFound() {
		when(customerRepository.findByCustIdAndActiveTrue(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> finder.getActiveCustomerOrThrow(99L))
				.isInstanceOf(CustomerNotFoundException.class);
	}
}
