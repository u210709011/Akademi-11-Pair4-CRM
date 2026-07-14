package com.etiya.crm.customerservice.dataAccess.abstracts;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.etiya.crm.customerservice.entities.concretes.CustomerAccount;

public interface CustomerAccountRepository extends JpaRepository<CustomerAccount, Long> {

	List<CustomerAccount> findByCustomer_CustIdAndActiveTrue(Long custId);

	boolean existsByAccountNo(String accountNo);

	boolean existsByAddressIdAndActiveTrue(Long addressId);

	@Modifying
	@Query("update CustomerAccount a set a.active = false where a.customer.custId = :custId")
	void softDeleteByCustId(@Param("custId") Long custId);
}
