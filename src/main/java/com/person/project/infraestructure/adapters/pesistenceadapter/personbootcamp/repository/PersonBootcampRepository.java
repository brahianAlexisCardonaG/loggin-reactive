package com.person.project.infraestructure.adapters.pesistenceadapter.personbootcamp.repository;

import com.person.project.infraestructure.adapters.pesistenceadapter.personbootcamp.entity.PersonBootcampEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PersonBootcampRepository extends ReactiveCrudRepository<PersonBootcampEntity,Long> {
    Flux<PersonBootcampEntity> findByIdPerson(Long personId);
}
