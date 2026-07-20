package com.etiya.crm.productservice.entities.concretes;

import com.etiya.crm.productservice.entities.abstracts.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cmpg_prod_ofr")
@Entity
public class CampaignOffering extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cmpg_prod_ofr_id")
    private Long campaignOfferingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cmpg_id", nullable = false)
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_ofr_id", nullable = false)
    private ProductOffering productOffering;

    @Column(name = "prod_ofr_name", nullable = false)
    private String productOfferingName;

    @Column(name = "prio")
    private Integer priority;

    @Column(name = "sdate", nullable = false)
    private LocalDate startDate;

    @Column(name = "edate")
    private LocalDate endDate;

    @Column(name = "is_actv", nullable = false)
    private boolean active;
}