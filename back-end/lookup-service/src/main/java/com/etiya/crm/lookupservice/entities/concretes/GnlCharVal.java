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

import java.time.LocalDate;

/** Bir GNL_CHAR'in alabilecegi degerlerden biri (ornek: charId=Renk icin val="Kirmizi"). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gnl_char_val")
public class GnlCharVal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "char_val_id")
    private Long charValId;

    @Column(name = "char_id", nullable = false)
    private Long charId;

    @Column(name = "is_dflt", nullable = false)
    private boolean dflt;

    @Column(name = "val", length = 100)
    private String val;

    @Column(name = "shrt_code", nullable = false, length = 100)
    private String shrtCode;

    @Column(name = "sdate", nullable = false)
    private LocalDate sdate;

    @Column(name = "edate")
    private LocalDate edate;

    @Column(name = "is_actv", nullable = false)
    private boolean active;
}
