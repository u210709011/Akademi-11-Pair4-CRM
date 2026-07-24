package com.etiya.crm.productservice.dataAccess.abstracts;

import com.etiya.crm.productservice.entities.concretes.ProductSpec;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSpecRepository extends JpaRepository<ProductSpec,Long> {

}
