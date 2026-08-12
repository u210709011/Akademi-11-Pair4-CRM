package com.etiya.crm.productservice.entities.concretes;

import com.etiya.crm.productservice.entities.abstracts.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Herhangi bir tablonun herhangi bir string alani icin varsayilan-disi (Ingilizce disi) dil
 * karsiligi - lookup-service'teki Translation entity'siyle birebir ayni sekil (bkz. o servisteki
 * yorum). entityName/entityId/fieldName polimorfik referanstir, gercek bir FK yoktur.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "translation")
public class Translation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "translation_id")
    private Long translationId;

    @Column(name = "entity_name", nullable = false, length = 60)
    private String entityName;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "field_name", nullable = false, length = 40)
    private String fieldName;

    @Column(name = "locale", nullable = false, length = 10)
    private String locale;

    @Column(name = "value", nullable = false, length = 200)
    private String value;
}
