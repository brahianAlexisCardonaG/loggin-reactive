package com.person.project.domain.usecase.personbootcamp;

import com.person.project.domain.api.PersonBootcampServicePort;
import com.person.project.domain.enums.TechnicalMessage;
import com.person.project.domain.exception.BusinessException;
import com.person.project.domain.model.bootcamp.Bootcamp;
import com.person.project.domain.model.bootcamp.PersonListBootcamp;
import com.person.project.domain.spi.BootcampWebClientPort;
import com.person.project.domain.spi.PersonBootcampPersistencePort;
import com.person.project.domain.spi.PersonPersistencePort;
import com.person.project.domain.usecase.personbootcamp.util.ValidationPersonBootcamp;
import com.person.project.infraestructure.adapters.pesistenceadapter.webclient.response.bootcamp.BootcampResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PersonBootcampUseCase implements PersonBootcampServicePort {

    private final TransactionalOperator transactionalOperator;
    private final BootcampWebClientPort bootcampWebClientPort;
    private final PersonPersistencePort personPersistencePort;
    private final PersonBootcampPersistencePort personBootcampPersistencePort;
    private final ValidationPersonBootcamp validationPersonBootcamp;

    @Override
    public Mono<List<PersonListBootcamp>> saveBootcampCapability(Long personId, List<Long> bootcampIds) {
        return transactionalOperator.transactional(
                personPersistencePort.findById(personId)
                        .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.PERSON_NOT_EXIST)))
                        .flatMap(person ->
                                // Obtenemos en paralelo:
                                // a) Bootcamp IDs ya asociados a la persona.
                                // b) Los bootcamps nuevos solicitados vía WebClient.
                                Mono.zip(
                                                personBootcampPersistencePort.findBootcampsByPersonId(personId),
                                                bootcampWebClientPort.getBootcampsByIds(bootcampIds)
                                        ).flatMap(tuple -> {
                                            List<Long> existingBootcampIds = tuple.getT1();
                                            List<BootcampResponse> newBootcampResponses = tuple.getT2().getData();

                                            // Validar que ninguno de los bootcamps nuevos ya esté asociado a la persona.
                                            List<Long> duplicateIds = newBootcampResponses.stream()
                                                    .map(BootcampResponse::getId)
                                                    .filter(existingBootcampIds::contains)
                                                    .collect(Collectors.toList());
                                            if (!duplicateIds.isEmpty()) {
                                                return Mono.error(new BusinessException(TechnicalMessage.BOOTCAMP_ALREADY_ASSOCIATED));
                                            }

                                            // Recuperar la información completa de los bootcamps ya asociados,
                                            // si existen, para poder combinarlos y validar.
                                            Mono<List<BootcampResponse>> existingDetailsMono = existingBootcampIds.isEmpty()
                                                    ? Mono.just(List.of())
                                                    : bootcampWebClientPort.getBootcampsByIds(existingBootcampIds)
                                                    .map(resp -> resp.getData());

                                            return existingDetailsMono.flatMap(existingBootcampResponses -> {
                                                // Validación de cantidad total: existentes + nuevos
                                                return validationPersonBootcamp.validateNumberBootcamps(existingBootcampResponses, newBootcampResponses)
                                                        .then(Mono.defer(() -> {
                                                            // Combinar las listas para validar la unicidad de releaseDate y duration.
                                                            List<BootcampResponse> combinedBootcamps = new java.util.ArrayList<>();
                                                            combinedBootcamps.addAll(existingBootcampResponses);
                                                            combinedBootcamps.addAll(newBootcampResponses);

                                                            return validationPersonBootcamp.validateUniqueReleaseDateAndDuration(combinedBootcamps)
                                                                    .thenReturn(newBootcampResponses);
                                                        }));
                                            });
                                        })
                                        .flatMap(newBootcampResponses ->
                                                // Guarda la relación persona ↔ bootcamp
                                                personBootcampPersistencePort.saveRelations(personId, bootcampIds)
                                                        .then(Mono.defer(() -> {
                                                            // Mapear cada BootcampResponse a la entidad de dominio Bootcamp.
                                                            List<Bootcamp> listaBootcamps = newBootcampResponses.stream()
                                                                    .map(br -> Bootcamp.builder()
                                                                            .id(br.getId())
                                                                            .name(br.getName())
                                                                            .releaseDate(br.getReleaseDate())
                                                                            .duration(br.getDuration())
                                                                            .build())
                                                                    .collect(Collectors.toList());

                                                            // Construir y devolver el PersonListBootcamp.
                                                            PersonListBootcamp resultado = PersonListBootcamp.builder()
                                                                    .id(person.getId())
                                                                    .name(person.getName())
                                                                    .email(person.getEmail())
                                                                    .bootcamps(listaBootcamps)
                                                                    .build();

                                                            return Mono.just(List.of(resultado));
                                                        }))
                                        )
                        )
        );

    }
}
