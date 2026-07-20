package com.etiya.crm.lookupservice.mapper;

import com.etiya.crm.lookupservice.business.dtos.requests.CreateGnlCharRequest;
import com.etiya.crm.lookupservice.business.dtos.responses.GnlCharResponse;
import com.etiya.crm.lookupservice.entities.concretes.GnlChar;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GnlCharMapper {

    GnlCharResponse toResponse(GnlChar gnlChar);

    GnlChar toEntity(CreateGnlCharRequest request);
}
