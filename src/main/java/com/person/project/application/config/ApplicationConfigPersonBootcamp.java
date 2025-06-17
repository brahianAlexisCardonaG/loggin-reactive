package com.person.project.application.config;

import com.person.project.domain.api.PersonBootcampServicePort;
import com.person.project.domain.spi.bootcamp.BootcampWebClientPort;
import com.person.project.domain.spi.bootcampmongo.BootcampMongoPersistencePort;
import com.person.project.domain.spi.person.PersonBootcampPersistencePort;
import com.person.project.domain.spi.person.PersonPersistencePort;
import com.person.project.domain.usecase.personbootcamp.PersonBootcampUseCase;
import com.person.project.domain.usecase.personbootcamp.util.ValidationPersonBootcamp;
import com.person.project.infraestructure.adapters.pesistenceadapter.personbootcamp.PersonBootcampPersistenceAdapter;
import com.person.project.infraestructure.adapters.pesistenceadapter.personbootcamp.repository.PersonBootcampRepository;
import com.person.project.infraestructure.entrypoints.personbootcamp.mapper.PersonBootcampMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.reactive.TransactionalOperator;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfigPersonBootcamp {
    private final PersonBootcampRepository personBootcampRepository;
    private final PersonBootcampMapper personBootcampMapper;

    @Bean
    public PersonBootcampPersistencePort personBootcampPersistencePort() {
        return new PersonBootcampPersistenceAdapter(personBootcampRepository);
    }

    @Bean
    public PersonBootcampServicePort personBootcampServicePort(TransactionalOperator transactionalOperator,
                                                               BootcampWebClientPort bootcampWebClientPort,
                                                               PersonPersistencePort personPersistencePort,
                                                               PersonBootcampPersistencePort personBootcampPersistencePort,
                                                               ValidationPersonBootcamp validationPersonBootcamp,
                                                               BootcampMongoPersistencePort bootcampMongoPersistencePort) {
        return new PersonBootcampUseCase(transactionalOperator,
                                         bootcampWebClientPort,
                                         personPersistencePort,
                                         personBootcampPersistencePort,
                                         validationPersonBootcamp,
                                         bootcampMongoPersistencePort
                );
    }
}
