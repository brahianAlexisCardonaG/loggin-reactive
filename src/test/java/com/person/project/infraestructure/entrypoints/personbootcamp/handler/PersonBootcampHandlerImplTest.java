package com.person.project.infraestructure.entrypoints.personbootcamp.handler;

import com.person.project.domain.api.PersonBootcampServicePort;
import com.person.project.domain.enums.TechnicalMessage;
import com.person.project.domain.model.bootcamp.PersonListBootcamp;
import com.person.project.infraestructure.entrypoints.personbootcamp.dto.PersonBootcampDto;
import com.person.project.infraestructure.entrypoints.personbootcamp.mapper.PersonBootcampMapper;
import com.person.project.infraestructure.entrypoints.personbootcamp.response.ApiPersonBootcampListResponse;
import com.person.project.infraestructure.entrypoints.personbootcamp.response.PersonListBootcampResponse;
import com.person.project.infraestructure.entrypoints.personbootcamp.validation.ValidationDtoPersonBootcamp;
import com.person.project.infraestructure.entrypoints.util.error.ApplyErrorHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@ExtendWith(MockitoExtension.class)
public class PersonBootcampHandlerImplTest {
    @InjectMocks
    private PersonBootcampHandlerImpl personBootcampHandlerImpl;

    @Mock
    private PersonBootcampServicePort personBootcampServicePort;

    @Mock
    private ValidationDtoPersonBootcamp validationDtoPersonBootcamp;

    @Mock
    private PersonBootcampMapper personBootcampMapper;

    @Mock
    private ApplyErrorHandler applyErrorHandler;

    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        // Configuramos el RouterFunction para exponer el endpoint del handler
        RouterFunction<ServerResponse> routerFunction = RouterFunctions
                .route(POST("/person-bootcamp").and(accept(MediaType.APPLICATION_JSON)),
                        personBootcampHandlerImpl::createPersonRelateBootcamp);

        // Para los tests se simula que el ApplyErrorHandler no modifica el flujo,
        // devolviendo el mismo Mono recibido.
        when(applyErrorHandler.applyErrorHandling(any())).thenAnswer(invocation -> invocation.getArgument(0));

        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    @Test
    public void testCreatePersonRelateBootcamp_Success() {
        // Arrange
        PersonBootcampDto dto = new PersonBootcampDto();
        dto.setPersonId(1L);
        dto.setBootcampIds(List.of(100L, 200L));

        // Simulamos que las validaciones pasan devolviendo el DTO sin modificar.
        when(validationDtoPersonBootcamp.validateDuplicateIds(any(PersonBootcampDto.class)))
                .thenReturn(Mono.just(dto));
        when(validationDtoPersonBootcamp.validateFieldNotNullOrBlank(any(PersonBootcampDto.class)))
                .thenReturn(Mono.just(dto));

        // Creamos un objeto dummy para representar la entidad de dominio
        PersonListBootcamp personListBootcamp = PersonListBootcamp.builder()
                .id(1L)
                .name("Test Bootcamp")
                .email("test@example.com")
                .bootcamps(List.of())
                .build();
        when(personBootcampServicePort.saveBootcampCapability(dto.getPersonId(), dto.getBootcampIds()))
                .thenReturn(Mono.just(List.of(personListBootcamp)));

        // Simulamos el mapeo de la entidad a la respuesta esperada
        PersonListBootcampResponse responseDto = new PersonListBootcampResponse();
        when(personBootcampMapper.toPersonListBootcampResponse(any(PersonListBootcamp.class)))
                .thenReturn(responseDto);

        // Act & Assert
        webTestClient.post()
                .uri("/person-bootcamp")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated() // Se espera un 201 CREATED
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ApiPersonBootcampListResponse.class)
                .value(apiResponse -> {
                    // Se validan algunos datos de la respuesta
                    assertNotNull(apiResponse);
                    assertEquals(TechnicalMessage.PERSON_BOOTCAMP_CREATED.getMessage(), apiResponse.getCode());
                    assertEquals(TechnicalMessage.PERSON_BOOTCAMP_CREATED.getMessage(), apiResponse.getMessage());
                    assertNotNull(apiResponse.getDate());
                    assertNotNull(apiResponse.getData());
                    // Se espera que la lista mapeada tenga un elemento
                    assertEquals(1, apiResponse.getData().size());
                });

        verify(validationDtoPersonBootcamp, times(1)).validateDuplicateIds(any(PersonBootcampDto.class));
        verify(validationDtoPersonBootcamp, times(1)).validateFieldNotNullOrBlank(any(PersonBootcampDto.class));
        verify(personBootcampServicePort, times(1)).saveBootcampCapability(dto.getPersonId(), dto.getBootcampIds());
        verify(personBootcampMapper, times(1)).toPersonListBootcampResponse(any(PersonListBootcamp.class));
    }

    @Test
    public void testCreatePersonRelateBootcamp_ValidationError() {
        // Arrange: Creamos un DTO con bootcampIds duplicados para provocar un error de validación.
        PersonBootcampDto dto = new PersonBootcampDto();
        dto.setPersonId(1L);
        dto.setBootcampIds(List.of(100L, 100L));

        RuntimeException exception = new RuntimeException(TechnicalMessage.BOOTCAMP_DUPLICATES_IDS.getMessage());
        when(validationDtoPersonBootcamp.validateDuplicateIds(any(PersonBootcampDto.class)))
                .thenReturn(Mono.error(exception));

        // Act & Assert: Se espera que la ruta retorne un error (status 5xx en este ejemplo; ajústalo según tu lógica de errores).
        webTestClient.post()
                .uri("/person-bootcamp")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().is5xxServerError();

        // Verificamos que solo se invoque la validación inicial y que no continúe el proceso
        verify(validationDtoPersonBootcamp, times(1)).validateDuplicateIds(any(PersonBootcampDto.class));
        verify(validationDtoPersonBootcamp, never()).validateFieldNotNullOrBlank(any(PersonBootcampDto.class));
        verify(personBootcampServicePort, never()).saveBootcampCapability(anyLong(), any());
    }
}
