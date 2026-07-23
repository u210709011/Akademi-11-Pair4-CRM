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

/** Karakteristik tanimi (ornek: "Renk", "Kapasite"). Alabilecegi degerler GNL_CHAR_VAL'de tutulur. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gnl_char")
public class GnlChar extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "char_id")
    private Long charId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "descr", nullable = false, length = 100)
    private String descr;

    @Column(name = "prvdr_cls", length = 100)
    private String prvdrCls;

    @Column(name = "shrt_code", nullable = false, length = 50)
    private String shrtCode;

    @Column(name = "is_actv", nullable = false)
    private boolean active;
}
