package com.etiya.crm.lookupservice.dataAccess.abstracts;

import com.etiya.crm.lookupservice.entities.concretes.GnlTp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GnlTpRepository extends JpaRepository<GnlTp, Long> {

    Optional<GnlTp> findByShrtCode(String shrtCode);

    boolean existsByShrtCode(String shrtCode);
}
