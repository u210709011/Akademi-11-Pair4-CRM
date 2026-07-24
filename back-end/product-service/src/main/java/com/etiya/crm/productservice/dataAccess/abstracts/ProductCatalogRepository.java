package com.etiya.crm.productservice.dataAccess.abstracts;

import com.etiya.crm.productservice.entities.concretes.ProductCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCatalogRepository extends JpaRepository<ProductCatalog,Long> {
}
