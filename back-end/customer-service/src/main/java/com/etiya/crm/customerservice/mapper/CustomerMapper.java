package com.etiya.crm.customerservice.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.etiya.crm.customerservice.business.dtos.responses.CustomerAccountResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerSearchResponse;
import com.etiya.crm.customerservice.entities.concretes.Customer;
import com.etiya.crm.customerservice.entities.concretes.CustomerAccount;
import com.etiya.crm.customerservice.entities.concretes.CustomerSearchView;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

	CustomerAccountResponse toResponse(CustomerAccount account);

	CustomerSearchResponse toResponse(CustomerSearchView searchView);

	/**
	 * CUST_ACCT'in aktif alt kumesi ayrica sorgulanip verildigi icin
	 * Customer.accounts (soft-delete edilenler dahil tum liste) yerine bu
	 * parametre kullanilir.
	 */
	@Mapping(target = "accounts", source = "accounts")
	CustomerResponse toResponse(Customer customer, List<CustomerAccount> accounts);
}
