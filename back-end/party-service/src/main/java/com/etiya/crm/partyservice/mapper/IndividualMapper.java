package com.etiya.crm.partyservice.mapper;

import com.etiya.crm.partyservice.business.dtos.requests.CreateIndividualCommand;
import com.etiya.crm.partyservice.entities.concretes.Individual;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IndividualMapper {

    @Mapping(target = "individualId", ignore = true)
    @Mapping(target = "statusId", ignore = true)
    @Mapping(target = "party", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdUser", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "updatedUser", ignore = true)
    Individual toEntity(CreateIndividualCommand command);
}
