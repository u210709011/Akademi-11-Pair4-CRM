package com.etiya.crm.orderservice.entities.abstracts;

import java.time.Instant;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * isActive, soft-delete bayragidir (kayit gorunur/silinmis mi).
 * Order domaininde ayrica lifecycle durumu (ORD_ST_ID / BSN_INTER_ST_ID vb.)
 * lookup-service uzerinden ayri kolonlarla yonetilir, bu bayrakla karistirilmaz.
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @CreatedDate
    @Column(name = "cdate", updatable = false)
    private Instant cdate;

    @CreatedBy
    @Column(name = "cuser", updatable = false)
    private String cuser;

    @LastModifiedDate
    @Column(name = "udate")
    private Instant udate;

    @LastModifiedBy
    @Column(name = "uuser")
    private String uuser;
}
