package com.etiya.crm.contactinfoservice.dataAccess.abstracts;

import com.etiya.crm.contactinfoservice.entities.concretes.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findByIdAndActiveTrue(Long id);

    List<Address> findAllByActiveTrue();

    List<Address> findAllByRowIdAndDataTypeIdAndActiveTrue(Long rowId, Long dataTypeId);

}
