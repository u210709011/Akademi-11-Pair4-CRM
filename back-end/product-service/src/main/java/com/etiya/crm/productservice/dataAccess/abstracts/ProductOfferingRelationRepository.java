package com.etiya.crm.productservice.dataAccess.abstracts;

import com.etiya.crm.productservice.entities.concretes.ProductOfferingRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOfferingRelationRepository extends JpaRepository<ProductOfferingRelation, Long> {
    List<ProductOfferingRelation> findByProductOffering1_ProductOfferingIdAndActiveTrue(Long productOfferingId);

}