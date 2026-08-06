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
@Table(name = "prod_ofr_rel")
@Entity
public class ProductOfferingRelation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prod_ofr_rel_id")
    private Long productOfferingRelationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_ofr_id1", nullable = false)
    private ProductOffering productOffering1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_ofr_id2", nullable = false)
    private ProductOffering productOffering2;

    @Column(name = "rel_tp_id", nullable = false)
    private Long relationTypeId; // zorunlu? opsiyonel?

    @Column(name = "qty", nullable = false)
    private Integer qty;

    @Column(name = "is_actv", nullable = false)
    private boolean active;
}
