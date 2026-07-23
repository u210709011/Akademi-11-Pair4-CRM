package com.etiya.crm.lookupservice.dataAccess.abstracts;

import com.etiya.crm.lookupservice.entities.concretes.GnlTp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GnlTpRepository extends JpaRepository<GnlTp, Long> {

    Optional<GnlTp> findByEntCodeNameAndShrtCode(String entCodeName, String shrtCode);

    List<GnlTp> findAllByEntCodeName(String entCodeName);
}
