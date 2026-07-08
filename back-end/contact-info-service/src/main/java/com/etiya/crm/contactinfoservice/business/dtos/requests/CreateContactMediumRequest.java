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
public class CreateContactMediumRequest {

    @NotNull
    private Long rowId;

    @NotNull
    private Long dataTypeId;

    @NotBlank
    @Size(max = 100)
    private String cntcData;

    @NotNull
    private Long cntcMediumTypeId;

    @NotNull
    private Long statusId;

}
