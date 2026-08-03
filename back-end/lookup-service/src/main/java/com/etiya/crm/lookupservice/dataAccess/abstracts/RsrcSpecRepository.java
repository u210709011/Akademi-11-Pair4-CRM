package com.etiya.crm.lookupservice.dataAccess.abstracts;

import com.etiya.crm.lookupservice.entities.concretes.RsrcSpec;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RsrcSpecRepository extends JpaRepository<RsrcSpec, Long> {
}
