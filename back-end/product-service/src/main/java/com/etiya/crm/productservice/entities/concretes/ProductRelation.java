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
@Table(name = "prod_rel")
@Entity
public class ProductRelation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prod_rel_id")
    private Long productRelationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_id1", nullable = false)
    private Product product1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_id2", nullable = false)
    private Product product2;

    @Column(name = "rel_tp_id", nullable = false)
    private Long relationTypeId;

    @Column(name = "is_actv", nullable = false)
    private boolean active;
}