package com.etiya.crm.contactinfoservice.dataAccess.abstracts;

import com.etiya.crm.contactinfoservice.entities.concretes.ContactMedium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactMediumRepository extends JpaRepository<ContactMedium, Long> {

    Optional<ContactMedium> findByIdAndDeletedFalse(Long id);

    List<ContactMedium> findAllByDeletedFalse();

    List<ContactMedium> findAllByRowIdAndDataTypeIdAndDeletedFalse(Long rowId, Long dataTypeId);

}
