package com.etiya.crm.partyservice.business.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * CUSTOMER_SERVICE_CONTRACTS.md SS2 ile birebir: customer-service PartyClient
 * bu alan adlariyla JSON gonderiyor.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateIndividualCommand {

    @NotBlank
    private String firstName;

    private String middleName;

    @NotBlank
    private String lastName;

    @NotNull
    private LocalDate birthDate;

    @NotNull
    private Long genderId;

    private String motherName;

    private String fatherName;

    @NotBlank
    @Pattern(regexp = "\\d{11}", message = "nationalId 11 haneli rakamlardan olusmalidir")
    private String nationalId;
}
