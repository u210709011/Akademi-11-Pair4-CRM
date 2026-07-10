package com.etiya.crm.shared.events.outbox;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

/**
 * Ayni transaction icinde OUTBOX'a yazar; Debezium bu tabloyu WAL uzerinden
 * izleyip "<aggregate_type>-events" topic'ine yayinlar (bkz.
 * infra/debezium/*-connector.json). Bu servis Kafka'ya dogrudan yazmaz.
 */
@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

	private final OutboxEventRepository outboxEventRepository;
	private final ObjectMapper objectMapper;

	public void publish(String aggregateType, String aggregateId, String eventType, Object payload) {
		try {
			String json = objectMapper.writeValueAsString(payload);
			outboxEventRepository.save(OutboxEvent.of(aggregateType, aggregateId, eventType, json));
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Failed to serialize outbox payload for event " + eventType, e);
		}
	}
}
