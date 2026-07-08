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
public class AddressResponse {

    private Long id;
    private Long rowId;
    private Long dataTypeId;
    private Long cityId;
    private String streetName;
    private String houseName;
    private String addrDesc;
    private boolean primary;
    private Instant cdate;
    private Long cuser;
    private Instant udate;
    private Long uuser;

}
