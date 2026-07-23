package com.etiya.crm.productservice.entities.concretes;

import com.etiya.crm.productservice.entities.abstracts.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "prod_ofr")
@Entity
public class ProductOffering extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prod_ofr_id")
    private Long productOfferingId;

    @ManyToOne(fetch = FetchType.LAZY) // birden çok teklik bir tane prod_spec e ait olabilir.
    @JoinColumn(name = "prod_spec_id", nullable = false)
    private ProductSpec productSpec;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "descr", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY) // bir paketin kendi üst paketi olabilir. Süper paket teklifi içinde internet ve tv teklifi var gibi
    @JoinColumn(name = "prnt_ofr_id")
    private ProductOffering parentOffering;

    @Column(name = "st_id", nullable = false)
    private Long statusId; // teklifin durum kimliği

    @Column(name = "prod_ofr_total_prc", nullable = false)
    private BigDecimal totalPrice; // teklifin toplam fiyatı

}
