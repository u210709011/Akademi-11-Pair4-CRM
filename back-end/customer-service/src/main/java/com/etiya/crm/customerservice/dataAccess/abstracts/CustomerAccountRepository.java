package com.etiya.crm.customerservice.dataAccess.abstracts;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.etiya.crm.customerservice.entities.concretes.CustomerAccount;

public interface CustomerAccountRepository extends JpaRepository<CustomerAccount, Long> {

	List<CustomerAccount> findByCustomer_CustIdAndActiveTrue(Long custId);

	/** FR-009 ACC-009: getAccounts ucu icin sayfalanmis versiyon. */
	Page<CustomerAccount> findByCustomer_CustIdAndActiveTrue(Long custId, Pageable pageable);

	/** FR-010/FR-011 IDOR: hesap gercekten bu musteriye mi ait. */
	Optional<CustomerAccount> findByCustAcctIdAndCustomer_CustIdAndActiveTrue(Long custAcctId, Long custId);

	boolean existsByAccountNo(String accountNo);

	boolean existsByAddressIdAndActiveTrue(Long addressId);

	@Modifying
	@Query("update CustomerAccount a set a.active = false where a.customer.custId = :custId")
	void softDeleteByCustId(@Param("custId") Long custId);
}
