package com.etiya.crm.productservice.dataAccess.abstracts;

import com.etiya.crm.productservice.entities.concretes.ProductSpecResourceSpec;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSpecResourceSpecRepository extends JpaRepository<ProductSpecResourceSpec,Long> {
}
