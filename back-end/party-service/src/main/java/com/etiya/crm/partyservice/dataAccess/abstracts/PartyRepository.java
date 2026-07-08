package com.etiya.crm.partyservice.dataAccess.abstracts;

import com.etiya.crm.partyservice.entities.concretes.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyRepository extends JpaRepository<Party, Long> {
}