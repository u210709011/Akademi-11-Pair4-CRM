package com.etiya.crm.productservice.entities.concretes;

import com.etiya.crm.productservice.entities.abstracts.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "prod_spec")
@Entity
public class ProductSpec extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prod_spec_id")
    protected Long productSpecId;  // ürün tanım kimliği

    @Column(name = "name", nullable = false)
    private String name; // ürün isim

    @Column(name = "descr", nullable = false)
    private String descr; // ürün açıklama

    @Column(name = "st_id", nullable = false)
    private Long statusId; // ürün durumu

    @Column(name = "is_dev" , nullable = false)
    private boolean dev; // geliştirme aşamasında mı
}
