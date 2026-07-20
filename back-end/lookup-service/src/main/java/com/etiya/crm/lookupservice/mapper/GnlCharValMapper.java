package com.etiya.crm.lookupservice.mapper;

import com.etiya.crm.lookupservice.business.dtos.requests.CreateGnlCharValRequest;
import com.etiya.crm.lookupservice.business.dtos.responses.GnlCharValResponse;
import com.etiya.crm.lookupservice.entities.concretes.GnlCharVal;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GnlCharValMapper {

    GnlCharValResponse toResponse(GnlCharVal gnlCharVal);

    GnlCharVal toEntity(CreateGnlCharValRequest request);
}
