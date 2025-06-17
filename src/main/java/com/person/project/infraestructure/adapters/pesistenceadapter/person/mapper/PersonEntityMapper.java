package com.person.project.infraestructure.adapters.pesistenceadapter.person.mapper;

import com.person.project.domain.model.person.Person;
import com.person.project.domain.model.person.PersonBasic;
import com.person.project.infraestructure.adapters.pesistenceadapter.person.entity.PersonEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PersonEntityMapper {
    Person toPerson(PersonEntity personEntity);
    PersonEntity toPersonEntity(Person person);

    PersonBasic toPersonBasic(PersonEntity personEntity);
}
