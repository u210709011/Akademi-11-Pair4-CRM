package com.etiya.crm.lookupservice.dataAccess.abstracts;

import com.etiya.crm.lookupservice.entities.concretes.TypeValue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeValueRepository extends JpaRepository<TypeValue, Long> {
}
