package com.person.project.domain.usecase.personbootcamp;

import com.person.project.domain.api.PersonBootcampServicePort;
import com.person.project.domain.enums.TechnicalMessage;
import com.person.project.domain.exception.BusinessException;
import com.person.project.domain.model.bootcamp.Bootcamp;
import com.person.project.domain.model.bootcamp.PersonListBootcamp;
import com.person.project.domain.model.bootcamp.BootcampPersonList;
import com.person.project.domain.spi.bootcamp.BootcampWebClientPort;
import com.person.project.domain.spi.bootcampmongo.BootcampMongoPersistencePort;
import com.person.project.domain.spi.person.PersonBootcampPersistencePort;
import com.person.project.domain.spi.person.PersonPersistencePort;
import com.person.project.domain.usecase.personbootcamp.util.ValidationPersonBootcamp;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class PersonBootcampUseCase implements PersonBootcampServicePort {

    private final TransactionalOperator transactionalOperator;
    private final BootcampWebClientPort bootcampWebClientPort;
    private final PersonPersistencePort personPersistencePort;
    private final PersonBootcampPersistencePort personBootcampPersistencePort;
    private final ValidationPersonBootcamp validationPersonBootcamp;
    private final BootcampMongoPersistencePort bootcampMongoPersistencePort;

    @Override
    public Mono<List<PersonListBootcamp>> saveBootcampCapability(Long personId, List<Long> bootcampIds) {
        return transactionalOperator.transactional(
                personPersistencePort.findById(personId)
                        .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.PERSON_NOT_EXIST)))
                        .flatMap(person ->
                                Mono.zip(
                                                personBootcampPersistencePort.findBootcampsByPersonId(personId),
                                                bootcampWebClientPort.getBootcampsByIds(bootcampIds)
                                        ).flatMap(tuple -> {
                                            List<Long> existingBootcampIds = tuple.getT1();
                                            List<Bootcamp> newBootcamps = tuple.getT2();
                                            List<Long> duplicateIds = newBootcamps.stream()
                                                    .map(Bootcamp::getId)
                                                    .filter(existingBootcampIds::contains)
                                                    .toList();
                                            if (!duplicateIds.isEmpty()) {
                                                return Mono.error(new BusinessException(TechnicalMessage.BOOTCAMP_ALREADY_ASSOCIATED));
                                            }
                                            Mono<List<Bootcamp>> existingDetailsMono = existingBootcampIds.isEmpty()
                                                    ? Mono.just(List.of())
                                                    : bootcampWebClientPort.getBootcampsByIds(existingBootcampIds);

                                            return existingDetailsMono.flatMap(existingBootcamps ->
                                                    validationPersonBootcamp.validateNumberBootcamps(existingBootcamps, newBootcamps)
                                                            .then(Mono.defer(() -> {
                                                                List<Bootcamp> combinedBootcamps = new java.util.ArrayList<>();
                                                                combinedBootcamps.addAll(existingBootcamps);
                                                                combinedBootcamps.addAll(newBootcamps);

                                                                return validationPersonBootcamp
                                                                        .validateUniqueReleaseDateAndDuration(combinedBootcamps)
                                                                        .thenReturn(newBootcamps);
                                                            }))
                                            );
                                        })
                                        .flatMap(newBootc ->
                                                bootcampMongoPersistencePort.findBootcampIds(bootcampIds)
                                                        // 2) incrementar numberPersons
                                                        .flatMapMany(list ->
                                                                Flux.fromIterable(list)
                                                                        .map(b -> {
                                                                            b.setNumberPersons((b
                                                                                    .getNumberPersons() == null ? 0 : b
                                                                                    .getNumberPersons()) + 1);
                                                                            return b;
                                                                        })
                                                        )
                                                        .as(bootcampMongoPersistencePort::updateNumberPersons)
                                                        .thenReturn(newBootc)
                                        )
                                        .flatMap(newBootcamps ->
                                                // Guarda la relación persona ↔ bootcamp y, luego, construye PersonListBootcamp.
                                                personBootcampPersistencePort.saveRelations(personId, bootcampIds)
                                                        .then(Mono.defer(() -> {
                                                            PersonListBootcamp result = PersonListBootcamp.builder()
                                                                    .id(person.getId())
                                                                    .name(person.getName())
                                                                    .email(person.getEmail())
                                                                    .bootcamps(newBootcamps)
                                                                    .build();
                                                            return Mono.just(List.of(result));
                                                        }))
                                        )
                        )
        );

    }

    @Override
    public Mono<BootcampPersonList> getPersonsByBootcampsByIdMaxNumberPerson() {
        return bootcampMongoPersistencePort.findBootcampByMaxNumberPersons()
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.BOOTCAMP_NOT_EXIST)))
                .flatMap(bootcamp ->
                        personBootcampPersistencePort.findPersonIdsByBootcampId(bootcamp.getIdBootcamp())
                                .filter(ids -> ids != null && !ids.isEmpty())
                                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.PERSON_NOT_EXIST)))
                                .flatMap(personIds ->
                                        personPersistencePort.findByIds(personIds)
                                                .collectList()
                                                .filter(personList -> personList != null && !personList.isEmpty())
                                                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.PERSON_NOT_EXIST)))
                                                .map(personList ->
                                                        BootcampPersonList.builder()
                                                                .idBootcamp(bootcamp.getIdBootcamp())
                                                                .name(bootcamp.getName())
                                                                .releaseDate(bootcamp.getReleaseDate())
                                                                .duration(bootcamp.getDuration())
                                                                .persons(personList)
                                                                .build()
                                                )
                                )
                );
    }
}