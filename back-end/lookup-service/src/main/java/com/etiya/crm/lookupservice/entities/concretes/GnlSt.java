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

/** Genel "durum" grubu tanimi (ornek: CUST_STATUS, ACCT_STATUS). Degerleri TYPE_VALUE'de tutulur. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gnl_st")
public class GnlSt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gnl_st_id")
    private Long gnlStId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "descr", nullable = false, length = 100)
    private String descr;

    @Column(name = "shrt_code", nullable = false, length = 15, unique = true)
    private String shrtCode;

    @Column(name = "is_actv", nullable = false)
    private boolean active;

    @Column(name = "ent_code_name", nullable = false, length = 100)
    private String entCodeName;

    @Column(name = "ent_name", length = 100)
    private String entName;
}
