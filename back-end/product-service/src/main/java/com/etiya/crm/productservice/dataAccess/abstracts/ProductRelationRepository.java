package com.etiya.crm.productservice.dataAccess.abstracts;

import com.etiya.crm.productservice.entities.concretes.ProductRelation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRelationRepository extends JpaRepository<ProductRelation,Long> {
}
