package com.etiya.crm.productservice.entities.concretes;

import com.etiya.crm.productservice.entities.abstracts.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "cmpg")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Campaign extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cmpg_id")
    private Long campaignid; // kampanya kimliği

    @Column(name = "name", nullable = false)
    private String name; // kampanya isim

    @Column(name = "descr", nullable = false)
    private String description; // kampanya tanımı

    @Column(name = "cmpg_code", nullable = false)
    private String campaignCode; // kampanya kodu

    @Column(name = "actvt_edate")
    private LocalDate activityEndDate; // aktiflik bitiş tarihi

    @Column(name = "st_id", nullable = false)
    private Long statusId; // kampanya durum kimliği

    @Column(name = "is_penalty", nullable = false)
    private boolean penalty; // cayma bedeli/ cezai şart var mı?
}
