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

/** Genel "tip" grubu tanimi (ornek: PARTY_TYPE, ACCT_TP). Grubun tek tek degerleri TYPE_VALUE'de tutulur. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gnl_tp")
public class GnlTp extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gnl_tp_id")
    private Long gnlTpId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "descr", nullable = false, length = 100)
    private String descr;

    @Column(name = "shrt_code", nullable = false, length = 64, unique = true)
    private String shrtCode;

    @Column(name = "ent_code_name", nullable = false, length = 100)
    private String entCodeName;

    @Column(name = "ent_name", length = 100)
    private String entName;

    @Column(name = "is_actv", nullable = false)
    private boolean active;
}
