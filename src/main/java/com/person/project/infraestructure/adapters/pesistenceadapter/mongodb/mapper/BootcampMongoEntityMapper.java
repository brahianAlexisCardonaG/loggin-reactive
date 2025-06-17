package com.person.project.infraestructure.adapters.pesistenceadapter.mongodb.mapper;

import com.person.project.domain.model.bootcampmongo.BootcampMongo;
import com.person.project.infraestructure.adapters.pesistenceadapter.mongodb.entity.BootcampMongoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BootcampMongoEntityMapper {
    BootcampMongoEntity toEntity(BootcampMongo bootcampMongo);
    BootcampMongo toDomain(BootcampMongoEntity bootcampMongoEntity);
}
