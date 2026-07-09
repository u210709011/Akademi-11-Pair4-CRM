package com.etiya.crm.contactinfoservice.business.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactMediumResponse {

    private Long id;
    private Long rowId;
    private Long dataTypeId;
    private String cntcData;
    private Long cntcMediumTypeId;
    private Long statusId;
    private Instant cdate;
    private String cuser;
    private Instant udate;
    private String uuser;

}
