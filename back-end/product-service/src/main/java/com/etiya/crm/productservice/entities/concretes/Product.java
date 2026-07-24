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
@Table(name = "prod")
@Entity
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prod_id")
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prnt_prod_id")
    private Product parentProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_ofr_id", nullable = false)
    private ProductOffering productOffering;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_spec_id", nullable = false)
    private ProductSpec productSpec;

    @Column(name = "name")
    private String name;

    @Column(name = "descr")
    private String descr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cmpg_id")
    private Campaign campaign;

    @Column(name = "st_id", nullable = false)
    private Long statusId;
}