package com.person.project.infraestructure.adapters.pesistenceadapter.mongodb;

import com.person.project.domain.model.bootcampmongo.BootcampMongo;
import com.person.project.domain.spi.bootcampmongo.BootcampMongoPersistencePort;
import com.person.project.infraestructure.adapters.pesistenceadapter.mongodb.entity.BootcampMongoEntity;
import com.person.project.infraestructure.adapters.pesistenceadapter.mongodb.mapper.BootcampMongoEntityMapper;
import com.person.project.infraestructure.adapters.pesistenceadapter.mongodb.repository.BootcampMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class BootcampMongoDbPersistenceAdapter implements BootcampMongoPersistencePort {
    private final BootcampMongoRepository repository;
    private final BootcampMongoEntityMapper bootcampMongoEntityMapper;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public Mono<Void> updateNumberPersons(Flux<BootcampMongo> bootcampMongoFlux) {
        return bootcampMongoFlux
                .map(bootcampMongoEntityMapper::toEntity)
                .collectList()
                .flatMapMany(repository::saveAll)
                .then();
    }

    @Override
    public Mono<BootcampMongo> findBootcampByMaxNumberPersons() {
        // Construimos el pipeline de agregación
        Aggregation aggregation = Aggregation.newAggregation(
                // Ordenamos de forma descendente por numberPersons
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "numberPersons")),
                // Limitamos a un único resultado
                Aggregation.limit(1)
        );

        // Ejecutamos la agregación, delegando el procesamiento en Mongo
        return reactiveMongoTemplate.aggregate(aggregation, BootcampMongoEntity.class, BootcampMongoEntity.class)
                .next() // Toma el primer resultado
                .map(bootcampMongoEntity -> BootcampMongo.builder()
                        .id(bootcampMongoEntity.getId())
                        .idBootcamp(bootcampMongoEntity.getIdBootcamp())
                        .name(bootcampMongoEntity.getName())
                        .releaseDate(bootcampMongoEntity.getReleaseDate())
                        .duration(bootcampMongoEntity.getDuration())
                        .numberCapabilities(bootcampMongoEntity.getNumberCapabilities())
                        .numberTechnologies(bootcampMongoEntity.getNumberTechnologies())
                        .numberPersons(bootcampMongoEntity.getNumberPersons())
                        .build()
                );
    }

    @Override
    public Mono<List<BootcampMongo>> findBootcampIds(List<Long> idBootcamp) {
        return repository
                .findByIdBootcampIn(idBootcamp)
                .map(bootcampMongoEntityMapper::toDomain)
                .collectList();
    }
}