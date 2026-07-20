package com.etiya.crm.productservice.entities.concretes;

import com.etiya.crm.productservice.entities.abstracts.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "prod_catal_prod_ofr") //jubction table = ara tablo
@Entity

public class ProductCatalogOffering extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prod_catal_prod_ofr_id")
    private Long productCatalogOfferingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_catal_id", nullable = false)
    private ProductCatalog productCatalog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_ofr_id", nullable = false)
    private ProductOffering productOffering;

    @Column(name = "st_id", nullable = false)
    private Long statusId;
}
