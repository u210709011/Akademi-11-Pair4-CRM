package com.etiya.crm.customerservice.dataAccess.abstracts;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.etiya.crm.customerservice.entities.concretes.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	Optional<Customer> findByCustIdAndActiveTrue(Long custId);

	Optional<Customer> findByPartyRoleId(Long partyRoleId);

	boolean existsByPartyRoleId(Long partyRoleId);
}
