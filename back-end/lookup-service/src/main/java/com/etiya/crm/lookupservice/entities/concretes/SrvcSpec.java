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

/** Servis spesifikasyonu tanimi. product-service PROD_SPEC_SRVC_SPEC junction'i uzerinden cross-service referans verir. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "srvc_spec")
public class SrvcSpec extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "srvc_spec_id")
    private Long srvcSpecId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "descr", nullable = false, length = 100)
    private String descr;

    @Column(name = "srvc_code", nullable = false, length = 100)
    private String srvcCode;

    /** Mantiksal referans -> gnl_st.gnl_st_id, gercek FK yok. */
    @Column(name = "st_id", nullable = false)
    private Long stId;
}
