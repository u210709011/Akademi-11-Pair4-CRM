package com.etiya.crm.lookupservice.dataAccess.abstracts;

import com.etiya.crm.lookupservice.entities.concretes.SrvcSpec;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SrvcSpecRepository extends JpaRepository<SrvcSpec, Long> {
}
