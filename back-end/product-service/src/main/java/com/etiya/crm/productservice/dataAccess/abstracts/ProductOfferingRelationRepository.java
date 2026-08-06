package com.etiya.crm.productservice.dataAccess.abstracts;

import com.etiya.crm.productservice.entities.concretes.ProductOfferingRelation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOfferingRelationRepository extends JpaRepository<ProductOfferingRelation, Long> {
}