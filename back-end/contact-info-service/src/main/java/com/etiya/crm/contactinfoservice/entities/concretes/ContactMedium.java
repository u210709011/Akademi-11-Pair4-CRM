package com.etiya.crm.contactinfoservice.entities.concretes;

import com.etiya.crm.contactinfoservice.entities.abstracts.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CNTC_MEDIUM")
public class ContactMedium extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String cntcData;

    @Column(nullable = false)
    private Long cntcMediumTypeId;

    @Column(nullable = false)
    private Long statusId;

}
