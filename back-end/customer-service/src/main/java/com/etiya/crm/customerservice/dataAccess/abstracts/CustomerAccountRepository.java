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

	/**
	 * Hesap aktifligi artik BaseEntity.active degil acct_st_id (lookup-service GNL_ST/CUST_ACCT
	 * grubu) uzerinden - deletedStatusId (DEL) caller tarafindan cozulup verilir. Derived query
	 * bunu ifade edemedigi icin @Query kullanilir. Null-safe: acct_st_id IS NULL da "silinmemis"
	 * sayilir (yeni acilan/eski satirlar icin varsayilan yorum ACTV'dir).
	 */
	@Query("select a from CustomerAccount a where a.customer.custId = :custId "
			+ "and (a.acctStId is null or a.acctStId <> :deletedStatusId)")
	List<CustomerAccount> findByCustomer_CustIdAndAcctStIdNotDeleted(@Param("custId") Long custId,
			@Param("deletedStatusId") Long deletedStatusId);

	/** FR-009 ACC-009: getAccounts ucu icin sayfalanmis versiyon - ayni filtre. */
	@Query("select a from CustomerAccount a where a.customer.custId = :custId "
			+ "and (a.acctStId is null or a.acctStId <> :deletedStatusId)")
	Page<CustomerAccount> findByCustomer_CustIdAndAcctStIdNotDeleted(@Param("custId") Long custId,
			@Param("deletedStatusId") Long deletedStatusId, Pageable pageable);

	/** FR-010/FR-011 IDOR: hesap gercekten bu musteriye mi ait, silinmemis mi. */
	@Query("select a from CustomerAccount a where a.custAcctId = :custAcctId and a.customer.custId = :custId "
			+ "and (a.acctStId is null or a.acctStId <> :deletedStatusId)")
	Optional<CustomerAccount> findByCustAcctIdAndCustomer_CustIdAndAcctStIdNotDeleted(
			@Param("custAcctId") Long custAcctId, @Param("custId") Long custId,
			@Param("deletedStatusId") Long deletedStatusId);

	boolean existsByAccountNo(String accountNo);

	@Query("select case when count(a) > 0 then true else false end from CustomerAccount a "
			+ "where a.addressId = :addressId and (a.acctStId is null or a.acctStId <> :deletedStatusId)")
	boolean existsByAddressIdAndAcctStIdNotDeleted(@Param("addressId") Long addressId,
			@Param("deletedStatusId") Long deletedStatusId);

	/** Soft-delete artik active=false DEGIL, acct_st_id=DEL yazar (bkz. CustomerAccount.acctStId). */
	@Modifying
	@Query("update CustomerAccount a set a.acctStId = :deletedStatusId where a.customer.custId = :custId")
	void softDeleteByCustId(@Param("custId") Long custId, @Param("deletedStatusId") Long deletedStatusId);
}
