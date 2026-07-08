package com.etiya.crm.contactinfoservice.dataAccess.abstracts;

import com.etiya.crm.contactinfoservice.entities.concretes.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findByIdAndDeletedFalse(Long id);

    List<Address> findAllByDeletedFalse();

    List<Address> findAllByRowIdAndDataTypeIdAndDeletedFalse(Long rowId, Long dataTypeId);

}
