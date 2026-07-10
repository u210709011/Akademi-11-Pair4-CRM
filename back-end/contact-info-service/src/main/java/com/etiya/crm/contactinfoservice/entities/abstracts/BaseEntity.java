package com.etiya.crm.contactinfoservice.entities.abstracts;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long rowId;

    @Column(nullable = false)
    private Long dataTypeId;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @CreatedDate
    @Column(name = "cdate", nullable = false, updatable = false)
    private Instant cdate;

    @CreatedBy
    @Column(name = "cuser", nullable = false, updatable = false)
    private String cuser;

    @LastModifiedDate
    @Column(name = "udate")
    private Instant udate;

    @LastModifiedBy
    @Column(name = "uuser")
    private String uuser;

}