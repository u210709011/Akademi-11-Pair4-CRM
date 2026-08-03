package com.etiya.crm.productservice.dataAccess.abstracts;

import com.etiya.crm.productservice.entities.concretes.ProductCatalogOffering;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCatalogOfferingRepository extends JpaRepository<ProductCatalogOffering,Long> {
}
