package com.etiya.crm.orderservice.dataAccess.abstracts;

import com.etiya.crm.orderservice.entities.concretes.CustOrdItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustOrdItemRepository extends JpaRepository<CustOrdItem, Long> {
    // Hesabin PROCESSING/FINISHED durumundaki siparislerindeki item'lar - BR-03 "Already Active"
    // ve musteri detay ekranindaki fatura hesabi urun tablosu icin ortak filtre (bkz.
    // CustOrdManager.findActiveItems/getItemsByCustAcctId).
    List<CustOrdItem> findByCustAcctIdAndCustOrd_OrdStIdIn(Long custAcctId, List<Long> ordStIds);
}
