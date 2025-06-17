package com.person.project.application.config;

import com.person.project.domain.spi.bootcampmongo.BootcampMongoPersistencePort;
import com.person.project.domain.spi.person.PersonPersistencePort;
import com.person.project.domain.util.DomainPersonDetails;
import com.person.project.infraestructure.adapters.pesistenceadapter.mongodb.BootcampMongoDbPersistenceAdapter;
import com.person.project.infraestructure.adapters.pesistenceadapter.mongodb.mapper.BootcampMongoEntityMapper;
import com.person.project.infraestructure.adapters.pesistenceadapter.mongodb.repository.BootcampMongoRepository;
import com.person.project.infraestructure.adapters.pesistenceadapter.person.PersonPersistenceAdapter;
import com.person.project.infraestructure.adapters.pesistenceadapter.person.mapper.PersonEntityMapper;
import com.person.project.infraestructure.adapters.pesistenceadapter.person.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfigPerson {

    private final PersonRepository personRepository;
    private final PersonEntityMapper personEntityMapper;

    private final BootcampMongoRepository bootcampMongoRepository;
    private final BootcampMongoEntityMapper bootcampMongoEntityMapper;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Bean
    public PersonPersistencePort personPersistencePort() {
        return new PersonPersistenceAdapter(personRepository
                ,personEntityMapper);
    }

    @Bean
    public BootcampMongoPersistencePort bootcampMongoPersistencePort() {
        return new BootcampMongoDbPersistenceAdapter(bootcampMongoRepository,
                bootcampMongoEntityMapper,
                reactiveMongoTemplate);
    }



    @Bean
    public ReactiveUserDetailsService personDetailsService() {
        return username -> personRepository.findByEmail(username)
                .map(personEntity -> new DomainPersonDetails(personEntityMapper.toPerson(personEntity)))
                .cast(UserDetails.class) // Aquí aseguramos la conversión explícita
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")));
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService reactiveUserDetailsService,
                                                                       PasswordEncoder passwordEncoder) {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService);

        authenticationManager.setPasswordEncoder(passwordEncoder);

        return authenticationManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ServerLogoutHandler logoutHandler() {
        return new SecurityContextServerLogoutHandler();
    }

}
