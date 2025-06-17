package com.person.project.domain.spi.person;

import reactor.core.publisher.Mono;

import java.util.List;

public interface PersonBootcampPersistencePort {
    Mono<List<Long>> findBootcampsByPersonId(Long personId);
    Mono<Void> saveRelations(Long personId, List<Long> bootcampIds);
    Mono<List<Long>> findPersonIdsByBootcampId(Long bootcampId);
}
