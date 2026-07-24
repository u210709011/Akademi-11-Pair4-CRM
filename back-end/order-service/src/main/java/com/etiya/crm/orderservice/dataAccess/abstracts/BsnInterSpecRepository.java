package com.etiya.crm.orderservice.dataAccess.abstracts;

import com.etiya.crm.orderservice.entities.concretes.BsnInterSpec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BsnInterSpecRepository extends JpaRepository<BsnInterSpec, Long> {

    Optional<BsnInterSpec> findByShrtCode(String shrtCode);
}
