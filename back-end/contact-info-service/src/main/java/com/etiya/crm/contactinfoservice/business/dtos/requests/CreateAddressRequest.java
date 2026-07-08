package com.etiya.crm.contactinfoservice.business.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAddressRequest {

    @NotNull
    private Long rowId;

    @NotNull
    private Long dataTypeId;

    @NotNull
    private Long cityId;

    @NotBlank
    @Size(max = 200)
    private String streetName;

    @NotBlank
    @Size(max = 100)
    private String houseName;

    @Size(max = 200)
    private String addrDesc;

    private boolean isPrimary;

    @NotNull
    private Long cuser;

}
