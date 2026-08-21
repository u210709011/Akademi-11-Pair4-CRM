package com.etiya.crm.customerservice.mapper;

import java.util.List;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.etiya.crm.customerservice.business.dtos.responses.CustomerAccountResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerSearchResponse;
import com.etiya.crm.customerservice.constants.AccountDefaults;
import com.etiya.crm.customerservice.entities.concretes.Customer;
import com.etiya.crm.customerservice.entities.concretes.CustomerAccount;
import com.etiya.crm.customerservice.entities.concretes.CustomerSearchView;

@Mapper(componentModel = "spring")
/** Entity ve API DTO dönüşümlerini tanımlar. */
public interface CustomerMapper {


	@Mapping(target = "active", expression = "java(isActive(account, activeStatusId))")
	CustomerAccountResponse toResponse(CustomerAccount account, @Context Long activeStatusId);


	@Mapping(target = "roleShrtCode", ignore = true)
	CustomerSearchResponse toResponse(CustomerSearchView searchView);


	@Mapping(target = "accounts", source = "accounts")
	@Mapping(target = "custNo", expression = "java(formatCustNo(customer.getCustId()))")
	/** Müşteriyi hesaplarıyla birlikte API yanıtına dönüştürür. */
	CustomerResponse toResponse(Customer customer, List<CustomerAccount> accounts, @Context Long activeStatusId);


	default boolean isActive(CustomerAccount account, Long activeStatusId) {
		return account.getAcctStId() == null || activeStatusId.equals(account.getAcctStId());
	}

	
	default String formatCustNo(Long custId) {
		return AccountDefaults.formatAccountNo(custId);
	}
}
