package com.etiya.crm.customerservice.outbox;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Debezium EventRouter, bu tabloyu WAL uzerinden izler ve aggregate_type
 * degerine gore "<aggregate_type>-events" topic'ine yayinlar
 * (bkz. infra/debezium/customer-outbox-connector.json).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "outbox")
public class OutboxEvent {

	@Id
	private UUID id;

	@Column(name = "aggregate_type", nullable = false)
	private String aggregateType;

	@Column(name = "aggregate_id", nullable = false)
	private String aggregateId;

	@Column(name = "type", nullable = false)
	private String type;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "payload", nullable = false)
	private String payload;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	public static OutboxEvent of(String aggregateType, String aggregateId, String type, String jsonPayload) {
		return new OutboxEvent(UUID.randomUUID(), aggregateType, aggregateId, type, jsonPayload, Instant.now());
	}
}
