package com.etiya.crm.orderservice.dataAccess.abstracts;

import com.etiya.crm.orderservice.entities.concretes.CustOrdItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustOrdItemRepository extends JpaRepository<CustOrdItem, Long> {
    List<CustOrdItem> findByCustAcctId(Long custAcctId);
}
