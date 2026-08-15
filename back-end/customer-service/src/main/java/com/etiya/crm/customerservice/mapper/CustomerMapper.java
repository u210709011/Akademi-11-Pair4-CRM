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
public interface CustomerMapper {

	/**
	 * "active" onceden MapStruct'in isim-uyumu convention'i ile BaseEntity.isActive()'dan
	 * (CustomerAccount icin hep true, bkz. entity javadoc'u) doluyordu - gercek durumu hic
	 * yansitmiyordu. Artik acctStId'nin caller'in @Context olarak verdigi activeStatusId'ye
	 * gore null-safe yorumlanmasindan turetilir (bkz. isActive default metodu).
	 */
	@Mapping(target = "active", expression = "java(isActive(account, activeStatusId))")
	CustomerAccountResponse toResponse(CustomerAccount account, @Context Long activeStatusId);

	@Mapping(target = "roleShrtCode", ignore = true)
	CustomerSearchResponse toResponse(CustomerSearchView searchView);

	/**
	 * CUST_ACCT'in aktif alt kumesi ayrica sorgulanip verildigi icin
	 * Customer.accounts (soft-delete edilenler dahil tum liste) yerine bu
	 * parametre kullanilir. @Context activeStatusId, listedeki her CustomerAccount icin
	 * toResponse(CustomerAccount, Long) cagrisina otomatik iletilir. custNo, front-end'in
	 * kendi tarafinda hesaplamak zorunda kalmamasi icin burada uretilir (bkz. AccountDefaults -
	 * accountNo ile ayni sifirla-soldan-doldurma kurali, tek kaynak burasi).
	 */
	@Mapping(target = "accounts", source = "accounts")
	@Mapping(target = "custNo", expression = "java(formatCustNo(customer.getCustId()))")
	CustomerResponse toResponse(Customer customer, List<CustomerAccount> accounts, @Context Long activeStatusId);

	default boolean isActive(CustomerAccount account, Long activeStatusId) {
		return account.getAcctStId() == null || activeStatusId.equals(account.getAcctStId());
	}

	default String formatCustNo(Long custId) {
		return AccountDefaults.formatAccountNo(custId);
	}
}
