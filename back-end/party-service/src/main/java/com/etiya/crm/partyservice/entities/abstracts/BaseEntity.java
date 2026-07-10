package com.etiya.crm.partyservice.entities.abstracts;

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
 * Party domaininde lifecycle durumu (ST_ID) kullanilmiyor.
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