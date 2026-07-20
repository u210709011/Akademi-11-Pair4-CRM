package com.etiya.crm.lookupservice.mapper;

import com.etiya.crm.lookupservice.business.dtos.requests.CreateGnlStRequest;
import com.etiya.crm.lookupservice.business.dtos.responses.GnlStResponse;
import com.etiya.crm.lookupservice.entities.concretes.GnlSt;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GnlStMapper {

    GnlStResponse toResponse(GnlSt gnlSt);

    GnlSt toEntity(CreateGnlStRequest request);
}
