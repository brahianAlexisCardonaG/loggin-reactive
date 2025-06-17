package com.person.project.domain.spi.bootcampmongo;

import com.person.project.domain.model.bootcampmongo.BootcampMongo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BootcampMongoPersistencePort {
    Mono<List<BootcampMongo>> findBootcampIds(List<Long> idBootcamp);
    Mono<Void> updateNumberPersons(Flux<BootcampMongo> bootcampMongoFlux);
    Mono<BootcampMongo> findBootcampByMaxNumberPersons();
}
