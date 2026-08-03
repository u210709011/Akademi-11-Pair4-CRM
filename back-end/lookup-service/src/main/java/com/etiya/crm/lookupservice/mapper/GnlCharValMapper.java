package com.etiya.crm.lookupservice.mapper;

import com.etiya.crm.shared.contracts.gnlcharval.CreateGnlCharValRequest;
import com.etiya.crm.shared.contracts.gnlcharval.GnlCharValResponse;
import com.etiya.crm.lookupservice.entities.concretes.GnlCharVal;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GnlCharValMapper {

    GnlCharValResponse toResponse(GnlCharVal gnlCharVal);

    GnlCharVal toEntity(CreateGnlCharValRequest request);
}
