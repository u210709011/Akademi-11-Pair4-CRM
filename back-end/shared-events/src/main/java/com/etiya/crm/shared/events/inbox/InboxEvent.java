package com.etiya.crm.shared.events.inbox;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Kafka consumer'larda idempotency icin islenen event id'lerinin kaydi.
 * Her servis kendi veritabaninda bu semayla bir "inbox" tablosu tutar
 * (bkz. infra/debezium/outbox-table.sql).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inbox")
public class InboxEvent {

	@Id
	@Column(name = "event_id")
	private UUID eventId;

	@Column(name = "event_type", nullable = false)
	private String eventType;

	@Column(name = "processed_at", nullable = false)
	private Instant processedAt;

	public static InboxEvent of(UUID eventId, String eventType) {
		return new InboxEvent(eventId, eventType, Instant.now());
	}
}
