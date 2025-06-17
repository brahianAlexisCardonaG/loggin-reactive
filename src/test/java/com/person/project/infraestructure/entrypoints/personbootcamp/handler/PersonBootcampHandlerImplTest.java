package com.person.project.infraestructure.entrypoints.personbootcamp.handler;

import com.person.project.domain.api.PersonBootcampServicePort;
import com.person.project.domain.enums.TechnicalMessage;
import com.person.project.domain.model.bootcamp.BootcampPersonList;
import com.person.project.domain.model.bootcamp.PersonListBootcamp;
import com.person.project.domain.model.person.PersonBasic;
import com.person.project.infraestructure.entrypoints.person.response.PersonBasicResponse;
import com.person.project.infraestructure.entrypoints.personbootcamp.dto.PersonBootcampDto;
import com.person.project.infraestructure.entrypoints.personbootcamp.mapper.PersonBootcampMapper;
import com.person.project.infraestructure.entrypoints.personbootcamp.response.ApiBootcampPersonListResponse;
import com.person.project.infraestructure.entrypoints.personbootcamp.response.ApiPersonBootcampListResponse;
import com.person.project.infraestructure.entrypoints.personbootcamp.response.BootcampPersonListResponse;
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

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

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
        RouterFunction<ServerResponse> routerFunction = RouterFunctions
                .route(POST("/person-bootcamp").and(accept(MediaType.APPLICATION_JSON)),
                        personBootcampHandlerImpl::createPersonRelateBootcamp);
        when(applyErrorHandler.applyErrorHandling(any())).thenAnswer(invocation -> invocation.getArgument(0));

        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    @Test
    public void testCreatePersonRelateBootcamp_Success() {
        // Arrange
        PersonBootcampDto dto = new PersonBootcampDto();
        dto.setPersonId(1L);
        dto.setBootcampIds(List.of(100L, 200L));

        when(validationDtoPersonBootcamp.validateDuplicateIds(any(PersonBootcampDto.class)))
                .thenReturn(Mono.just(dto));
        when(validationDtoPersonBootcamp.validateFieldNotNullOrBlank(any(PersonBootcampDto.class)))
                .thenReturn(Mono.just(dto));
        PersonListBootcamp personListBootcamp = PersonListBootcamp.builder()
                .id(1L)
                .name("Test Bootcamp")
                .email("test@example.com")
                .bootcamps(List.of())
                .build();
        when(personBootcampServicePort.saveBootcampCapability(dto.getPersonId(), dto.getBootcampIds()))
                .thenReturn(Mono.just(List.of(personListBootcamp)));

        PersonListBootcampResponse responseDto = new PersonListBootcampResponse();
        when(personBootcampMapper.toPersonListBootcampResponse(any(PersonListBootcamp.class)))
                .thenReturn(responseDto);

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
                    assertEquals(1, apiResponse.getData().size());
                });

        verify(validationDtoPersonBootcamp, times(1)).validateDuplicateIds(any(PersonBootcampDto.class));
        verify(validationDtoPersonBootcamp, times(1)).validateFieldNotNullOrBlank(any(PersonBootcampDto.class));
        verify(personBootcampServicePort, times(1)).saveBootcampCapability(dto.getPersonId(), dto.getBootcampIds());
        verify(personBootcampMapper, times(1)).toPersonListBootcampResponse(any(PersonListBootcamp.class));
    }

    @Test
    public void testCreatePersonRelateBootcamp_ValidationError() {
        PersonBootcampDto dto = new PersonBootcampDto();
        dto.setPersonId(1L);
        dto.setBootcampIds(List.of(100L, 100L));

        RuntimeException exception = new RuntimeException(TechnicalMessage.BOOTCAMP_DUPLICATES_IDS.getMessage());
        when(validationDtoPersonBootcamp.validateDuplicateIds(any(PersonBootcampDto.class)))
                .thenReturn(Mono.error(exception));

        webTestClient.post()
                .uri("/person-bootcamp")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().is5xxServerError();

        verify(validationDtoPersonBootcamp, times(1)).validateDuplicateIds(any(PersonBootcampDto.class));
        verify(validationDtoPersonBootcamp, never()).validateFieldNotNullOrBlank(any(PersonBootcampDto.class));
        verify(personBootcampServicePort, never()).saveBootcampCapability(anyLong(), any());
    }

    @Test
    public void testGetPersonsByBootcampsByIdMaxNumberPerson_success() {
        BootcampPersonList bootcampPersonList = BootcampPersonList.builder()
                .idBootcamp(101L)
                .name("Bootcamp Max")
                .releaseDate(LocalDate.of(2025, 6, 1))
                .duration(45)
                .persons(List.of(
                        PersonBasic.builder().id(1L).name("Alice").email("alice@example.com").build(),
                        PersonBasic.builder().id(2L).name("Bob").email("bob@example.com").build()
                ))
                .build();

        BootcampPersonListResponse mappedResponseDto = BootcampPersonListResponse.builder()
                .idBootcamp(101L)
                .name("Bootcamp Max")
                .releaseDate(LocalDate.of(2025, 6, 1))
                .duration(45)
                .persons(List.of(
                        new PersonBasicResponse(1L, "Alice", "alice@example.com"),
                        new PersonBasicResponse(2L, "Bob", "bob@example.com")
                ))
                .build();


        when(personBootcampServicePort.getPersonsByBootcampsByIdMaxNumberPerson())
                .thenReturn(Mono.just(bootcampPersonList));
        when(personBootcampMapper.toBootcampPersonListResponse(any(BootcampPersonList.class)))
                .thenReturn(mappedResponseDto);

       when(applyErrorHandler.applyErrorHandling(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RouterFunction<ServerResponse> routerFunction = RouterFunctions
                .route(GET("/api/v1/person/bootcamp/get-bootcamp-person").and(accept(MediaType.APPLICATION_JSON)),
                        personBootcampHandlerImpl::getPersonsByBootcampsByIdMaxNumberPerson);

        WebTestClient testClient = WebTestClient.bindToRouterFunction(routerFunction).build();

        testClient.get()
                .uri("/api/v1/person/bootcamp/get-bootcamp-person")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ApiBootcampPersonListResponse.class)
                .value(apiResponse -> {
                    assertNotNull(apiResponse);
                    assertEquals(TechnicalMessage.BOOTCAMP_PERSON_MAX_NUMBER_PERSONS.getCode(), apiResponse.getCode());
                    assertEquals(TechnicalMessage.BOOTCAMP_PERSON_MAX_NUMBER_PERSONS.getMessage(), apiResponse.getMessage());
                    assertNotNull(apiResponse.getDate());
                    BootcampPersonListResponse data = apiResponse.getData();
                    assertNotNull(data);
                    assertEquals(mappedResponseDto.getIdBootcamp(), data.getIdBootcamp());
                    assertEquals(mappedResponseDto.getName(), data.getName());
                    assertEquals(mappedResponseDto.getPersons().size(), data.getPersons().size());
                });
    }

}