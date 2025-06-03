package com.person.project.application.config;

import com.person.project.domain.spi.PersonPersistencePort;
import com.person.project.domain.util.DomainPersonDetails;
import com.person.project.infraestructure.adapters.pesistenceadapter.PersonPersistenceAdapter;
import com.person.project.infraestructure.adapters.pesistenceadapter.mapper.PersonEntityMapper;
import com.person.project.infraestructure.adapters.pesistenceadapter.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
public class ApplicationConfig {

    private final PersonRepository personRespository;

    private final PersonEntityMapper personEntityMapper;

    @Bean
    public PersonPersistencePort technologyPersistencePort() {
        return new PersonPersistenceAdapter(personRespository
                ,personEntityMapper);
    }


    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return username -> personRespository.findByEmail(username)
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
