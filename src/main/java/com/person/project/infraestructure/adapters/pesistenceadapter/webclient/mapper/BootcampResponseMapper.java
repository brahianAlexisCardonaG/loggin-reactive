package com.person.project.infraestructure.adapters.pesistenceadapter.webclient.mapper;

import com.person.project.domain.model.bootcamp.Bootcamp;
import com.person.project.infraestructure.adapters.pesistenceadapter.webclient.response.bootcamp.BootcampResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BootcampResponseMapper {

    Bootcamp toDomain(BootcampResponse response);
    List<Bootcamp> toDomainList(List<BootcampResponse> responses);
}
