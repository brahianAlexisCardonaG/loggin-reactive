package com.person.project.infraestructure.adapters.persistenceadapter.personbootcamp;

import com.person.project.infraestructure.adapters.pesistenceadapter.personbootcamp.PersonBootcampPersistenceAdapter;
import com.person.project.infraestructure.adapters.pesistenceadapter.personbootcamp.entity.PersonBootcampEntity;
import com.person.project.infraestructure.adapters.pesistenceadapter.personbootcamp.repository.PersonBootcampRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonBootcampPersistenceAdapterTest {
    @Mock
    private PersonBootcampRepository personBootcampRepository;

    @InjectMocks
    private PersonBootcampPersistenceAdapter adapter;

    //El anotador @Captor de Mockito se utiliza para crear argument captors,
    // que son herramientas que permiten capturar los argumentos que se pasan
    // a métodos de objetos mockeados durante las llamadas, para luego poder
    // hacer aserciones o verificaciones sobre ellos.
    @Captor
    private ArgumentCaptor<List<PersonBootcampEntity>> listCaptor;

    private final Long personId = 1L;
    private final List<Long> bootcampIds = List.of(100L, 200L, 300L);

    private List<PersonBootcampEntity> expectedEntities;

    @BeforeEach
    public void setUp() {
        // Se arma la lista esperada, la cual se genera a partir de los bootcampIds
        expectedEntities = bootcampIds.stream()
                .map(bootId -> new PersonBootcampEntity(null, personId, bootId))
                .collect(Collectors.toList());
    }

    @Test
    public void testSaveRelations_success() {
        // Stub: cuando se invoque saveAll con la lista de entidades, se emite un Flux con dichas entidades.
        when(personBootcampRepository.saveAll(anyList()))
                .thenReturn(Flux.fromIterable(expectedEntities));

        // Invoca el método a probar
        Mono<Void> result = adapter.saveRelations(personId, bootcampIds);

        // Verifica que el Mono finalice correctamente sin emitir ningún valor.
        StepVerifier.create(result)
                .verifyComplete();

        // Verificar que el repositorio se haya llamado con la lista esperada
        verify(personBootcampRepository, times(1)).saveAll(listCaptor.capture());
        List<PersonBootcampEntity> capturedEntities = listCaptor.getValue();
        assertEquals(expectedEntities, capturedEntities);
    }
}
