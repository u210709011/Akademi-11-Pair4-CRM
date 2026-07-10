package com.etiya.crm.contactinfoservice.inbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Kafka ayni event'i birden fazla kez teslim edebilir; islenen event id'leri
 * burada tutulur, tekrar gelen ayni event islenmez (idempotent consumer).
 */
@Entity
@Table(name = "inbox")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inbox {

    @Id
    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @CreationTimestamp
    @Column(name = "processed_at", nullable = false, updatable = false)
    private LocalDateTime processedAt;

}
