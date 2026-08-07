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
@Table(name = "prod_ofr_char_use")
@Entity
public class ProductOfferingCharUse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prod_ofr_char_use_id")
    private Long productOfferingCharUseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_ofr_id", nullable = false)
    private ProductOffering productOffering;

    @Column(name = "char_id", nullable = false)
    private Long characteristicId;

    @Column(name = "is_mandatory", nullable = false)
    private boolean mandatory;

    @Column(name = "is_actv", nullable = false)
    private boolean active;
}