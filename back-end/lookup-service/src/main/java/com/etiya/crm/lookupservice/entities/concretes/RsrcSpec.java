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

/** Kaynak spesifikasyonu tanimi. product-service PROD_SPEC_RSRC_SPEC junction'i uzerinden cross-service referans verir. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rsrc_spec")
public class RsrcSpec extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rsrc_spec_id")
    private Long rsrcSpecId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "descr", nullable = false, length = 100)
    private String descr;

    /** Mantiksal referans -> gnl_st.gnl_st_id, gercek FK yok. */
    @Column(name = "st_id", nullable = false)
    private Long stId;

    @Column(name = "rsrc_code", length = 30)
    private String rsrcCode;
}
