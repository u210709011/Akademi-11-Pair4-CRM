package com.etiya.crm.partyservice.mapper;

import com.etiya.crm.partyservice.business.dtos.requests.CreateIndividualCommand;
import com.etiya.crm.partyservice.entities.concretes.Individual;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IndividualMapper {

    @Mapping(target = "individualId", ignore = true)
    @Mapping(target = "party", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    @Mapping(target = "active", ignore = true)
    Individual toEntity(CreateIndividualCommand command);
}
