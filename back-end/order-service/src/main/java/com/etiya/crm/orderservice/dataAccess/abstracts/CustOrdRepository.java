package com.etiya.crm.orderservice.dataAccess.abstracts;

import com.etiya.crm.orderservice.entities.concretes.CustOrd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustOrdRepository extends JpaRepository<CustOrd, Long> {
    List<CustOrd> findByCustIdOrderByCdateDesc(Long custId);
}
