package com.person.project.infraestructure.entrypoints.personbootcamp.mapper;

import com.person.project.domain.model.bootcamp.BootcampPersonList;
import com.person.project.domain.model.bootcamp.PersonListBootcamp;
import com.person.project.infraestructure.entrypoints.personbootcamp.response.BootcampPersonListResponse;
import com.person.project.infraestructure.entrypoints.personbootcamp.response.PersonListBootcampResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersonBootcampMapper {
    PersonListBootcampResponse toPersonListBootcampResponse(PersonListBootcamp personListBootcamp);
    BootcampPersonListResponse toBootcampPersonListResponse(BootcampPersonList bootcampPersonList);
}
