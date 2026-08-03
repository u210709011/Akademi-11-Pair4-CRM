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
@Table(name = "prod_char_val")
@Entity
public class ProductCharacteristicValue extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prod_char_val_id")
    private Long productCharacteristicValueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_id", nullable = false)
    private Product product;

    @Column(name = "char_id", nullable = false)
    private Long characteristicId;

    @Column(name = "char_val_id")
    private Long characteristicValueId;

    @Column(name = "val")
    private String value;

    @Column(name = "st_id")
    private Long statusId;
}