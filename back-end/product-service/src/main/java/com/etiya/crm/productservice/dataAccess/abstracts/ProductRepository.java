package com.etiya.crm.productservice.dataAccess.abstracts;

import com.etiya.crm.productservice.entities.concretes.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
}
