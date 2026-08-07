package com.etiya.crm.productservice.dataAccess.abstracts;

import com.etiya.crm.productservice.entities.concretes.ProductCatalogOffering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductCatalogOfferingRepository extends JpaRepository<ProductCatalogOffering,Long> {
    List<ProductCatalogOffering> findByProductCatalog_ProductCatalogIdOrderByProductOffering_TotalPriceAsc(Long productCatalogId);
}
