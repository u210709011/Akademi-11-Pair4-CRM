package com.etiya.crm.orderservice.dataAccess.abstracts;

import com.etiya.crm.orderservice.entities.concretes.CustOrdItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustOrdItemRepository extends JpaRepository<CustOrdItem, Long> {
    List<CustOrdItem> findByCustAcctId(Long custAcctId);

    // BR-03 "Already Active": hesabin PROCESSING/FINISHED durumundaki siparislerindeki item'lar.
    List<CustOrdItem> findByCustAcctIdAndCustOrd_OrdStIdIn(Long custAcctId, List<Long> ordStIds);
}
