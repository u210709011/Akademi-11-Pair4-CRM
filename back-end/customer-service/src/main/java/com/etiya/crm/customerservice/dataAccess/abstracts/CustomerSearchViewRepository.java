package com.etiya.crm.customerservice.dataAccess.abstracts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.etiya.crm.customerservice.entities.concretes.CustomerSearchView;

public interface CustomerSearchViewRepository
		extends JpaRepository<CustomerSearchView, Long>, JpaSpecificationExecutor<CustomerSearchView> {
}
