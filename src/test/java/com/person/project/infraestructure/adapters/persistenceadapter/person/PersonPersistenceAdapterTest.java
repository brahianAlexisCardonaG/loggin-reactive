package com.person.project.infraestructure.adapters.persistenceadapter.person;

import com.person.project.domain.enums.RoleUserEnum;
import com.person.project.domain.model.Person;
import com.person.project.infraestructure.adapters.pesistenceadapter.person.PersonPersistenceAdapter;
import com.person.project.infraestructure.adapters.pesistenceadapter.person.entity.PersonEntity;
import com.person.project.infraestructure.adapters.pesistenceadapter.person.mapper.PersonEntityMapper;
import com.person.project.infraestructure.adapters.pesistenceadapter.person.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonPersistenceAdapterTest {
    @Mock
    private PersonRepository repository;

    @Mock
    private PersonEntityMapper personEntityMapper;

    @InjectMocks
    private PersonPersistenceAdapter adapter;

    private Person sampleDomain;
    private PersonEntity sampleEntity;

    @BeforeEach
    void setUp() {
        sampleDomain = new Person(
                1L,
                "John Doe",
                "john.doe@example.com",
                "30",
                "secret",
                RoleUserEnum.USER
        );

        sampleEntity = new PersonEntity();
        sampleEntity.setId(1L);
        sampleEntity.setName("John Doe");
        sampleEntity.setEmail("john.doe@example.com");
        sampleEntity.setAge("30");
        sampleEntity.setPassword("secret");
        sampleEntity.setRoleUserEnum(RoleUserEnum.USER);
    }

    @Test
    public void testFindByEmail_whenExists_returnsMappedPerson() {
        String email = sampleDomain.getEmail();

        when(repository.findByEmail(email)).thenReturn(Mono.just(sampleEntity));
        when(personEntityMapper.toPerson(sampleEntity)).thenReturn(sampleDomain);

        Mono<Person> result = adapter.findByEmail(email);

        StepVerifier.create(result)
                .expectNextMatches(person -> person.equals(sampleDomain))
                .verifyComplete();

        verify(repository, times(1)).findByEmail(email);
        verify(personEntityMapper, times(1)).toPerson(sampleEntity);
    }

    @Test
    public void testSave_whenCalled_persistsAndMapsBack() {
        when(personEntityMapper.toPersonEntity(sampleDomain)).thenReturn(sampleEntity);
        when(repository.save(sampleEntity)).thenReturn(Mono.just(sampleEntity));
        when(personEntityMapper.toPerson(sampleEntity)).thenReturn(sampleDomain);

        Mono<Person> result = adapter.save(sampleDomain);

        StepVerifier.create(result)
                .expectNextMatches(person -> person.equals(sampleDomain))
                .verifyComplete();

        verify(personEntityMapper, times(1)).toPersonEntity(sampleDomain);
        verify(repository, times(1)).save(sampleEntity);
        verify(personEntityMapper, times(1)).toPerson(sampleEntity);
    }

    @Test
    public void testFindById_whenExists_returnsMappedPerson() {
        Long personId = sampleDomain.getId();

        when(repository.findById(personId)).thenReturn(Mono.just(sampleEntity));
        when(personEntityMapper.toPerson(sampleEntity)).thenReturn(sampleDomain);

        Mono<Person> result = adapter.findById(personId);

        StepVerifier.create(result)
                .expectNextMatches(person -> person.equals(sampleDomain))
                .verifyComplete();

        verify(repository, times(1)).findById(personId);
        verify(personEntityMapper, times(1)).toPerson(sampleEntity);
    }
}
