package com.etiya.crm.customerservice.dataAccess.abstracts;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.etiya.crm.customerservice.inbox.InboxEvent;

public interface InboxEventRepository extends JpaRepository<InboxEvent, UUID> {
}
