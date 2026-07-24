package com.etiya.crm.lookupservice.dataAccess.abstracts;

import com.etiya.crm.lookupservice.entities.concretes.TypeValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeValueRepository extends JpaRepository<TypeValue, Long> {

    /** table_name benzersizdir (uq_type_value_table_name) - her is tablosunun tek bir tip etiketi olur. */
    Optional<TypeValue> findByTableName(String tableName);
}
