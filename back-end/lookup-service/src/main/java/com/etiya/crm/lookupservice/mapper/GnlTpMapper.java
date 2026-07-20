package com.etiya.crm.lookupservice.mapper;

import com.etiya.crm.lookupservice.business.dtos.requests.CreateGnlTpRequest;
import com.etiya.crm.lookupservice.business.dtos.responses.GnlTpResponse;
import com.etiya.crm.lookupservice.entities.concretes.GnlTp;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GnlTpMapper {

    GnlTpResponse toResponse(GnlTp gnlTp);

    GnlTp toEntity(CreateGnlTpRequest request);
}
