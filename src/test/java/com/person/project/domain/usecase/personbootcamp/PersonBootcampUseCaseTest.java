package com.person.project.domain.usecase.personbootcamp;

import com.person.project.domain.enums.RoleUserEnum;
import com.person.project.domain.model.bootcamp.BootcampPersonList;
import com.person.project.domain.model.person.Person;
import com.person.project.domain.model.bootcamp.Bootcamp;
import com.person.project.domain.model.bootcamp.PersonListBootcamp;
import com.person.project.domain.model.bootcampmongo.BootcampMongo;
import com.person.project.domain.model.person.PersonBasic;
import com.person.project.domain.spi.bootcamp.BootcampWebClientPort;
import com.person.project.domain.spi.bootcampmongo.BootcampMongoPersistencePort;
import com.person.project.domain.spi.person.PersonBootcampPersistencePort;
import com.person.project.domain.spi.person.PersonPersistencePort;
import com.person.project.domain.usecase.personbootcamp.util.ValidationPersonBootcamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PersonBootcampUseCaseTest {
    @Mock
    private TransactionalOperator transactionalOperator;

    @Mock
    private BootcampWebClientPort bootcampWebClientPort;

    @Mock
    private PersonPersistencePort personPersistencePort;

    @Mock
    private PersonBootcampPersistencePort personBootcampPersistencePort;

    @Mock
    private ValidationPersonBootcamp validationPersonBootcamp;

    @Mock
    private BootcampMongoPersistencePort bootcampMongoPersistencePort;

    private PersonBootcampUseCase useCase;

    @BeforeEach
    public void setup() {
        lenient().when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        useCase = new PersonBootcampUseCase(
                transactionalOperator,
                bootcampWebClientPort,
                personPersistencePort,
                personBootcampPersistencePort,
                validationPersonBootcamp,
                bootcampMongoPersistencePort
        );
    }

    @Test
    public void testSaveBootcampCapability_success() {
        Long personId = 1L;
        List<Long> bootcampIds = List.of(10L, 20L);

        // Dummy de la persona existente.
        Person dummyPerson = new Person(
                personId,
                "John Doe",
                "john@example.com",
                "27",
                "1234",
                RoleUserEnum.ADMIN
        );

        // Bootcamp devuelto por el WebClient.
        Bootcamp newBootcamp = Bootcamp.builder()
                .id(100L)
                .name("Bootcamp A")
                .releaseDate(LocalDate.of(2025, 6, 12))
                .duration(30)
                .build();
        List<Bootcamp> newBootcamps = List.of(newBootcamp);

        // Configuración de stubbing para los puertos.
        when(personPersistencePort.findById(personId))
                .thenReturn(Mono.just(dummyPerson));

        // No hay bootcamps asociados previamente.
        when(personBootcampPersistencePort.findBootcampsByPersonId(personId))
                .thenReturn(Mono.just(List.of()));

        // El WebClient retorna los nuevos bootcamps.
        when(bootcampWebClientPort.getBootcampsByIds(bootcampIds))
                .thenReturn(Mono.just(newBootcamps));

        // Las validaciones pasan:
        when(validationPersonBootcamp.validateNumberBootcamps(List.of(), newBootcamps))
                .thenReturn(Mono.just(List.of()));
        when(validationPersonBootcamp.validateUniqueReleaseDateAndDuration(any()))
                .thenReturn(Mono.empty());

        // Guardado de relaciones retorna Mono vacío.
        when(personBootcampPersistencePort.saveRelations(personId, bootcampIds))
                .thenReturn(Mono.empty());

        // --- Configuración para el puerto Mongo ---
        // Simulamos que se encuentran BootcampMongo asociados a los bootcampIds.
        BootcampMongo dummyMongo = BootcampMongo.builder()
                .id(1L)
                .idBootcamp(10L)
                .name("Dummy Mongo Bootcamp")
                .releaseDate(LocalDate.of(2025, 6, 12))
                .duration(30)
                .numberCapabilities(0)
                .numberTechnologies(0)
                .numberPersons(0)
                .build();
        List<BootcampMongo> dummyMongoList = List.of(dummyMongo);

        when(bootcampMongoPersistencePort.findBootcampIds(bootcampIds))
                .thenReturn(Mono.just(dummyMongoList));

        // Se simula la actualización de numberPersons en Mongo.
        when(bootcampMongoPersistencePort.updateNumberPersons(any(Flux.class)))
                .thenReturn(Mono.empty());

        Mono<List<PersonListBootcamp>> result = useCase.saveBootcampCapability(personId, bootcampIds);

        StepVerifier.create(result)
                .assertNext(personListBootcamps -> {
                    // Se espera que la lista tenga un solo elemento.
                    assert personListBootcamps.size() == 1;
                    PersonListBootcamp plb = personListBootcamps.get(0);
                    // Verificamos que los datos de la persona sean correctos.
                    assert plb.getId().equals(dummyPerson.getId());
                    assert plb.getName().equals(dummyPerson.getName());
                    assert plb.getEmail().equals(dummyPerson.getEmail());
                    // Y que los bootcamps sean los nuevos devueltos.
                    assert plb.getBootcamps().equals(newBootcamps);
                })
                .verifyComplete();
    }

    @Test
    void testGetPersonsByBootcampsByIdMaxNumberPerson_success() {
        // Arrange: bootcamp con mayor número de personas
        BootcampMongo maxBootcamp = BootcampMongo.builder()
                .id(1L)
                .idBootcamp(101L)
                .name("Bootcamp Max")
                .releaseDate(LocalDate.of(2025, 6, 1))
                .duration(45)
                .numberPersons(100)
                .build();

        List<Long> personIds = List.of(1L, 2L);

        PersonBasic person1 = PersonBasic.builder()
                .id(1L)
                .name("Alice")
                .email("alice@example.com")
                .build();

        PersonBasic person2 = PersonBasic.builder()
                .id(2L)
                .name("Bob")
                .email("bob@example.com")
                .build();

        List<PersonBasic> persons = List.of(person1, person2);

        // Stubbing
        when(bootcampMongoPersistencePort.findBootcampByMaxNumberPersons())
                .thenReturn(Mono.just(maxBootcamp));

        when(personBootcampPersistencePort.findPersonIdsByBootcampId(101L))
                .thenReturn(Mono.just(personIds));

        when(personPersistencePort.findByIds(personIds))
                .thenReturn(Flux.fromIterable(persons));

        // Act
        Mono<BootcampPersonList> result = useCase.getPersonsByBootcampsByIdMaxNumberPerson();

        // Assert
        StepVerifier.create(result)
                .assertNext(bootcampPersonList -> {
                    assertThat(bootcampPersonList.getIdBootcamp()).isEqualTo(101L);
                    assertThat(bootcampPersonList.getName()).isEqualTo("Bootcamp Max");
                    assertThat(bootcampPersonList.getReleaseDate()).isEqualTo(LocalDate.of(2025, 6, 1));
                    assertThat(bootcampPersonList.getDuration()).isEqualTo(45);

                    List<PersonBasic> personsList = bootcampPersonList.getPersons();
                    assertThat(personsList).isNotNull();
                    assertThat(personsList.size()).isEqualTo(2);

                    // Validación para la primera persona
                    PersonBasic firstPerson = personsList.get(0);
                    assertThat(firstPerson.getId()).isEqualTo(1L);
                    assertThat(firstPerson.getName()).isEqualTo("Alice");
                    assertThat(firstPerson.getEmail()).isEqualTo("alice@example.com");

                    // Validación para la segunda persona
                    PersonBasic secondPerson = personsList.get(1);
                    assertThat(secondPerson.getId()).isEqualTo(2L);
                    assertThat(secondPerson.getName()).isEqualTo("Bob");
                    assertThat(secondPerson.getEmail()).isEqualTo("bob@example.com");
                })
                .verifyComplete();
    }
}