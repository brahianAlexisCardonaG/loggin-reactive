package com.person.project.infraestructure.adapters.pesistenceadapter.repository;

import com.person.project.infraestructure.adapters.pesistenceadapter.entity.PersonEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface PersonRepository extends ReactiveCrudRepository<PersonEntity, Long> {
   Mono<PersonEntity> findByEmail(String email);
}
