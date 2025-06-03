package com.person.project.domain.usecase;

import com.person.project.application.config.JwtService;
import com.person.project.domain.api.PersonServicePort;
import com.person.project.domain.model.AuthenticationResponse;
import com.person.project.domain.model.Person;
import com.person.project.domain.spi.PersonPersistencePort;
import com.person.project.domain.util.DomainPersonDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PersonUseCase implements PersonServicePort {
    private final PasswordEncoder passwordEncoder;
    private final PersonPersistencePort userPersistencePort; // Debe exponer métodos reactivos, ej.: Mono<User>
    private final JwtService jwtService;
    private final ReactiveAuthenticationManager reactiveAuthenticationManager;

    @Override
    public Mono<AuthenticationResponse> registerPerson(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));

        // Se guarda el usuario y, una vez persistido, se genera el token.
        return userPersistencePort.save(person)
                .map(savedUser -> {
                    DomainPersonDetails userDetails = new DomainPersonDetails(savedUser);
                    String jwtToken = jwtService.generateToken(userDetails);
                    AuthenticationResponse response = new AuthenticationResponse();
                    response.setAccessToken(jwtToken);
                    return response;
                });
    }

    @Override
    public Mono<AuthenticationResponse> authenticatePerson(Person user) {
        // Primero se autentica de forma reactiva
        return reactiveAuthenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()))
                // Luego se busca el usuario en la base de datos de forma reactiva
                .flatMap(authentication -> userPersistencePort.findByEmail(user.getEmail()))
                .map(foundUser -> {
                    DomainPersonDetails userDetails = new DomainPersonDetails(foundUser);
                    String jwtToken = jwtService.generateToken(userDetails);
                    AuthenticationResponse response = new AuthenticationResponse();
                    response.setAccessToken(jwtToken);
                    return response;
                });
    }
}
