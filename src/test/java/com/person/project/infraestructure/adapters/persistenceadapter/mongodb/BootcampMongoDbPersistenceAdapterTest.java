package com.person.project.infraestructure.adapters.persistenceadapter.mongodb;

import com.person.project.domain.model.bootcampmongo.BootcampMongo;
import com.person.project.domain.spi.bootcampmongo.BootcampMongoPersistencePort;
import com.person.project.infraestructure.adapters.pesistenceadapter.mongodb.BootcampMongoDbPersistenceAdapter;
import com.person.project.infraestructure.adapters.pesistenceadapter.mongodb.entity.BootcampMongoEntity;
import com.person.project.infraestructure.adapters.pesistenceadapter.mongodb.mapper.BootcampMongoEntityMapper;
import com.person.project.infraestructure.adapters.pesistenceadapter.mongodb.repository.BootcampMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BootcampMongoDbPersistenceAdapterTest {
    @Mock
    private BootcampMongoRepository repository;

    @Mock
    private BootcampMongoEntityMapper bootcampMongoEntityMapper;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    private BootcampMongoPersistencePort adapter;

    @BeforeEach
    public void setup() {
        adapter = new BootcampMongoDbPersistenceAdapter(repository, bootcampMongoEntityMapper, reactiveMongoTemplate);
    }

    @Test
    public void testUpdateNumberPersons_success() {
        BootcampMongo domain = BootcampMongo.builder()
                .id(1L)
                .idBootcamp(10L)
                .name("Test Bootcamp")
                .releaseDate(LocalDate.of(2025, 6, 12))
                .duration(30)
                .numberCapabilities(0)
                .numberTechnologies(0)
                .numberPersons(1)
                .build();

        // Se define el dummy de la entidad.
        BootcampMongoEntity entity = new BootcampMongoEntity(
                1L, 10L, "Test Bootcamp", LocalDate.of(2025, 6, 12),
                30, 0, 0, 1
        );
        when(bootcampMongoEntityMapper.toEntity(domain)).thenReturn(entity);
        when(repository.saveAll(anyList()))
                .thenReturn(Flux.just(entity));
        Mono<Void> result = adapter.updateNumberPersons(Flux.just(domain));
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    public void testFindBootcampIds_success() {
        BootcampMongoEntity entity = new BootcampMongoEntity(
                1L, 10L, "Test Bootcamp", LocalDate.of(2025, 6, 12),
                30, 0, 0, 1
        );
        BootcampMongo domain = BootcampMongo.builder()
                .id(1L)
                .idBootcamp(10L)
                .name("Test Bootcamp")
                .releaseDate(LocalDate.of(2025, 6, 12))
                .duration(30)
                .numberCapabilities(0)
                .numberTechnologies(0)
                .numberPersons(1)
                .build();
        when(repository.findByIdBootcampIn(List.of(10L)))
                .thenReturn(Flux.just(entity));
        when(bootcampMongoEntityMapper.toDomain(entity)).thenReturn(domain);
        Mono<List<BootcampMongo>> result = adapter.findBootcampIds(List.of(10L));
        StepVerifier.create(result)
                .assertNext(list -> {
                    assert list.size() == 1;
                    assert list.get(0).equals(domain);
                })
                .verifyComplete();
    }

    @Test
    public void testFindBootcampByMaxNumberPersons_success() {
        // Arrange – simulamos entidad y dominio
        BootcampMongoEntity entity = new BootcampMongoEntity(
                1L, 10L, "Max Bootcamp", LocalDate.of(2025, 6, 12),
                30, 5, 8, 20
        );

        BootcampMongo expectedDomain = BootcampMongo.builder()
                .id(1L)
                .idBootcamp(10L)
                .name("Max Bootcamp")
                .releaseDate(LocalDate.of(2025, 6, 12))
                .duration(30)
                .numberCapabilities(5)
                .numberTechnologies(8)
                .numberPersons(20)
                .build();

        // Simulamos la agregación de Mongo que retorna un Flux con un único resultado
        when(reactiveMongoTemplate.aggregate(
                any(Aggregation.class),
                any(Class.class),
                any(Class.class)
        )).thenReturn(Flux.just(entity));

        // Act
        Mono<BootcampMongo> result = adapter.findBootcampByMaxNumberPersons();

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(actual -> actual.equals(expectedDomain))
                .verifyComplete();
    }

}
