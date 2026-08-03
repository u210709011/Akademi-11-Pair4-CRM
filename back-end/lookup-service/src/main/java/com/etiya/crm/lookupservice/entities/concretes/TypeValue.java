package com.etiya.crm.lookupservice.entities.concretes;

import com.etiya.crm.lookupservice.entities.abstracts.BaseEntity;
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
 * GNL_TP/GNL_ST grubuna ait tek bir deger. tableName ('GNL_TP'/'GNL_ST') + fieldName
 * (o tablodaki satirin id'si) polimorfik referanstir - gercek bir FK yoktur (bkz. V4 migration).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "type_value")
public class TypeValue extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_value_id")
    private Long typeValueId;

    @Column(name = "table_name", nullable = false, length = 40)
    private String tableName;

    @Column(name = "field_name", nullable = false)
    private Long fieldName;

    @Column(name = "description", nullable = false, length = 200)
    private String description;

    @Column(name = "value", length = 50)
    private String value;

    @Column(name = "using_module_name", length = 50)
    private String usingModuleName;
}
