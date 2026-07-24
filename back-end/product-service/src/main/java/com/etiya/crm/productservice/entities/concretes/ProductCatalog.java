package com.etiya.crm.productservice.entities.concretes;

import com.etiya.crm.productservice.entities.abstracts.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "prod_catal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCatalog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prod_catal_id")
    private Long productCatalogId; // katalog kimliği

    @Column(name = "name", nullable = false)
    private String name; // katalog ad

    @Column(name = "descr", nullable = false)
    private String description; // katalog açıklama

    @Column(name = "st_id", nullable = false)
    private Long statusId; // durum kimliği

    @Column(name = "shrt_code")
    private String shortCode; // kısa kodu

}
