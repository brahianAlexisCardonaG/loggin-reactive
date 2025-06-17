package com.person.project.infraestructure.adapters.pesistenceadapter.personbootcamp;

import com.person.project.domain.spi.person.PersonBootcampPersistencePort;
import com.person.project.infraestructure.adapters.pesistenceadapter.personbootcamp.entity.PersonBootcampEntity;
import com.person.project.infraestructure.adapters.pesistenceadapter.personbootcamp.repository.PersonBootcampRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class PersonBootcampPersistenceAdapter implements PersonBootcampPersistencePort {
    private final PersonBootcampRepository personBootcampRepository;

    @Override
    public Mono<List<Long>> findBootcampsByPersonId(Long personId) {
        return personBootcampRepository.findByIdPerson(personId)
                .map(PersonBootcampEntity::getIdBootcamp)
                .collectList();
    }

    @Override
    public Mono<Void> saveRelations(Long personId, List<Long> bootcampIds) {
        return Flux.fromIterable(bootcampIds)
                .map(bootId -> new PersonBootcampEntity(null, personId, bootId))
                .collectList()
                .flatMapMany(personBootcampRepository::saveAll)
                .then();
    }

    @Override
    public Mono<List<Long>> findPersonIdsByBootcampId(Long bootcampId) {
        return personBootcampRepository.findByIdBootcamp(bootcampId)
                .map(PersonBootcampEntity::getIdPerson)
                .collectList();
    }
}
