package com.etiya.crm.lookupservice.dataAccess.abstracts;

import com.etiya.crm.lookupservice.entities.concretes.GnlSt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GnlStRepository extends JpaRepository<GnlSt, Long> {

    Optional<GnlSt> findByShrtCode(String shrtCode);

    boolean existsByShrtCode(String shrtCode);
}
