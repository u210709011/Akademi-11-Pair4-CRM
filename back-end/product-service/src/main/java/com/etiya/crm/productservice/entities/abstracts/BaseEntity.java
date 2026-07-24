package com.etiya.crm.productservice.entities.abstracts;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {


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
