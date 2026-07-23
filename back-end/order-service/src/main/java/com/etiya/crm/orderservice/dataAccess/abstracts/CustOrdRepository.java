package com.etiya.crm.orderservice.dataAccess.abstracts;

import com.etiya.crm.orderservice.entities.concretes.CustOrd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustOrdRepository extends JpaRepository<CustOrd, Long> {
}
