package com.person.project.infraestructure.adapters.persistenceadapter.mogodb;

import com.person.project.domain.model.bootcampmongo.BootcampMongo;
import com.person.project.domain.spi.bootcampmongo.BootcampMongoPersistencePort;
import com.person.project.infraestructure.adapters.pesistenceadapter.mongodb.BootcampMongoDbPersistenceAdapter;
import com.person.project.infraestructure.adapters.pesistenceadapter.mongodb.entity.BootcampMongoEntity;
import com.person.project.infraestructure.adapters.pesistenceadapter.mongodb.mapper.BootcampMongoEntityMapper;
import com.person.project.infraestructure.adapters.pesistenceadapter.mongodb.repository.BootcampMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BootcampMongoDbPersistenceAdapterTest {
    @Mock
    private BootcampMongoRepository repository;

    @Mock
    private BootcampMongoEntityMapper bootcampMongoEntityMapper;
    private BootcampMongoPersistencePort adapter;

    @BeforeEach
    public void setup() {
        adapter = new BootcampMongoDbPersistenceAdapter(repository, bootcampMongoEntityMapper);
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

        // Se simula el mapeo: del dominio a la entidad.
        when(bootcampMongoEntityMapper.toEntity(domain)).thenReturn(entity);
        // Se simula que el repositorio guarda la entidad y la retorna.
        when(repository.saveAll(anyList()))
                .thenReturn(Flux.just(entity));

        // Ejecutamos el método updateNumberPersons.
        Mono<Void> result = adapter.updateNumberPersons(Flux.just(domain));

        // Verificamos que se complete sin error.
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    public void testFindBootcampIds_success() {
        // Simula un dummy de entidad que retorna el repositorio.
        BootcampMongoEntity entity = new BootcampMongoEntity(
                1L, 10L, "Test Bootcamp", LocalDate.of(2025, 6, 12),
                30, 0, 0, 1
        );

        // El mapper convertirá la entidad en el objeto de dominio.
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

        // Se configura el repositorio para que retorne la entidad a partir del query.
        when(repository.findByIdBootcampIn(List.of(10L)))
                .thenReturn(Flux.just(entity));
        // Y el mapper para convertir la entidad en dominio.
        when(bootcampMongoEntityMapper.toDomain(entity)).thenReturn(domain);

        // Ejecutamos el método findBootcampIds.
        Mono<List<BootcampMongo>> result = adapter.findBootcampIds(List.of(10L));

        // Verificamos que se retorne una lista con el dummy esperado.
        StepVerifier.create(result)
                .assertNext(list -> {
                    assert list.size() == 1;
                    assert list.get(0).equals(domain);
                })
                .verifyComplete();
    }
}
