package com.etiya.crm.customerservice.dataAccess.abstracts;

import org.springframework.data.jpa.repository.JpaRepository;

import com.etiya.crm.customerservice.entities.concretes.CustomerDeletionSaga;

public interface CustomerDeletionSagaRepository extends JpaRepository<CustomerDeletionSaga, Long> {
}
