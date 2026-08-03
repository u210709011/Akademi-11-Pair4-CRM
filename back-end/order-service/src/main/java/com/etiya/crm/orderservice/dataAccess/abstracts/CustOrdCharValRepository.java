package com.etiya.crm.orderservice.dataAccess.abstracts;

import com.etiya.crm.orderservice.entities.concretes.CustOrdCharVal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustOrdCharValRepository extends JpaRepository<CustOrdCharVal, Long> {

    // saveConfiguration idempotent (replace-all) calisir: her cagrida onceki karakteristikler silinip yenileri yazilir.
    void deleteByCustOrd_CustOrdId(Long custOrdId);
}
