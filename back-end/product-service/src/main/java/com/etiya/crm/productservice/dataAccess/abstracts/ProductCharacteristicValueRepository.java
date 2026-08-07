package com.etiya.crm.productservice.dataAccess.abstracts;

import com.etiya.crm.productservice.entities.concretes.ProductCharacteristicValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductCharacteristicValueRepository extends JpaRepository<ProductCharacteristicValue,Long> {
    List<ProductCharacteristicValue> findByProduct_ProductId(Long productId);
}
