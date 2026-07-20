package com.etiya.crm.lookupservice.dataAccess.abstracts;

import com.etiya.crm.lookupservice.entities.concretes.GnlChar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GnlCharRepository extends JpaRepository<GnlChar, Long> {
}
