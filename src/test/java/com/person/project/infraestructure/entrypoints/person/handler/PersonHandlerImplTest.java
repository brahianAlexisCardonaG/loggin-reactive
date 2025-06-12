package com.person.project.infraestructure.entrypoints.person.handler;

import com.person.project.domain.api.PersonServicePort;
import com.person.project.domain.enums.RoleUserEnum;
import com.person.project.domain.model.AuthenticationResponse;
import com.person.project.domain.model.Person;
import com.person.project.infraestructure.entrypoints.person.dto.AuthenticatePersonDto;
import com.person.project.infraestructure.entrypoints.person.dto.RegisterPersonDto;
import com.person.project.infraestructure.entrypoints.person.mapper.PersonMapper;
import com.person.project.infraestructure.entrypoints.util.ApiResponseBase;
import com.person.project.infraestructure.entrypoints.util.error.ApplyErrorHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@ExtendWith(MockitoExtension.class)
public class PersonHandlerImplTest {
    @InjectMocks
    private PersonHandlerImpl personHandler;

    @Mock
    private PersonServicePort personServicePort;

    @Mock
    private PersonMapper personMapper;

    @Mock
    private ApplyErrorHandler applyErrorHandler;

    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        RouterFunction<ServerResponse> routerFunction = RouterFunctions
                .route(POST("/person/register").and(accept(APPLICATION_JSON)), personHandler::register)
                .andRoute(POST("/person/authenticate").and(accept(APPLICATION_JSON)), personHandler::authenticate);

        when(applyErrorHandler.applyErrorHandling(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    @Test
    public void testRegisterSuccess() {
        RegisterPersonDto registerDto = new RegisterPersonDto();
        registerDto.setName("John Doe");
        registerDto.setEmail("john@example.com");
        registerDto.setAge("30");
        registerDto.setPassword("password");
        registerDto.setRoleUserEnum(RoleUserEnum.USER);

        Person person = new Person();
        when(personMapper.toPersonRegister(any(RegisterPersonDto.class))).thenReturn(person);

        AuthenticationResponse authResponse = new AuthenticationResponse();
        when(personServicePort.registerPerson(person)).thenReturn(Mono.just(authResponse));

        webTestClient.post()
                .uri("/person/register")
                .contentType(APPLICATION_JSON)
                .bodyValue(registerDto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody(ApiResponseBase.class)
                .value(apiResponseBase -> {
                    assertNotNull(apiResponseBase);
                    assertEquals(authResponse, apiResponseBase.getData());
                });

        verify(personMapper).toPersonRegister(any(RegisterPersonDto.class));
        verify(personServicePort).registerPerson(any(Person.class));
    }

    @Test
    public void testAuthenticateSuccess() {
        AuthenticatePersonDto authDto = new AuthenticatePersonDto("john@example.com", "password");
        Person person = new Person();
        when(personMapper.toPersonAuthenticate(any(AuthenticatePersonDto.class))).thenReturn(person);
        AuthenticationResponse authResponse = new AuthenticationResponse();
        when(personServicePort.authenticatePerson(person)).thenReturn(Mono.just(authResponse));

        webTestClient.post()
                .uri("/person/authenticate")
                .contentType(APPLICATION_JSON)
                .bodyValue(authDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ApiResponseBase.class)
                .value(apiResponseBase -> {
                    assertNotNull(apiResponseBase);
                    assertEquals(authResponse, apiResponseBase.getData());
                });

        // Verificamos las invocaciones en el mapper y en el servicio
        verify(personMapper).toPersonAuthenticate(any(AuthenticatePersonDto.class));
        verify(personServicePort).authenticatePerson(any(Person.class));
    }
}
