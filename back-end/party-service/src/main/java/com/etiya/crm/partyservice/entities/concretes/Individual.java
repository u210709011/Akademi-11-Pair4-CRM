package com.etiya.crm.partyservice.entities.concretes;

import com.etiya.crm.partyservice.entities.abstracts.BaseEntity;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ind")
@Entity
public class Individual extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ind_id")
    private Long individualId;

    @Column(name = "frst_name", nullable = false)
    private String firstName;

    @Column(name = "mname")
    private String middleName;

    @Column(name = "lst_name", nullable = false)
    private String lastName;

    @Column(name = "brth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "gndr_id", nullable = false)
    private Long genderId;

    @Column(name = "mthr_name")
    private String motherName;

    @Column(name = "fthr_name")
    private String fatherName;

    @Column(name = "nat_id", nullable = false, unique = true)
    private String nationalId;

    @Column(name = "st_id", nullable = false)
    private Long statusId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;
}
