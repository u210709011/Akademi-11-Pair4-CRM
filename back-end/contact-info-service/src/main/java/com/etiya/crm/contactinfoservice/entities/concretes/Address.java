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
@Table(name = "ADDR")
public class Address extends BaseEntity {

    @Column(nullable = false)
    private Long cityId;

    @Column(nullable = false, length = 200)
    private String streetName;

    @Column(nullable = false, length = 100)
    private String houseName;

    @Column(length = 200)
    private String addrDesc;

    @Column(nullable = false)
    private boolean isPrimary = false;

}
