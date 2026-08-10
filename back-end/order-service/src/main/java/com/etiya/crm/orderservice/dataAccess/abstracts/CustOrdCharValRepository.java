package com.etiya.crm.orderservice.dataAccess.abstracts;

import com.etiya.crm.orderservice.entities.concretes.CustOrdCharVal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustOrdCharValRepository extends JpaRepository<CustOrdCharVal, Long> {

    // saveConfiguration idempotent (replace-all) calisir: her cagrida ilgili item'in onceki
    // karakteristikleri silinip yenileri yazilir.
    void deleteByCustOrdItem_CustOrdItemId(Long custOrdItemId);

    // buildSummary'de siparisin butun item'larinin karakteristiklerini tek sorguda cekip
    // item bazinda gruplamak icin.
    List<CustOrdCharVal> findByCustOrdItem_CustOrd_CustOrdId(Long custOrdId);

    // finishOrder'da provizyon edilen tek bir item'in karakteristiklerini product-service'e
    // islemek icin.
    List<CustOrdCharVal> findByCustOrdItem_CustOrdItemId(Long custOrdItemId);
}
