package com.etiya.crm.shared.events.inbox;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxEventRepository extends JpaRepository<InboxEvent, UUID> {
}
