package com.etiya.crm.productservice.dataAccess.abstracts;

import com.etiya.crm.productservice.entities.concretes.ProductOfferingCharUse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOfferingCharUseRepository extends JpaRepository<ProductOfferingCharUse, Long> {
    List<ProductOfferingCharUse> findByProductOffering_ProductOfferingId(Long productOfferingId);

    boolean existsByProductOffering_ProductOfferingIdAndCharacteristicId(Long productOfferingId, Long characteristicId);

    boolean existsByProductOffering_ProductOfferingIdAndCharacteristicIdAndProductOfferingCharUseIdNot(
            Long productOfferingId, Long characteristicId, Long productOfferingCharUseId);
}