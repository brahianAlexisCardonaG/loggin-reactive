package com.person.project.infraestructure.adapters.pesistenceadapter.mongodb.repository;

import com.person.project.infraestructure.adapters.pesistenceadapter.mongodb.entity.BootcampMongoEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface BootcampMongoRepository extends ReactiveMongoRepository<BootcampMongoEntity, Long> {
    Flux<BootcampMongoEntity> findByIdBootcampIn(List<Long> idBootcamps);
}
