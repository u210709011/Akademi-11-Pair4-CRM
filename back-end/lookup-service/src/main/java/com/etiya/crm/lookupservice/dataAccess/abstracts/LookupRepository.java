package com.etiya.crm.lookupservice.dataAccess.abstracts;

import com.etiya.crm.lookupservice.entities.concretes.Lookup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LookupRepository extends JpaRepository<Lookup, Long> {

    Optional<Lookup> findByGroupCodeAndValueId(String groupCode, Long valueId);

    Optional<Lookup> findByGroupCodeAndCode(String groupCode, String code);

    List<Lookup> findByGroupCode(String groupCode);
}
