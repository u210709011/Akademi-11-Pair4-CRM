package com.etiya.crm.lookupservice.mapper;

import com.etiya.crm.shared.contracts.rsrcspec.CreateRsrcSpecRequest;
import com.etiya.crm.shared.contracts.rsrcspec.RsrcSpecResponse;
import com.etiya.crm.lookupservice.entities.concretes.RsrcSpec;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RsrcSpecMapper {

    RsrcSpecResponse toResponse(RsrcSpec rsrcSpec);

    RsrcSpec toEntity(CreateRsrcSpecRequest request);
}
