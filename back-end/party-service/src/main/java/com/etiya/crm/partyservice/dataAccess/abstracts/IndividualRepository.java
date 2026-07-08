package com.etiya.crm.partyservice.dataAccess.abstracts;

import com.etiya.crm.partyservice.entities.concretes.Individual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IndividualRepository extends JpaRepository<Individual, Long> {
    boolean existsByNationalId(String nationalId);

    Optional<Individual> findByNationalId(String nationalId);

    Optional<Individual> findByParty_PartyId(Long partyId);
}