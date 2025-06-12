package com.person.project.domain.usecase.person;

import com.person.project.application.config.JwtService;
import com.person.project.domain.model.AuthenticationResponse;
import com.person.project.domain.model.Person;
import com.person.project.domain.spi.PersonPersistencePort;
import com.person.project.domain.util.DomainPersonDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PersonUseCaseTest {
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PersonPersistencePort userPersistencePort;

    @Mock
    private JwtService jwtService;

    @Mock
    private ReactiveAuthenticationManager reactiveAuthenticationManager;

    @InjectMocks
    private PersonUseCase personUseCase;

    private Person inputPerson;
    private Person savedPerson;
    private String encodedPassword;
    private String token;

    @BeforeEach
    public void setUp() {
        encodedPassword = "encodedPassword";
        token = "fakeToken";

        // Configuramos el usuario de entrada (por registrar o autenticar)
        inputPerson = new Person();
        inputPerson.setId(null);
        inputPerson.setName("John Doe");
        inputPerson.setEmail("john@example.com");
        inputPerson.setPassword("plainPassword");
        // Aquí se pueden configurar otros campos, por ejemplo, el RoleUserEnum si se usa.

        // Este objeto representa el usuario persistido (después de registrar o al autenticar).
        savedPerson = new Person();
        savedPerson.setId(1L);
        savedPerson.setName("John Doe");
        savedPerson.setEmail("john@example.com");
        savedPerson.setPassword(encodedPassword);
    }

    @Test
    public void testRegisterPerson() {
        // Arrange
        // Se simula que el encoder transforma "plainPassword" en "encodedPassword"
        when(passwordEncoder.encode("plainPassword")).thenReturn(encodedPassword);
        // Se simula que al guardar el usuario se retorna el usuario persistido con el password codificado
        when(userPersistencePort.save(any(Person.class))).thenReturn(Mono.just(savedPerson));
        // Se simula que JwtService, a partir de DomainPersonDetails, retorna un token
        when(jwtService.generateToken(any(DomainPersonDetails.class))).thenReturn(token);

        // Act
        Mono<AuthenticationResponse> result = personUseCase.registerPerson(inputPerson);

        // Assert: Se debe retornar un AuthenticationResponse con el token "fakeToken"
        StepVerifier.create(result)
                .assertNext(response -> assertEquals(token, response.getAccessToken()))
                .verifyComplete();

        // Verificamos que se llame al encoder y que el usuario se guarde con el password codificado
        verify(passwordEncoder).encode("plainPassword");
        verify(userPersistencePort).save(argThat(person -> encodedPassword.equals(person.getPassword())));
        verify(jwtService).generateToken(any(DomainPersonDetails.class));
    }

    @Test
    public void testAuthenticatePerson() {
        // Arrange
        // Simulamos que la autenticación reactiva tiene éxito y retorna un token de autenticación (no importa el contenido)
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(inputPerson.getEmail(), inputPerson.getPassword());
        when(reactiveAuthenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mono.just(authToken));
        // Simulamos que el persistence port trae el usuario persistido al buscar por email
        when(userPersistencePort.findByEmail(inputPerson.getEmail()))
                .thenReturn(Mono.just(savedPerson));
        // Simulamos la generación del token a partir de los datos del usuario encontrado
        when(jwtService.generateToken(any(DomainPersonDetails.class))).thenReturn(token);

        // Act
        Mono<AuthenticationResponse> result = personUseCase.authenticatePerson(inputPerson);

        // Assert: Se espera que el AuthenticationResponse tenga el token "fakeToken"
        StepVerifier.create(result)
                .assertNext(response -> assertEquals(token, response.getAccessToken()))
                .verifyComplete();

        verify(reactiveAuthenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userPersistencePort).findByEmail(inputPerson.getEmail());
        verify(jwtService).generateToken(any(DomainPersonDetails.class));
    }
}
