package com.etiya.crm.productservice.entities.concretes;

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
@Table(name = "prod_spec_srvc_spec")
@Entity
public class ProductSpecServiceSpec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prod_spec_srvc_spec_id")
    private Long productSpecServiceSpecId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_spec_id", nullable = false)
    private ProductSpec productSpec;

    @Column(name = "srvc_spec_id", nullable = false)
    private Long serviceSpecId;

    @Column(name = "rel_tp_id", nullable = false)
    private Long relationTypeId;

    @Column(name = "sdate", nullable = false)
    private LocalDate startDate;

    @Column(name = "edate")
    private LocalDate endDate;

    @Column(name = "st_id", nullable = false)
    private Long statusId;
}
