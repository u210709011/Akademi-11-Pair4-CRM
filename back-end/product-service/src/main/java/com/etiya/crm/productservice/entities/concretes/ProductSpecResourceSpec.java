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
@Table(name = "prod_spec_rsrc_spec")
@Entity
public class ProductSpecResourceSpec extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prod_spec_rsrc_spec_id")
    private Long productSpecResourceSpecId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_spec_id", nullable = false)
    private ProductSpec productSpec;

    @Column(name = "rsrc_spec_id", nullable = false) // lookuptan gelen
    private Long resourceSpecId;

    @Column(name = "rel_tp_id", nullable = false) // lookuptan gelen
    private Long relationTypeId;

    @Column(name = "sdate", nullable = false)
    private LocalDate startDate;

    @Column(name = "edate")
    private LocalDate endDate;

    @Column(name = "st_id", nullable = false)
    private Long statusId;
}
