package com.etiya.crm.customerservice.business.concretes;

import org.springframework.stereotype.Service;

import com.etiya.crm.customerservice.business.abstracts.CustomerFinder;
import com.etiya.crm.customerservice.business.exceptions.CustomerNotFoundException;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerRepository;
import com.etiya.crm.customerservice.entities.concretes.Customer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerFinderImpl implements CustomerFinder {

	private final CustomerRepository customerRepository;

	@Override
	public Customer getActiveCustomerOrThrow(Long custId) {
		return customerRepository.findByCustIdAndActiveTrue(custId)
				.orElseThrow(() -> new CustomerNotFoundException(custId));
	}
}
