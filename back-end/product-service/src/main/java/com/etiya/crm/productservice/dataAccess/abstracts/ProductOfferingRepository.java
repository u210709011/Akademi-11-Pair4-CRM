package com.etiya.crm.productservice.dataAccess.abstracts;

import com.etiya.crm.productservice.entities.concretes.ProductOffering;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOfferingRepository extends JpaRepository<ProductOffering,Long> {
}
