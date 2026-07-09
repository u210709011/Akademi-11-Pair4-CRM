package com.etiya.crm.lookupservice.entities.abstracts;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

    @Column(name = "cdate", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "cuser", updatable = false)
    private Long createdUser;

    @Column(name = "udate")
    private LocalDateTime updatedDate;

    @Column(name = "uuser")
    private Long updatedUser;
}
