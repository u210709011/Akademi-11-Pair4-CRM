package com.etiya.crm.lookupservice.dataAccess.abstracts;

import com.etiya.crm.lookupservice.entities.concretes.GnlCharVal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GnlCharValRepository extends JpaRepository<GnlCharVal, Long> {
}
