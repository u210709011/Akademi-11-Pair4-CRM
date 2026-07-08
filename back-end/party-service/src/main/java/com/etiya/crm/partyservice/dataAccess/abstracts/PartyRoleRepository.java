package com.etiya.crm.partyservice.dataAccess.abstracts;

import com.etiya.crm.partyservice.entities.concretes.PartyRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyRoleRepository extends JpaRepository<PartyRole, Long> {
}